package com.djt.hvac.domain.model.common.dsl.pointmap;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class PointMapExpression {

  private final Expression expression;

  public static PointMapExpression parse(String expression) {
    return Parser.parse(expression);
  }

  PointMapExpression(Expression expression) {
    this.expression = requireNonNull(expression, "expression cannot be null");
  }

  void accept(PointMapExpressionVisitor visitor) {
    expression.accept(visitor);
  }

  public String toSql(String delimiter) {
    requireNonNull(delimiter, "delimiter cannot be null");
    checkArgument(delimiter.length() == 1, "expected a one-character delimiter");
    checkArgument(LexicalAnalyzer.isValidOpenTsdbCharacter(delimiter.charAt(0)),
        "expected a valid delimiter");
    return toSql(delimiter, Maps.newHashMap());
  }

  public String toSql(String delimiter, Map<NodeType, List<String>> variableSubstitutions) {
    requireNonNull(delimiter, "delimiter cannot be null");
    checkArgument(delimiter.length() == 1, "expected a one-character delimiter");
    checkArgument(LexicalAnalyzer.isValidOpenTsdbCharacter(delimiter.charAt(0)),
        "expected a valid delimiter");
    requireNonNull(variableSubstitutions, "variableSubstitutions cannot be null");
    return expression.toSql(delimiter, variableSubstitutions);
  }

  public String toSql(String delimiter, Map<NodeType, List<String>> variableSubstitutions,
      Map<NodeType, List<String>> variableExclusions) {
    requireNonNull(delimiter, "delimiter cannot be null");
    checkArgument(delimiter.length() == 1, "expected a one-character delimiter");
    checkArgument(LexicalAnalyzer.isValidOpenTsdbCharacter(delimiter.charAt(0)),
        "expected a valid delimiter");
    requireNonNull(variableSubstitutions, "variableSubstitutions cannot be null");
    requireNonNull(variableExclusions, "variableExclusions cannot be null");
    Set<NodeType> intersection = Sets.newHashSet(variableSubstitutions.keySet());
    intersection.retainAll(variableExclusions.keySet());
    checkArgument(intersection.isEmpty(), "You can include an entry for a given node type "
        + "either in variableSubstitutions or variableExclusions but not both");
    return expression.toSql(delimiter, variableSubstitutions, variableExclusions);
  }

  public Optional<List<Node>> match(String metricId, String delimiter) {
    requireNonNull(metricId, "metricId cannot be null");
    requireNonNull(delimiter, "delimiter cannot be null");
    checkArgument(delimiter.length() == 1, "expected a one-character delimiter");
    checkArgument(LexicalAnalyzer.isValidOpenTsdbCharacter(delimiter.charAt(0)),
        "expected a valid delimiter");
    String regexp = expression.toRegExp(delimiter);
    Pattern p = Pattern.compile(regexp);
    Matcher m = p.matcher(metricId);
    if (!m.find()) {
      return Optional.empty();
    }

    Queue<String> patternGroups = Lists.newLinkedList();
    for (int i = 1; i <= m.groupCount(); i++) {
      patternGroups.add(m.group(i));
    }
    List<Node> nodes = expression.convertPatternGroupsToNodes(patternGroups);
    return Optional.of(nodes);
  }

  @Override
  public String toString() {
    return expression.toString();
  }
}

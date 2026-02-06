package com.djt.hvac.domain.model.common.dsl.pointmap;

import static com.djt.hvac.domain.model.common.dsl.pointmap.TokenType.END_COMPLEX_VARIABLE;
import static com.djt.hvac.domain.model.common.dsl.pointmap.TokenType.END_SIMPLE_VARIABLE;
import static com.djt.hvac.domain.model.common.dsl.pointmap.TokenType.EOE;
import static com.djt.hvac.domain.model.common.dsl.pointmap.TokenType.START_COMPLEX_VARIABLE;
import static com.djt.hvac.domain.model.common.dsl.pointmap.TokenType.START_SIMPLE_VARIABLE;
import static com.djt.hvac.domain.model.common.dsl.pointmap.TokenType.STRING_LITERAL;
import static com.djt.hvac.domain.model.common.dsl.pointmap.TokenType.WILDCARD_LITERAL;
import static com.djt.hvac.domain.model.common.dsl.pointmap.utils.StringUtils.prettyJoin;
import static com.djt.hvac.domain.model.common.dsl.pointmap.utils.StringUtils.processStringAsReader;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.io.Reader;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

class Parser {
  private static final Set<String> SIMPLE_VARS = ImmutableSet.<String>builder()
      .addAll(Arrays.stream(NodeType.values())
          .map(NodeType::getVariable)
          .collect(toList()))
      .add("{*}")
      .build();


  private final LexicalAnalyzer lexer;
  private Token token;

  // External API

  static PointMapExpression parse(String expression) {
    requireNonNull(expression, "expression cannot be null");
    return processStringAsReader(expression, Parser::parse);
  }

  static PointMapExpression parse(Reader input) {
    Parser parser = new Parser(input);
    return parser.parse();
  }

  // Constructor

  private Parser(Reader input) {
    this.lexer = new LexicalAnalyzer(input);
  }

  // Parse methods

  private PointMapExpression parse() {
    Expression expression = expression();
    return new PointMapExpression(expression);
  }

  private Expression expression() {
    CompositeExpression.Builder builder = CompositeExpression.builder();
    boolean nodeVariableExpressionFound = false;
    do {
      nextToken();
      Optional<Expression> expr = complexVariableExpression();
      if (expr.isPresent()) {
        Expression e = expr.get();
        if (e instanceof NodeVariableExpression || e instanceof ComplexVariableExpression) {
          nodeVariableExpressionFound = true;
        }
        builder.add(e);

      }
    } while (token.getType() != EOE);
    expect(EOE);
    // we validate that at least one node var was found first so we don't build
    // the expression in cases where there are no node vars, since doing so would result in a less
    // user-friendly error message!
    expectAtLeastOneNodeVar(nodeVariableExpressionFound);
    Expression e = builder.build();
    validate(e);
    return builder.build();
  }

  private Optional<Expression> complexVariableExpression() {
    if (token.getType() == START_COMPLEX_VARIABLE) {
      boolean nodeVariableExpressionFound = false;
      ComplexVariableExpression.Builder builder = ComplexVariableExpression.builder();
      nextToken();
      while (token.getType() != END_COMPLEX_VARIABLE && token.getType() != EOE) {
        Position start = token.getPosition();
        Optional<Expression> expr = simpleVariableExpression(Optional.of(END_COMPLEX_VARIABLE));
        if (expr.isPresent()) {
          Expression e = expr.get();
          nodeVariableExpressionFound =
              expectNoMoreThanOneNodeVarPerComplexVar(nodeVariableExpressionFound, start, e);
          builder.add(e);
          nextToken();
        }
      }
      expect(END_COMPLEX_VARIABLE);
      expectAtLeastOneNodeVarPerComplexVar(nodeVariableExpressionFound);
      return Optional.of(builder.build());
    }
    return simpleVariableExpression(Optional.empty());
  }

  private Optional<Expression> simpleVariableExpression(Optional<TokenType> expectedTerminator) {
    ;
    if (token.getType() == START_SIMPLE_VARIABLE) {
      nextToken();
      expectOneOf(WILDCARD_LITERAL, STRING_LITERAL);
      if (token.getType() == WILDCARD_LITERAL) {
        return wildcardVariableExpression();
      }
      return nodeVariableExpression();
    }
    return stringLiteralExpression(expectedTerminator);
  }

  private Optional<Expression> nodeVariableExpression() {
    NodeType nodeType = expectValidNodeType();
    nextToken();
    expect(END_SIMPLE_VARIABLE);
    return Optional.of(new NodeVariableExpression(nodeType));
  }

  private Optional<Expression> wildcardVariableExpression() {
    nextToken();
    expect(END_SIMPLE_VARIABLE);
    return Optional.of(new WildcardVariableExpression());
  }

  private Optional<Expression> stringLiteralExpression(Optional<TokenType> expectedTerminator) {
    expectOneOf(STRING_LITERAL, expectedTerminator.isPresent() ? expectedTerminator.get() : EOE);
    if (token.getType() == STRING_LITERAL) {
      return Optional.of(new StringLiteralExpression(token.getLexeme()));
    }
    return Optional.empty();
  }

  // Token processing methods

  private void nextToken() {
    token = lexer.nextToken();
  }

  // Validation methods

  private void validate(Expression e) {
    Map<NodeType, Long> nodeVarTypeCounts = getNodeVarCountsByType(e);
    expectNoDuplicateNodeVars(nodeVarTypeCounts);
    expectPointVar(nodeVarTypeCounts);
    expectValidCombinationOfNodesAndOrdering(e);
  }

  private void expect(TokenType type) {
    if (token.getType() != type) {
      throw new SyntaxException("Syntax error at " + token.getPosition() + ": found "
          + token.getType() + " when expecting " + type);
    }
  }

  private void expectOneOf(TokenType... types) {
    Set<TokenType> typeSet = Arrays.stream(types).collect(toCollection(LinkedHashSet::new));
    if (!typeSet.contains(token.getType())) {
      throw new SyntaxException("Syntax error at " + token.getPosition() + ": found "
          + token.getType() + " when expecting " + typeSet.stream()
              .map(String::valueOf)
              .collect(prettyJoin("or ")));
    }
  }

  private void expectAtLeastOneNodeVar(boolean nodeVariableExpressionFound) {
    if (!nodeVariableExpressionFound) {
      throw new SyntaxException("Syntax error at " + token.getPosition()
          + ": expected at least one node variable");
    }
  }

  private void expectNoDuplicateNodeVars(Map<NodeType, Long> nodeVarTypeCounts) {
    Set<String> duplicateNodeVars = nodeVarTypeCounts.entrySet().stream()
        .filter(entry -> entry.getValue() > 1)
        .map(Entry::getKey)
        .map(NodeType::getVariable)
        .collect(toCollection(LinkedHashSet::new)); // ensure ordering by using a linked hashset!
    if (!duplicateNodeVars.isEmpty() && !duplicateNodeVars.toString().equalsIgnoreCase("[{equipment}]")) {
      throw new SyntaxException(
          "Syntax error: each node variable can only appear once; the expression contains multiple copies of "
              + duplicateNodeVars.stream()
                  .sorted()
                  .collect(prettyJoin("and ")));
    }
  }
  
  private void expectValidCombinationOfNodesAndOrdering(Expression e) {
    
    Set<String> validCombinations = Sets.newHashSet();
    
    // 2 nodes
    validCombinations.add("[building, point]");

    // 3 nodes
    validCombinations.add("[building, subBuilding, point]");
    validCombinations.add("[building, plant, point]");
    validCombinations.add("[building, floor, point]");
    validCombinations.add("[building, equipment, point]");

    // 4 nodes
    validCombinations.add("[building, subBuilding, plant, point]");
    validCombinations.add("[building, subBuilding, floor, point]");
    validCombinations.add("[building, subBuilding, equipment, point]");
    validCombinations.add("[building, floor, equipment, point]");
    validCombinations.add("[building, equipment, equipment, point]");
    
    // 5 nodes
    validCombinations.add("[building, subBuilding, floor, equipment, point]");
    validCombinations.add("[building, subBuilding, equipment, equipment, point]");
    validCombinations.add("[building, floor, equipment, equipment, point]");
    
    // 6 nodes
    validCombinations.add("[building, subBuilding, floor, equipment, equipment, point]");
    
    // The following combinations are valid for parsing, but anything under a plant 
    // that's not a point will be moved to the immediate parent of the plant, which
    // will either be a building or sub building (done outside of this codebase).
    validCombinations.add("[building, plant, floor, point]");
    validCombinations.add("[building, plant, floor, equipment, point]");
    validCombinations.add("[building, plant, floor, equipment, equipment, point]");
    validCombinations.add("[building, plant, equipment, point]");
    validCombinations.add("[building, plant, equipment, equipment, point]");
    validCombinations.add("[building, subBuilding, plant, floor, point]");
    validCombinations.add("[building, subBuilding, plant, floor, equipment, point]");
    validCombinations.add("[building, subBuilding, plant, floor, equipment, equipment, point]");
    validCombinations.add("[building, subBuilding, plant, equipment, point]");
    validCombinations.add("[building, subBuilding, plant, equipment, equipment, point]");
    
    List<String> nodeTypes = Lists.newArrayList();
    for (NodeVariableExpression nodeVariableExpression: e.getNodeVariables()) {
      
      nodeTypes.add(nodeVariableExpression.getNodeType().getValue());
    }
    
    String combination = nodeTypes.toString();
    if (!validCombinations.contains(combination)) {

      throw new SyntaxException("Syntax error: unsupported combination: " + combination);
    }
  }

  private void expectPointVar(Map<NodeType, Long> nodeVarTypeCounts) {
    if (!nodeVarTypeCounts.containsKey(NodeType.POINT)) {
      throw new SyntaxException(
          "Syntax error: expected a {point} node variable");
    }
  }

  private boolean expectNoMoreThanOneNodeVarPerComplexVar(boolean nodeVariableExpressionFound,
      Position start, Expression e) {
    if (e instanceof NodeVariableExpression) {
      if (nodeVariableExpressionFound) {
        throw new SyntaxException("Syntax error at " + start
            + ": found a second node variable "
            + e
            + " when expecting only one within a given set of square braces");
      }
      nodeVariableExpressionFound = true;
    }
    return nodeVariableExpressionFound;
  }

  private void expectAtLeastOneNodeVarPerComplexVar(boolean nodeVariableExpressionFound) {
    if (!nodeVariableExpressionFound) {
      throw new SyntaxException("Syntax error at " + token.getPosition()
          + ": expected at least one node variable within the current set of square braces");
    }
  }

  private NodeType expectValidNodeType() {
    NodeType nodeType = NodeType.get(token.getLexeme()).orElseThrow(
        () -> new SyntaxException("Syntax error at " + token.getPosition() + ": found {"
            + token.getLexeme() + "} when expecting one of "
            + SIMPLE_VARS.stream()
                .sorted()
                .collect(prettyJoin("or "))));
    return nodeType;
  }

  // Miscellaneous Helper methods

  private Map<NodeType, Long> getNodeVarCountsByType(Expression e) {
    return e.getNodeVariables().stream()
        .collect(groupingBy(NodeVariableExpression::getNodeType, counting()));
  }



}

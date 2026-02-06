package com.djt.hvac.domain.model.common.dsl.currentobject;

import static com.djt.hvac.domain.model.common.dsl.currentobject.TokenType.COMMA;
import static com.djt.hvac.domain.model.common.dsl.currentobject.TokenType.ELSEIF;
import static com.djt.hvac.domain.model.common.dsl.currentobject.TokenType.EOE;
import static com.djt.hvac.domain.model.common.dsl.currentobject.TokenType.EQUALS;
import static com.djt.hvac.domain.model.common.dsl.currentobject.TokenType.LEFT_PAREN;
import static com.djt.hvac.domain.model.common.dsl.currentobject.TokenType.RIGHT_PAREN;
import static com.djt.hvac.domain.model.common.dsl.currentobject.TokenType.SELECT;
import static com.djt.hvac.domain.model.common.dsl.currentobject.TokenType.TAG;
import static com.djt.hvac.domain.model.common.dsl.currentobject.TokenType.WILDCARD;
import static com.djt.hvac.domain.model.common.dsl.currentobject.TokenTypeCategory.ARGUMENT;
import static com.djt.hvac.domain.model.common.dsl.currentobject.TokenTypeCategory.FUNCTION;
import static com.djt.hvac.domain.model.common.dsl.currentobject.TokenTypeCategory.MODEL_TYPE;
import static com.djt.hvac.domain.model.common.dsl.currentobject.TokenTypeCategory.NODE_TYPE;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class CurrentObjectExpressionParser {
  private final LexicalAnalyzer lexer;
  private Token token;

  public static CompositeFunctionCall parse(String expression) {
    try (StringReader reader = new StringReader(expression)) {
      LexicalAnalyzer lexer = LexicalAnalyzer.create(reader);
      CurrentObjectExpressionParser parser = new CurrentObjectExpressionParser(lexer);
      return parser.parse();
    }
  }

  private CurrentObjectExpressionParser(LexicalAnalyzer lexer) {
    this.lexer = lexer;
  }

  private CompositeFunctionCall parse() {
    CompositeFunctionCall.Builder builder = CompositeFunctionCall.builder();
    funcs(builder);
    expect(EOE);
    return builder.build();
  }


  private void funcs(CompositeFunctionCall.Builder builder) {

    boolean firstLoop = true;
    do {
      nextToken();

      TokenType funcType = token.getType();
      if (firstLoop) {
        if (funcType == ELSEIF) {
          throw new SyntaxException("Syntax error at " + token.getPosition() + ": found 'elseIf'"
              + " when expecting a standard function call");
        }
        firstLoop = false;
      }

      FunctionCall functionCall;
      if (funcType == ELSEIF) {
        functionCall = elseIf();
      } else {
        functionCall = standardFunc();
      }
      builder.withCall(functionCall);
    } while (token.getType() == TokenType.PERIOD);
  }

  private FunctionCall elseIf() {
    expect(ELSEIF);
    nextToken();
    expect(LEFT_PAREN);
    nextToken();
    expect(SELECT);
    nextToken();
    expect(EQUALS);
    CompositeFunctionCall.Builder builder = CompositeFunctionCall.builder();
    funcs(builder);
    CompositeFunctionCall calls = builder.build();
    expect(RIGHT_PAREN);
    nextToken();
    return ElseIfFunctionCall.builder()
        .withCalls(calls)
        .build();
  }


  private FunctionCall standardFunc() {
    expect(FUNCTION);
    CurrentObjectFunction func = CurrentObjectFunction.get(token.getType());
    StandardFunctionCall.Builder builder = StandardFunctionCall.builder(func);
    nextToken();
    expect(LEFT_PAREN);
    nextToken();
    args(builder);
    expect(RIGHT_PAREN);
    nextToken();
    return builder.build();
  }

  private void args(StandardFunctionCall.Builder builder) {
    Set<TokenType> visited = Sets.newHashSet();
    while (token.getType().getCategory() == ARGUMENT) {
      if (visited.contains(token.getType())) {
        throw new SyntaxException(
            "Syntax error at " + token.getPosition() + ": duplicate argument "
                + token.toString());
      }
      visited.add(token.getType());
      TokenType argType = token.getType();
      nextToken();
      expect(TokenType.EQUALS);
      nextToken();
      switch (argType) {
        case MODEL:
          builder.withModel(model());
          break;
        case TYPE:
          builder.withType(type());
          break;
        case TAGS:
          tags(builder);
          break;
        default:
          throw new SyntaxException(
              "Syntax error at " + token.getPosition() + ": invalid argument "
                  + token.toString());
      }
      if (token.getType() == COMMA) {
        nextToken();
      }
    }
  }

  private Model model() {
    expect(MODEL_TYPE);
    Token tok = token;
    nextToken();
    return Model.get(tok.getType());
  }

  private NodeType type() {
    expect(NODE_TYPE);
    Token tok = token;
    nextToken();
    return NodeType.get(tok.getType());
  }

  private void tags(StandardFunctionCall.Builder builder) {
    expect(TAG, WILDCARD);
    List<String> tags = Lists.newArrayList();
    while (token.getType() == TAG || token.getType() == WILDCARD) {
      if (token.getType() == WILDCARD) {
        builder.withWildcardTag(true);
      } else {
        tags.add(token.getLexeme());
      }
      nextToken();
      if (token.getType() == TokenType.PIPE) {
        nextToken();
      }
    }
    builder.withTags(tags);
  }

  private void nextToken() {
    token = lexer.nextToken();
  }

  private void expect(TokenType... types) {
    Set<TokenType> typeSet = Arrays.stream(types)
        .collect(toSet());
    if (!typeSet.contains(token.getType())) {
      throw new SyntaxException("Syntax error at " + token.getPosition() + ": found "
          + token.getType() + " when expecting "
          + typeSet.stream().map(TokenType::getValue).collect(joining(", ")));
    }
  }

  private void expect(TokenTypeCategory category) {
    if (token.getType().getCategory() != category) {
      throw new SyntaxException("Syntax error at " + token.getPosition() + ": found "
          + token.toString() + " when expecting " + category.getValue());
    }
  }
}

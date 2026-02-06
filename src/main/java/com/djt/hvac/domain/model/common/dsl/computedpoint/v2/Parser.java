package com.djt.hvac.domain.model.common.dsl.computedpoint.v2;

import static com.djt.hvac.domain.model.common.dsl.computedpoint.v2.TokenType.AND;
import static com.djt.hvac.domain.model.common.dsl.computedpoint.v2.TokenType.COMMA;
import static com.djt.hvac.domain.model.common.dsl.computedpoint.v2.TokenType.DIVIDE;
import static com.djt.hvac.domain.model.common.dsl.computedpoint.v2.TokenType.ELSE;
import static com.djt.hvac.domain.model.common.dsl.computedpoint.v2.TokenType.EQUALS;
import static com.djt.hvac.domain.model.common.dsl.computedpoint.v2.TokenType.GREATER_THAN;
import static com.djt.hvac.domain.model.common.dsl.computedpoint.v2.TokenType.GREATER_THAN_OR_EQUALS;
import static com.djt.hvac.domain.model.common.dsl.computedpoint.v2.TokenType.IF;
import static com.djt.hvac.domain.model.common.dsl.computedpoint.v2.TokenType.LEFT_PAREN;
import static com.djt.hvac.domain.model.common.dsl.computedpoint.v2.TokenType.LESS_THAN;
import static com.djt.hvac.domain.model.common.dsl.computedpoint.v2.TokenType.LESS_THAN_OR_EQUALS;
import static com.djt.hvac.domain.model.common.dsl.computedpoint.v2.TokenType.MINUS;
import static com.djt.hvac.domain.model.common.dsl.computedpoint.v2.TokenType.MULTIPLY;
import static com.djt.hvac.domain.model.common.dsl.computedpoint.v2.TokenType.NOT;
import static com.djt.hvac.domain.model.common.dsl.computedpoint.v2.TokenType.NOT_EQUALS;
import static com.djt.hvac.domain.model.common.dsl.computedpoint.v2.TokenType.NUMBER;
import static com.djt.hvac.domain.model.common.dsl.computedpoint.v2.TokenType.OR;
import static com.djt.hvac.domain.model.common.dsl.computedpoint.v2.TokenType.PLUS;
import static com.djt.hvac.domain.model.common.dsl.computedpoint.v2.TokenType.RIGHT_PAREN;
import static com.djt.hvac.domain.model.common.dsl.computedpoint.v2.TokenType.TRUE;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import com.djt.hvac.domain.model.common.dsl.computedpoint.v2.function.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

class Parser {
  private final FunctionRegistry functions;
  private final Map<String, Integer> functionCallIds = Maps.newHashMap();

  private LexicalAnalyzer lexer;
  private Token token;

  Parser(FunctionRegistry functions) {
    this.functions = requireNonNull(functions, "functions cannot be null");
  }

  ComputedPointExpression parse(String s) {
    try (Reader in = new StringReader(s)) {
      return parse(in);
    } catch (IOException e) {
      // This should never happen, but if it does, throw an assertion error
      throw new AssertionError("Unexpected exception", e);
    }
  }

  ComputedPointExpression parse(Reader in) {
    lexer = new LexicalAnalyzer(functions, in);
    nextToken();
    Expression expr = castToExpression(ifExpression());
    expect(TokenType.EOS);
    ComputedPointExpression result = new ComputedPointExpression(expr);
    return result;
  }

  private AbstractExpressionWithVariables ifExpression() {
    if (token.getType() == IF) {
      nextToken();
      expect(LEFT_PAREN);
      nextToken();
      AbstractExpressionWithVariables condition = orExpression();
      expect(RIGHT_PAREN);
      nextToken();
      AbstractExpressionWithVariables ifBody = ifExpression();
      expect(ELSE);
      nextToken();
      AbstractExpressionWithVariables elseBody = ifExpression();
      AbstractExpression ifExpression = new IfElseExpression(condition,
          ifBody, elseBody);
      return ifExpression;
    }
    return orExpression();
  }

  private AbstractExpressionWithVariables orExpression() {
    AbstractExpressionWithVariables expression = andExpression();
    while (token.getType() == OR) {
      nextToken();
      AbstractExpressionWithVariables expression2 = andExpression();
      expression =
          new OrExpression(expression, expression2);
    }
    return expression;
  }

  private AbstractExpressionWithVariables andExpression() {
    AbstractExpressionWithVariables expression = equalityExpression();
    while (token.getType() == AND) {
      nextToken();
      AbstractExpressionWithVariables expression2 = equalityExpression();
      expression =
          new AndExpression(expression, expression2);
    }
    return expression;
  }

  private AbstractExpressionWithVariables equalityExpression() {
    AbstractExpressionWithVariables expression = relationalExpression();
    while (token.getType() == EQUALS || token.getType() == NOT_EQUALS) {
      Token start = token;
      nextToken();
      AbstractExpressionWithVariables expression2 = relationalExpression();
      if (start.getType() == EQUALS) {
        expression =
            new EqualsExpression(expression, expression2);
      } else {
        expression =
            new NotEqualsExpression(expression, expression2);
      }
    }
    return expression;
  }

  private AbstractExpressionWithVariables relationalExpression() {
    AbstractExpressionWithVariables expression = notExpression();
    while (token.getType() == GREATER_THAN || token.getType() == GREATER_THAN_OR_EQUALS
        || token.getType() == LESS_THAN || token.getType() == LESS_THAN_OR_EQUALS) {
      Token start = token;
      nextToken();
      AbstractExpressionWithVariables expression2 = notExpression();
      switch (start.getType()) {
        case GREATER_THAN:
          expression = new GreaterThanExpression(expression, expression2);
          break;
        case GREATER_THAN_OR_EQUALS:
          expression = new GreaterThanOrEqualsExpression(expression, expression2);
          break;
        case LESS_THAN:
          expression = new LessThanExpression(expression, expression2);
          break;
        case LESS_THAN_OR_EQUALS:
          expression = new LessThanOrEqualsExpression(expression, expression2);
          break;
        default:
          throw new AssertionError("Expected the token to be " + GREATER_THAN + ", "
              + GREATER_THAN_OR_EQUALS + ", " + LESS_THAN + ", or " + LESS_THAN_OR_EQUALS);
      }
    }
    return expression;
  }

  private AbstractExpressionWithVariables notExpression() {
    if (token.getType() == NOT) {
      nextToken();
      AbstractExpressionWithVariables expression = additionExpression();
      return new NotExpression(expression);
    }
    return additionExpression();
  }

  private AbstractExpressionWithVariables additionExpression() {
    AbstractExpressionWithVariables expr = multiplicationExpression();
    TokenType type = token.getType();
    while (type == PLUS || type == MINUS) {
      nextToken();
      AbstractExpressionWithVariables expr2 = multiplicationExpression();
      if (type == PLUS) {
        expr = new AdditionExpression(expr, expr2);
      } else {
        expr = new SubtractionExpression(expr, expr2);
      }
      type = token.getType();
    }
    return expr;
  }

  private AbstractExpressionWithVariables multiplicationExpression() {
    AbstractExpressionWithVariables expr = parenthesisExpression();
    TokenType type = token.getType();
    while (type == MULTIPLY || type == DIVIDE) {
      nextToken();
      AbstractExpressionWithVariables expr2 = parenthesisExpression();
      if (type == MULTIPLY) {
        expr = new MultiplicationExpression(expr, expr2);
      } else {
        expr = new DivisionExpression(expr, expr2);
      }
      type = token.getType();
    }
    return expr;
  }

  private AbstractExpressionWithVariables parenthesisExpression() {
    AbstractExpressionWithVariables expr;
    if (token.getType() == LEFT_PAREN) {
      nextToken();
      expr = ifExpression();
      expect(RIGHT_PAREN);
      nextToken();
    } else {
      expr = termExpression();
    }
    return expr;
  }


  private AbstractExpressionWithVariables termExpression() {
    switch (token.getType()) {
      case MINUS:
        nextToken();
        expect(NUMBER);
        return new UnaryMinusExpression((NumberExpression) numberExpression());
      case NUMBER:
        return numberExpression();
      case TRUE:
      case FALSE:
        return booleanLiteralExpression();
      case VARIABLE:
        return variableExpression();
      case FUNCTION:
        return functionCallExpression();
      default:
        throw new SyntaxException("Syntax error at " + token.getPosition() + ": found "
            + token.getType() + " when expecting a variable, a function call, or a literal");

    }
  }

  private AbstractExpression numberExpression() {
    double value = Double.parseDouble(token.getLexeme());
    AbstractExpression expr = new NumberExpression(value);
    nextToken();
    return expr;
  }

  private AbstractBooleanExpression booleanLiteralExpression() {
    boolean value = token.getType() == TRUE ? true : false;
    AbstractBooleanExpression expr = new BooleanLiteralExpression(value);
    nextToken();
    return expr;
  }

  private AbstractExpression variableExpression() {
    String name = token.getLexeme();
    AbstractExpression expr = new VariableExpression(name);
    nextToken();
    return expr;
  }

  private AbstractExpression functionCallExpression() {
    String functionName = token.getLexeme();
    Function func = functions.get(functionName);
    if (func == null) {
      throw new AssertionError("The lexical analyzer should never return a function token "
          + "unless the name of the function is defined in the function registry.");
    }
    String functionCallId = getFunctionCallId(func);
    nextToken();
    expect(LEFT_PAREN);
    nextToken();
    List<Expression> argExpressions = argumentExpressions(func);
    expect(RIGHT_PAREN);
    nextToken();
    AbstractExpression expr = new FunctionCallExpression(func, functionCallId, argExpressions);
    return expr;
  }

  private List<Expression> argumentExpressions(Function func) {
    List<Expression> argExpressions = Lists.newArrayList();
    boolean first = true;
    for (int i = 0; i < func.getMinParams(); i++) {
      if (first) {
        first = false;
      } else {
        expect(COMMA);
        nextToken();
      }
      argExpressions.add(castToExpression(additionExpression()));
    }
    if (token.getType() != RIGHT_PAREN) {
      while (argExpressions.size() < func.getMaxParams()) {
        argExpressions.add(castToExpression(additionExpression()));
        if (token.getType() == COMMA) {
          nextToken();
        } else {
          break;
        }
      }
    }
    return argExpressions;
  }

  private void nextToken() {
    token = lexer.nextToken();
  }

  private void expect(TokenType type) {
    if (token.getType() != type) {
      throw new SyntaxException("Syntax error at " + token.getPosition() + ": found "
          + token.getType() + " when expecting " + type);
    }
  }

  private String getFunctionCallId(Function func) {
    Integer id = functionCallIds.get(func.getName());
    if (id == null) {
      id = 0;
    }
    id += 1;
    functionCallIds.put(func.getName(), id);
    return func.getName() + "_" + id;
  }

  private Expression castToExpression(AbstractExpressionWithVariables abstractExpr) {
    if (!Expression.class.isInstance(abstractExpr)) {
      throw new SyntaxException("Expected a numeric expression");
    }
    Expression expr = Expression.class.cast(abstractExpr);
    return expr;
  }
}

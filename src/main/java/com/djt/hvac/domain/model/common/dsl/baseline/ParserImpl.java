package com.djt.hvac.domain.model.common.dsl.baseline;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Optional;

import com.djt.hvac.domain.model.common.dsl.baseline.Token.Type;
import com.google.common.collect.Lists;

import static com.djt.hvac.domain.model.common.dsl.baseline.Token.Type.AND;
import static com.djt.hvac.domain.model.common.dsl.baseline.Token.Type.COMMA;
import static com.djt.hvac.domain.model.common.dsl.baseline.Token.Type.DIVIDE;
import static com.djt.hvac.domain.model.common.dsl.baseline.Token.Type.DOUBLE;
import static com.djt.hvac.domain.model.common.dsl.baseline.Token.Type.ELSE;
import static com.djt.hvac.domain.model.common.dsl.baseline.Token.Type.EOF;
import static com.djt.hvac.domain.model.common.dsl.baseline.Token.Type.EQUALS;
import static com.djt.hvac.domain.model.common.dsl.baseline.Token.Type.GREATER_THAN;
import static com.djt.hvac.domain.model.common.dsl.baseline.Token.Type.GREATER_THAN_OR_EQUALS;
import static com.djt.hvac.domain.model.common.dsl.baseline.Token.Type.IF;
import static com.djt.hvac.domain.model.common.dsl.baseline.Token.Type.LEFT_PAREN;
import static com.djt.hvac.domain.model.common.dsl.baseline.Token.Type.LESS_THAN;
import static com.djt.hvac.domain.model.common.dsl.baseline.Token.Type.LESS_THAN_OR_EQUALS;
import static com.djt.hvac.domain.model.common.dsl.baseline.Token.Type.MINUS;
import static com.djt.hvac.domain.model.common.dsl.baseline.Token.Type.MULTIPLY;
import static com.djt.hvac.domain.model.common.dsl.baseline.Token.Type.NOT;
import static com.djt.hvac.domain.model.common.dsl.baseline.Token.Type.NOT_EQUALS;
import static com.djt.hvac.domain.model.common.dsl.baseline.Token.Type.OR;
import static com.djt.hvac.domain.model.common.dsl.baseline.Token.Type.PLUS;
import static com.djt.hvac.domain.model.common.dsl.baseline.Token.Type.RIGHT_PAREN;
import static java.util.Objects.requireNonNull;

class ParserImpl implements Parser {

  private final LexicalAnalyzerFactory factory;
  private final TokenList tokens = TokenList.create();
  private LexicalAnalyzer lexer;
  private Token token;

  static ParserImpl create(LexicalAnalyzerFactory factory) {
    return new ParserImpl(factory);
  }

  private ParserImpl(LexicalAnalyzerFactory factory) {
    this.factory = requireNonNull(factory, "factory cannot be null");
  }

  @Override
  public Expression<Double> parse(String expression) {
    try (Reader in = new StringReader(expression)) {
      return parse(in);
    } catch (IOException e) {
      // This should never happen, but if it does, throw an assertion error
      throw new AssertionError("Unexpected exception", e);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public Expression<Double> parse(Reader in) throws IOException {
    lexer = this.factory.create(in);
    nextToken();
    Expression<?> e = ifExpression();
    if (e.getType() != Double.class) {
      throw new SyntaxException(
          "Syntax error at " + tokens.get(0).getPosition() + ": expected a numeric expression");
    }
    expect(EOF);
    return (Expression<Double>) e;
  }

  @SuppressWarnings("unchecked")
  private Expression<?> ifExpression() throws IOException {
    if (token.getType() == IF) {
      nextToken();
      expect(LEFT_PAREN);
      Token start = token;
      nextToken();
      Expression<?> condition = orExpression();
      if (condition.getType() != Boolean.class) {
        throw new SyntaxException("Syntax error at " + tokens.next(start).getPosition()
            + ": expected a boolean expression");
      }
      expect(RIGHT_PAREN);
      start = token;
      nextToken();
      Expression<?> ifBody = ifExpression();
      if (ifBody.getType() != Double.class) {
        throw new SyntaxException("Syntax error at " + tokens.next(start).getPosition()
            + ": expected a numeric expression");
      }
      expect(ELSE);
      start = token;
      nextToken();
      Expression<?> elseBody = ifExpression();
      if (elseBody.getType() != Double.class) {
        throw new SyntaxException("Syntax error at " + tokens.next(start).getPosition()
            + ": expected a numeric expression");
      }

      Expression<Double> ifExpression = IfExpression.create((Expression<Boolean>) condition,
          (Expression<Double>) ifBody, (Expression<Double>) elseBody);
      return ifExpression;
    }
    return orExpression();
  }

  @SuppressWarnings("unchecked")
  private Expression<?> orExpression() throws IOException {
    Expression<?> expression = andExpression();
    while (token.getType() == OR) {
      if (expression.getType() != Boolean.class) {
        throw new SyntaxException("Syntax error at " + token.getPosition() + ": unexpected "
            + token.getType() + " operator following a non-boolean expression");
      }
      Token start = token;
      nextToken();
      Expression<?> expression2 = andExpression();
      if (expression2.getType() != Boolean.class) {
        throw new SyntaxException("Syntax error at " + tokens.next(start).getPosition()
            + ": unexpected non-boolean expression");
      }
      expression =
          OrOperator.create((Expression<Boolean>) expression, (Expression<Boolean>) expression2);
    }
    return expression;
  }

  @SuppressWarnings("unchecked")
  private Expression<?> andExpression() throws IOException {
    Expression<?> expression = equalityExpression();
    while (token.getType() == AND) {
      if (expression.getType() != Boolean.class) {
        throw new SyntaxException("Syntax error at " + token.getPosition() + ": unexpected "
            + token.getType() + " operator following a non-boolean expression");
      }
      Token start = token;
      nextToken();
      Expression<?> expression2 = equalityExpression();
      if (expression2.getType() != Boolean.class) {
        throw new SyntaxException("Syntax error at " + tokens.next(start).getPosition()
            + ": unexpected non-boolean expression");
      }
      expression =
          AndOperator.create((Expression<Boolean>) expression, (Expression<Boolean>) expression2);
    }
    return expression;
  }

  private Expression<?> equalityExpression() throws IOException {
    Expression<?> expression = relationalExpression();
    while (token.getType() == EQUALS || token.getType() == NOT_EQUALS) {
      Token start = token;
      nextToken();
      Expression<?> expression2 = relationalExpression();
      switch (start.getType()) {
        case EQUALS:
          expression = EqualsOperator.create(expression, expression2);
          break;
        case NOT_EQUALS:
          expression = NotEqualsOperator.create(expression, expression2);
          break;
        default:
          throw new AssertionError(
              "Expected the token to be one of " + EQUALS + " or " + NOT_EQUALS);
      }
    }
    return expression;
  }

  @SuppressWarnings("unchecked")
  private Expression<?> relationalExpression() throws IOException {
    Expression<?> expression = arithmeticExpression();
    while (token.getType() == GREATER_THAN || token.getType() == GREATER_THAN_OR_EQUALS
        || token.getType() == LESS_THAN || token.getType() == LESS_THAN_OR_EQUALS) {
      if (expression.getType() != Double.class) {
        throw new SyntaxException("Syntax error at " + token.getPosition() + ": unexpected "
            + token.getType() + " operator following a non-numeric expression");
      }
      Token start = token;
      nextToken();
      Expression<?> expression2 = arithmeticExpression();
      if (expression2.getType() != Double.class) {
        throw new SyntaxException("Syntax error at " + tokens.next(start).getPosition()
            + ": unexpected non-numeric expression");
      }
      switch (start.getType()) {
        case GREATER_THAN:
          expression = GreaterThanOperator.create((Expression<Double>) expression,
              (Expression<Double>) expression2);
          break;
        case GREATER_THAN_OR_EQUALS:
          expression = GreaterThanOrEqualsOperator.create((Expression<Double>) expression,
              (Expression<Double>) expression2);
          break;
        case LESS_THAN:
          expression = LessThanOperator.create((Expression<Double>) expression,
              (Expression<Double>) expression2);
          break;
        case LESS_THAN_OR_EQUALS:
          expression = LessThanOrEqualsOperator.create((Expression<Double>) expression,
              (Expression<Double>) expression2);
          break;
        default:
          throw new AssertionError("Expected the token to be " + GREATER_THAN + ", "
              + GREATER_THAN_OR_EQUALS + ", " + LESS_THAN + ", or " + LESS_THAN_OR_EQUALS);
      }
    }
    return expression;
  }

  @SuppressWarnings("unchecked")
  private Expression<?> arithmeticExpression() throws IOException {
    Expression<?> expression = termExpression();
    while (token.getType() == PLUS || token.getType() == MINUS) {
      if (expression.getType() != Double.class) {
        throw new SyntaxException("Syntax error at " + token.getPosition() + ": unexpected "
            + token.getType() + " operator following a non-numeric expression");
      }
      Token start = token;
      nextToken();
      Expression<?> expression2 = termExpression();
      if (expression2.getType() != Double.class) {
        throw new SyntaxException("Syntax error at " + tokens.next(start).getPosition()
            + ": unexpected non-numeric expression");
      }
      switch (start.getType()) {
        case PLUS:
          expression = AdditionOperator.create((Expression<Double>) expression,
              (Expression<Double>) expression2);
          break;
        case MINUS:
          expression = SubtractionOperator.create((Expression<Double>) expression,
              (Expression<Double>) expression2);
          break;
        default:
          throw new AssertionError("Expected the token to be " + PLUS + " or " + MINUS);
      }
    }
    return expression;
  }

  @SuppressWarnings("unchecked")
  private Expression<?> termExpression() throws IOException {
    Expression<?> expression = unaryExpression();
    while (token.getType() == MULTIPLY || token.getType() == DIVIDE) {
      if (expression.getType() != Double.class) {
        throw new SyntaxException("Syntax error at " + token.getPosition() + ": unexpected "
            + token.getType() + " operator following a non-numeric expression");
      }
      Token start = token;
      nextToken();
      Expression<?> expression2 = unaryExpression();
      if (expression2.getType() != Double.class) {
        throw new SyntaxException("Syntax error at " + tokens.next(start).getPosition()
            + ": unexpected non-numeric expression");
      }
      switch (start.getType()) {
        case MULTIPLY:
          expression = MultiplicationOperator.create((Expression<Double>) expression,
              (Expression<Double>) expression2);
          break;
        case DIVIDE:
          expression = DivisionOperator.create((Expression<Double>) expression,
              (Expression<Double>) expression2);
          break;
        default:
          throw new AssertionError("Expected the token to be " + MULTIPLY + " or " + DIVIDE);
      }
    }
    return expression;
  }

  @SuppressWarnings("unchecked")
  private Expression<?> unaryExpression() throws IOException {
    if (token.getType() == MINUS) {
      nextToken();
      expect(DOUBLE);
      Expression<?> expression =
          UnaryMinusOperator.create(NumericLiteral.create(DoubleToken.class.cast(token).value()));
      nextToken();
      return expression;
    } else if (token.getType() == NOT) {
      Token start = token;
      nextToken();
      Expression<?> expression = unaryExpression();
      if (expression.getType() != Boolean.class) {
        throw new SyntaxException("Syntax error at " + tokens.next(start).getPosition()
            + ": unexpected non-boolean expression");
      }
      return NotOperator.create((Expression<Boolean>) expression);
    }
    return factor();
  }

  private Expression<?> factor() throws IOException {
    Expression<?> expression = null;
    switch (token.getType()) {
      case LEFT_PAREN:
        nextToken();
        expression = ifExpression();
        expect(RIGHT_PAREN);
        nextToken();
        return expression;
      case VARIABLE:
        VariableId<?> variableId = ((VariableToken) token).id();
        if (variableId.getParameterTypes().size() == 0) {
          expression = Variable.create(variableId);
          nextToken();
        } else {
          nextToken();
          expect(LEFT_PAREN);
          nextToken();
          List<Object> args = args(variableId);
          expect(RIGHT_PAREN);
          nextToken();
          expression = Variable.create(variableId, args);
        }
        return expression;
      default:
        expression = literal();
        return expression;
    }
  }

  private Expression<?> literal() throws IOException {
    return literal(Optional.empty(), Optional.empty());
  }

  private Expression<?> literal(Optional<Class<? extends Literal<?>>> expectedClass,
      Optional<String> expected) throws IOException {
    Expression<?> expression = null;
    switch (token.getType()) {
      case DOUBLE:
        if (expectedClass.isPresent() && !expectedClass.get().equals(NumericLiteral.class)) {
          expect(expected.get());
        }
        expression = NumericLiteral.create(((DoubleToken) token).value());
        nextToken();
        return expression;
      case TRUE:
        if (expectedClass.isPresent() && !expectedClass.get().equals(BooleanLiteral.class)) {
          expect(expected.get());
        }
        expression = BooleanLiteral.TRUE;
        nextToken();
        return expression;
      case FALSE:
        if (expectedClass.isPresent() && !expectedClass.get().equals(BooleanLiteral.class)) {
          expect(expected.get());
        }
        expression = BooleanLiteral.FALSE;
        nextToken();
        return expression;
      default:
        throw new SyntaxException("Syntax error at " + token.getPosition() + ": found "
            + token.getType() + " when expecting "
            + (expected.isPresent() ? expected.get() : "a variable or literal"));
    }
  }

  private List<Object> args(VariableId<?> variableId) throws IOException {
    List<Object> args = Lists.newArrayList();
    boolean firstLoop = true;
    for (int i = 0; i < variableId.getParameterTypes().size(); i++) {
      if (firstLoop) {
        firstLoop = false;
      } else {
        expect(COMMA);
        nextToken();
      }
      Class<? extends Literal<?>> paramType = variableId.getParameterTypes().get(i);
      String expected = Literal.getLabel(paramType);
      Expression<?> expression = literal(Optional.of(paramType), Optional.of(expected));
      Object arg = expression.evaluate(Inputs.builder().build());
      args.add(arg);
    }
    return args;
  }

  private void nextToken() throws IOException {
    token = lexer.nextToken();
    tokens.add(token);
  }

  private void expect(Type type) {
    if (token.getType() != type) {
      throw new SyntaxException("Syntax error at " + token.getPosition() + ": found "
          + token.getType() + " when expecting " + type);
    }
  }

  private void expect(String expected) {
    throw new SyntaxException("Syntax error at " + token.getPosition() + ": found "
        + token.getType() + " when expecting " + expected);
  }
}

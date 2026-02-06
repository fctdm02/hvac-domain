package com.djt.hvac.domain.model.common.dsl.computedpoint;

import static com.djt.hvac.domain.model.common.dsl.computedpoint.Token.Type.DIVIDE;
import static com.djt.hvac.domain.model.common.dsl.computedpoint.Token.Type.EOF;
import static com.djt.hvac.domain.model.common.dsl.computedpoint.Token.Type.MINUS;
import static com.djt.hvac.domain.model.common.dsl.computedpoint.Token.Type.MULTIPLY;
import static com.djt.hvac.domain.model.common.dsl.computedpoint.Token.Type.PLUS;
import static com.djt.hvac.domain.model.common.dsl.computedpoint.Token.Type.RIGHT_PAREN;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import com.djt.hvac.domain.model.common.dsl.computedpoint.Token.Type;

class Parser {

  private final TokenList tokens = TokenList.create();
  private LexicalAnalyzer lexer;
  private Token token;


  static Parser create() {
    return new Parser();
  }

  public <T> Expression<T> parse(String expression) {
    try (Reader in = new StringReader(expression)) {
      return parse(in);
    } catch (IOException e) {
      // This should never happen, but if it does, throw an assertion error
      throw new AssertionError("Unexpected exception", e);
    }
  }

  @SuppressWarnings("unchecked")
  public <T> Expression<T> parse(Reader in) throws IOException {
    lexer = new LexicalAnalyzer(in);
    nextToken();
    Expression<T> e = (Expression<T>) arithmeticExpression();
    expect(EOF);
    return e;
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
    Expression<?> expression = factor();
    while (token.getType() == MULTIPLY || token.getType() == DIVIDE) {
      if (expression.getType() != Double.class) {
        throw new SyntaxException("Syntax error at " + token.getPosition() + ": unexpected "
            + token.getType() + " operator following a non-numeric expression");
      }
      Token start = token;
      nextToken();
      Expression<?> expression2 = factor();
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

  private Expression<?> factor() throws IOException {
    Expression<?> expression = null;
    switch (token.getType()) {
      case LEFT_PAREN:
        nextToken();
        expression = arithmeticExpression();
        expect(RIGHT_PAREN);
        nextToken();
        return expression;
      case VARIABLE:
        String variableId = ((VariableToken) token).id();
        // TODO: We need to deduce the type of the variable based on its context instead of
        // hard-coding to Double.class; may need to do this as a post-parse analysis step
        expression = Variable.create(Double.class, variableId);
        nextToken();
        return expression;
      case DOUBLE:
        expression = NumericLiteral.create(((DoubleToken) token).value());
        nextToken();
        return expression;
      case TRUE:
        expression = BooleanLiteral.TRUE;
        nextToken();
        return expression;
      case FALSE:
        expression = BooleanLiteral.FALSE;
        nextToken();
        return expression;
      default:
        throw new SyntaxException("Syntax error at " + token.getPosition() + ": found "
            + token.getType() + " when expecting a variable or literal");
    }
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

}

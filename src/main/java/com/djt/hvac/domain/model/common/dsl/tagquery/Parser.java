package com.djt.hvac.domain.model.common.dsl.tagquery;


import static com.djt.hvac.domain.model.common.dsl.tagquery.TokenType.AND;
import static com.djt.hvac.domain.model.common.dsl.tagquery.TokenType.EOE;
import static com.djt.hvac.domain.model.common.dsl.tagquery.TokenType.LEFT_PAREN;
import static com.djt.hvac.domain.model.common.dsl.tagquery.TokenType.NOT;
import static com.djt.hvac.domain.model.common.dsl.tagquery.TokenType.OR;
import static com.djt.hvac.domain.model.common.dsl.tagquery.TokenType.RIGHT_PAREN;

import java.io.StringReader;

class Parser {
  private final LexicalAnalyzer lexer;
  private Token token;

  static Expression parse(String expression) {
    try (StringReader reader = new StringReader(expression)) {
      LexicalAnalyzer lexer = LexicalAnalyzer.create(reader);
      Parser parser = new Parser(lexer);
      return parser.parse();
    }
  }

  Parser(LexicalAnalyzer lexer) {
    this.lexer = lexer;
  }

  Expression parse() {
    nextToken();
    Expression c = orCombination();
    expect(EOE);
    return c;
  }

  private Expression orCombination() {
    Expression c = andCombination();
    while (token.getType() == OR) {
      nextToken();
      c = new OrExpression(c, andCombination());
    }
    return c;
  }

  private Expression andCombination() {
    Expression c = notCombination();
    while (token.getType() == AND) {
      nextToken();
      c = new AndExpression(c, notCombination());
    }
    return c;
  }

  private Expression notCombination() {
    if (token.getType() == NOT) {
      nextToken();
      return new NotExpression(simpleCombination());
    }
    return simpleCombination();
  }

  private Expression simpleCombination() {
    if (token.getType() == LEFT_PAREN) {
      nextToken();
      Expression c = orCombination();
      expect(RIGHT_PAREN);
      nextToken();
      return new ParenthesesExpression(c);
    }
    return tagCombination();
  }

  private Expression tagCombination() {
    expect(TokenType.TAG);
    Expression c = new TagExpression(token.getLexeme());
    nextToken();
    return c;
  }

  private void nextToken() {
    token = lexer.nextToken();
  }

  private void expect(TokenType type) {
    if (token.getType() != type) {
      throw new SyntaxException("Syntax error at " + token.getPosition() + ": found "
          + token.getType() + " when expecting " + type.getValue());
    }
  }
}

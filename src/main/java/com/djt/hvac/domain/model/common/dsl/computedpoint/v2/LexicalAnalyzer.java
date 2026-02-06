package com.djt.hvac.domain.model.common.dsl.computedpoint.v2;

import java.io.IOException;
import java.io.Reader;

import static com.djt.hvac.domain.model.common.dsl.computedpoint.v2.TokenType.AND;
import static com.djt.hvac.domain.model.common.dsl.computedpoint.v2.TokenType.COMMA;
import static com.djt.hvac.domain.model.common.dsl.computedpoint.v2.TokenType.DIVIDE;
import static com.djt.hvac.domain.model.common.dsl.computedpoint.v2.TokenType.ELSE;
import static com.djt.hvac.domain.model.common.dsl.computedpoint.v2.TokenType.EOS;
import static com.djt.hvac.domain.model.common.dsl.computedpoint.v2.TokenType.EQUALS;
import static com.djt.hvac.domain.model.common.dsl.computedpoint.v2.TokenType.FALSE;
import static com.djt.hvac.domain.model.common.dsl.computedpoint.v2.TokenType.FUNCTION;
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
import static com.djt.hvac.domain.model.common.dsl.computedpoint.v2.TokenType.VARIABLE;
import static java.util.Objects.requireNonNull;

class LexicalAnalyzer {

  private final FunctionRegistry functions;
  private final Reader in;
  private int peek = ' ';
  private Position position;
  private Position startPosition;

  LexicalAnalyzer(FunctionRegistry functions, Reader in) {
    this.functions = requireNonNull(functions, "functions cannot be null");
    this.in = requireNonNull(in, "in cannot be null");
    this.position = new Position();
  }

  Token nextToken() {
    // skip whitespace
    for (;; readChar()) {
      if (peek == ' ' || peek == '\t' || peek == '\n' || peek == '\r') {
        if (peek == '\n') {
          position.advanceLine();
        }
        continue;
      } else {
        break;
      }
    }
    startPosition = Position.copyOf(position);
    // Identify tokens representing operators
    switch (peek) {
      case '(':
        return getToken(LEFT_PAREN);
      case ')':
        return getToken(RIGHT_PAREN);
      case '+':
        return getToken(PLUS);
      case '-':
        return getToken(MINUS);
      case '*':
        return getToken(MULTIPLY);
      case '/':
        return getToken(DIVIDE);
      case ',':
        return getToken(COMMA);
      case '=':
        if (readChar('=')) {
          return getToken(EQUALS);
        }
      case '!':
        if (readChar('=')) {
          return getToken(NOT_EQUALS);
        }
        return getToken(NOT, false);
      case '&':
        if (readChar('&')) {
          return getToken(AND);
        }
      case '|':
        if (readChar('|')) {
          return getToken(OR);
        }
      case '>':
        if (readChar('=')) {
          return getToken(GREATER_THAN_OR_EQUALS);
        }
        return getToken(GREATER_THAN, false);
      case '<':
        if (readChar('=')) {
          return getToken(LESS_THAN_OR_EQUALS);
        }
        return getToken(LESS_THAN, false);
      case -1:
        return getToken(EOS, false);
    }

    // Identify tokens representing numbers
    if (Character.isDigit(peek)) {
      long v = 0;
      boolean firstLoop = true;
      do {
        if (firstLoop) {
          firstLoop = false;
        } else if (v == 0) {
          throw new LexicalAnalysisException("Invalid number starting at " + startPosition
              + ": numbers with multiple digits cannot start with 0");
        }
        v = (10 * v) + Character.digit(peek, 10);
        readChar();
      } while (Character.isDigit(peek));
      if (peek != '.' && peek != 'E' && peek != 'e') {
        return getToken(NUMBER, String.valueOf(v));
      }
      StringBuilder buf = new StringBuilder();
      if (peek == '.') {
        buf.append(String.valueOf(v)).append('.');
        int fracDigits = 0;
        while (true) {
          readChar();
          if (!Character.isDigit(peek)) {
            break;
          }
          buf.append((char) peek);
          fracDigits += 1;
        }
        if (fracDigits == 0) {
          throw new LexicalAnalysisException("Invalid number starting at " + startPosition
              + ": expected at least one digit after the '.'");
        }
        try {
          Double.parseDouble(buf.toString());
        } catch (NumberFormatException ex) {
          throw new LexicalAnalysisException(ex);
        }
      }
      if (peek != 'E' && peek != 'e') {
        return getToken(NUMBER, buf.toString());
      }

      buf.append((char) peek);
      readChar();
      if (peek == '+' || peek == '-') {
        buf.append((char) peek);
        readChar();
      }
      if (!Character.isDigit(peek)) {
        throw new LexicalAnalysisException("Invalid number starting at " + startPosition
            + ": expected at least one digit after the 'E'");
      }
      while (Character.isDigit(peek)) {
        buf.append((char) peek);
        readChar();
      }
      try {
        Double.parseDouble(buf.toString());
        return getToken(NUMBER, buf.toString());
      } catch (NumberFormatException ex) {
        throw new LexicalAnalysisException(ex);
      }
    }

    // Identify tokens representing reserved words
    if (Character.isLetter(peek) || peek == '_' || peek == '$') {
      StringBuilder buf = new StringBuilder();

      do {
        buf.append((char) peek);
        readChar();
      } while (Character.isLetterOrDigit(peek) || peek == '_');
      String s = buf.toString().toLowerCase();

      if (functions.names().contains(s)) {
        return getToken(FUNCTION, s);
      }
      if ("true".equals(s)) {
        return getToken(TRUE, false);
      }
      if ("false".equals(s)) {
        return getToken(FALSE, false);
      }
      if ("if".equals(s)) {
        return getToken(IF, false);
      }
      if ("else".equals(s)) {
        return getToken(ELSE, false);
      }

      return getToken(VARIABLE, s);
    }

    if (peek == -1) {
      throw new LexicalAnalysisException("Unexpected end of computed point expression string");
    }
    throw new LexicalAnalysisException(
        "Unexpected character '" + (char) peek + "' at " + startPosition);
  }

  private boolean readChar(char c) {
    readChar();
    if (peek != c) {
      return false;
    }
    return true;
  }


  private void readChar() {
    try {
      peek = in.read();
    } catch (IOException e) {
      // This should never happen, because we are only using StringReaders
      throw new AssertionError("Unexpected exception", e);
    }
    position.advanceCharacter();
  }

  private Token getToken(TokenType type) {
    return getToken(type, true);
  }

  private Token getToken(TokenType type, String lexeme) {
    return new Token(startPosition, type, lexeme);
  }

  private Token getToken(TokenType type, boolean resetPeek) {
    if (resetPeek) {
      peek = ' ';
    }
    return new Token(startPosition, type);
  }


}

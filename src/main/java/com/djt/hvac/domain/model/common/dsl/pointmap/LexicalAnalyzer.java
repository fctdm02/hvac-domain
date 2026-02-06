package com.djt.hvac.domain.model.common.dsl.pointmap;

import static com.djt.hvac.domain.model.common.dsl.pointmap.TokenType.END_COMPLEX_VARIABLE;
import static com.djt.hvac.domain.model.common.dsl.pointmap.TokenType.END_SIMPLE_VARIABLE;
import static com.djt.hvac.domain.model.common.dsl.pointmap.TokenType.EOE;
import static com.djt.hvac.domain.model.common.dsl.pointmap.TokenType.SPECIAL_CHARS;
import static com.djt.hvac.domain.model.common.dsl.pointmap.TokenType.START_COMPLEX_VARIABLE;
import static com.djt.hvac.domain.model.common.dsl.pointmap.TokenType.START_SIMPLE_VARIABLE;
import static com.djt.hvac.domain.model.common.dsl.pointmap.TokenType.STRING_LITERAL;
import static com.djt.hvac.domain.model.common.dsl.pointmap.TokenType.WILDCARD_LITERAL;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.Reader;

class LexicalAnalyzer {

  private final Reader input;
  private int peek = ' ';
  private Position position;
  private Position startPosition;


  LexicalAnalyzer(Reader input) {
    requireNonNull(input, "input cannot be null");
    this.input = input;
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
      case '{':
        return getToken(START_SIMPLE_VARIABLE);
      case '}':
        return getToken(END_SIMPLE_VARIABLE);
      case '[':
        return getToken(START_COMPLEX_VARIABLE);
      case ']':
        return getToken(END_COMPLEX_VARIABLE);
      case '*':
        return getToken(WILDCARD_LITERAL);
      case -1:
        return getToken(EOE, false);
    }

    // TODO: Disallow illegal OpenTSDB characters
    // Everything else is a string literal
    StringBuilder buf = new StringBuilder();
    for (;; readChar()) {
      // skip whitespace
      if (peek == ' ' || peek == '\t' || peek == '\n' || peek == '\r') {
        if (peek == '\n') {
          position.advanceLine();
        }
        continue;
      } else if (!SPECIAL_CHARS.contains((char) peek)) {
        if (!isValidOpenTsdbCharacter((char) peek)) {
          throw new LexicalAnalysisException(
              "Unexpected character '" + (char) peek + "' at " + startPosition);
        }
        buf.append((char) peek);
      } else {
        break;
      }
    }
    String lexeme = buf.toString();
    return getStringLiteralToken(lexeme);

  }

  private void readChar() {
    try {
      peek = input.read();
    } catch (IOException e) {
      // Wrap the checked exception as an unchecked exception
      throw new LexicalAnalysisException(e);
    }
    position.advanceCharacter();
  }

  private Token getToken(TokenType type) {
    return getToken(type, true);
  }

  private Token getToken(TokenType type, boolean resetPeek) {
    if (resetPeek) {
      peek = ' ';
    }
    return new Token(startPosition, type);
  }

  private Token getStringLiteralToken(String lexeme) {
    return new Token(startPosition, STRING_LITERAL, lexeme);
  }

  static boolean isValidOpenTsdbCharacter(char c) {
    return Character.isLetterOrDigit(c) || c == '-' || c == '_' || c == '.' || c == '/';
  }

}

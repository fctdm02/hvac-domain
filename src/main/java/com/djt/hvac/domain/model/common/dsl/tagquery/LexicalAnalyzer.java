package com.djt.hvac.domain.model.common.dsl.tagquery;

import static com.djt.hvac.domain.model.common.dsl.tagquery.TokenType.AND;
import static com.djt.hvac.domain.model.common.dsl.tagquery.TokenType.EOE;
import static com.djt.hvac.domain.model.common.dsl.tagquery.TokenType.LEFT_PAREN;
import static com.djt.hvac.domain.model.common.dsl.tagquery.TokenType.NOT;
import static com.djt.hvac.domain.model.common.dsl.tagquery.TokenType.OR;
import static com.djt.hvac.domain.model.common.dsl.tagquery.TokenType.RIGHT_PAREN;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.StringReader;

class LexicalAnalyzer {
  private final StringReader in;
  private int peek = ' ';
  private Position position;
  private Position startPosition;


  static LexicalAnalyzer create(StringReader reader) {
    requireNonNull(reader, "reader cannot be null");
    return new LexicalAnalyzer(reader);
  }

  private LexicalAnalyzer(StringReader in) {
    this.in = in;
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
      case '!':
        return getToken(NOT);
      case '&':
        if (readChar('&')) {
          return getToken(AND);
        }
      case '|':
        if (readChar('|')) {
          return getToken(OR);
        }
      case -1:
        return getToken(EOE, false);
    }

    // Identify tokens representing tags
    if (Character.isLetterOrDigit(peek) || peek == '-' || peek == '_' || peek == '.') {
      StringBuilder buf = new StringBuilder();

      do {
        buf.append((char) peek);
        readChar();
      } while (Character.isLetterOrDigit(peek) || peek == '-' || peek == '_' || peek == '.');
      String s = buf.toString();
      return getTagToken(s);
    }

    if (peek == -1) {
      throw new LexicalAnalysisException("Unexpected end of expression string");
    }

    throw new LexicalAnalysisException(
        "Unexpected character '" + (char) peek + "' at " + startPosition);
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

  private Token getTagToken(String lexeme) {
    return new Token(startPosition, TokenType.TAG, lexeme);
  }

  private boolean readChar(char c) {
    readChar();
    if (peek != c) {
      return false;
    }
    peek = ' ';
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


}

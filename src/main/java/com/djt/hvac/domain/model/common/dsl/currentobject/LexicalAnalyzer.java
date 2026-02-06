package com.djt.hvac.domain.model.common.dsl.currentobject;

import static com.djt.hvac.domain.model.common.dsl.currentobject.TokenType.COMMA;
import static com.djt.hvac.domain.model.common.dsl.currentobject.TokenType.EOE;
import static com.djt.hvac.domain.model.common.dsl.currentobject.TokenType.EQUALS;
import static com.djt.hvac.domain.model.common.dsl.currentobject.TokenType.LEFT_PAREN;
import static com.djt.hvac.domain.model.common.dsl.currentobject.TokenType.PERIOD;
import static com.djt.hvac.domain.model.common.dsl.currentobject.TokenType.PIPE;
import static com.djt.hvac.domain.model.common.dsl.currentobject.TokenType.RIGHT_PAREN;
import static com.djt.hvac.domain.model.common.dsl.currentobject.TokenType.WILDCARD;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

class LexicalAnalyzer {
  private final Map<String, TokenType> reservedWords;
  private final StringReader in;
  private int peek = ' ';
  private Position position;
  private Position startPosition;

  static LexicalAnalyzer create(StringReader reader) {
    Objects.requireNonNull(reader, "reader cannot be null");
    return new LexicalAnalyzer(reader);
  }

  private LexicalAnalyzer(StringReader in) {
    this.in = in;
    Map<String, TokenType> reservedWords = initReservedWords();
    this.reservedWords = ImmutableMap.copyOf(reservedWords);
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
      case '.':
        return getToken(PERIOD);
      case '(':
        return getToken(LEFT_PAREN);
      case ')':
        return getToken(RIGHT_PAREN);
      case '=':
        return getToken(EQUALS);
      case ',':
        return getToken(COMMA);
      case '|':
        return getToken(PIPE);
      case '*':
        return getToken(WILDCARD);
      case -1:
        return getToken(EOE, false);
    }

    // Identify tokens representing reserved words
    if (Character.isLetterOrDigit(peek) || peek == '-' || peek == '_') {
      StringBuilder buf = new StringBuilder();

      do {
        buf.append((char) peek);
        readChar();
      } while (Character.isLetterOrDigit(peek) || peek == '-' || peek == '_');
      String s = buf.toString();

      TokenType type = reservedWords.get(s);
      if (type != null) {
        return getToken(type, false);
      } else {
        return getTagNameToken(s);
      }
    }

    if (peek == -1) {
      throw new LexicalAnalysisException("Unexpected end of expression string");
    }

    throw new LexicalAnalysisException(
        "Unexpected character '" + (char) peek + "' at " + startPosition);
  }

  private Map<String, TokenType> initReservedWords() {
    Map<String, TokenType> reservedWords = Maps.newHashMap();
    for (TokenType type : TokenType.values()) {
      if (type.isReservedWord()) {
        reservedWords.put(type.getValue(), type);
      }
    }
    return reservedWords;
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

  private Token getToken(TokenType type, boolean resetPeek) {
    if (resetPeek) {
      peek = ' ';
    }
    return new Token(startPosition, type);
  }

  private Token getTagNameToken(String lexeme) {
    return new Token(startPosition, TokenType.TAG, lexeme);
  }


}

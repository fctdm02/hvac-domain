package com.djt.hvac.domain.model.common.dsl.computedpoint;

import java.io.IOException;
import java.io.Reader;
import java.util.Set;

import com.djt.hvac.domain.model.common.dsl.computedpoint.Token.Type;
import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeRangeSet;

import static com.djt.hvac.domain.model.common.dsl.computedpoint.Token.Type.DIVIDE;
import static com.djt.hvac.domain.model.common.dsl.computedpoint.Token.Type.EOF;
import static com.djt.hvac.domain.model.common.dsl.computedpoint.Token.Type.FALSE;
import static com.djt.hvac.domain.model.common.dsl.computedpoint.Token.Type.LEFT_PAREN;
import static com.djt.hvac.domain.model.common.dsl.computedpoint.Token.Type.MINUS;
import static com.djt.hvac.domain.model.common.dsl.computedpoint.Token.Type.MULTIPLY;
import static com.djt.hvac.domain.model.common.dsl.computedpoint.Token.Type.PLUS;
import static com.djt.hvac.domain.model.common.dsl.computedpoint.Token.Type.TRUE;
import static java.util.Objects.requireNonNull;

class LexicalAnalyzer {

  private static final RangeSet<Integer> STRING_CHARS;
  private static final Set<Character> ESCAPE_CHARS;

  private final Reader in;
  private int peek = ' ';
  private Position position;
  private Position startPosition;


  static {
    RangeSet<Integer> stringChars = TreeRangeSet.create();
    stringChars.add(Range.closed((int) '\u0020', (int) '\u0021'));
    stringChars.add(Range.closed((int) '\u0023', (int) '\u005B'));
    stringChars.add(Range.closed((int) '\u005D', (int) '\uFFFF'));
    STRING_CHARS = ImmutableRangeSet.copyOf(stringChars);

    Set<Character> escapeChars = Sets.newHashSet();
    escapeChars.add('"');
    escapeChars.add('\\');
    escapeChars.add('/');
    escapeChars.add('\b');
    escapeChars.add('\f');
    escapeChars.add('\n');
    escapeChars.add('\r');
    escapeChars.add('\t');
    ESCAPE_CHARS = ImmutableSet.copyOf(escapeChars);
  }

  LexicalAnalyzer create(Reader in) {
    return new LexicalAnalyzer(in);
  }

  public static boolean isValidStringCharacter(char c) {
    return STRING_CHARS.contains((int) c) || ESCAPE_CHARS.contains(c);
  }


  public LexicalAnalyzer(Reader in) {
    this.in = requireNonNull(in, "in cannot be null");
    this.position = new Position();
  }

  public Token nextToken() throws IOException {
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
        return getToken(LEFT_PAREN, startPosition);
      case ')':
        return getToken(Type.RIGHT_PAREN, startPosition);
      case '+':
        return getToken(PLUS, startPosition);
      case '-':
        return getToken(MINUS, startPosition);
      case '*':
        return getToken(MULTIPLY, startPosition);
      case '/':
        return getToken(DIVIDE, startPosition);
      case -1:
        return getToken(EOF, startPosition, false);
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
        return getDoubleToken(v, startPosition);
      }
      double x = v;
      if (peek == '.') {
        StringBuilder buf = new StringBuilder(String.valueOf(v)).append('.');
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
          x = Double.parseDouble(buf.toString());
        } catch (NumberFormatException ex) {
          throw new LexicalAnalysisException(ex);
        }
      }
      if (peek != 'E' && peek != 'e') {
        return getDoubleToken(x, startPosition);
      }

      StringBuilder buf = new StringBuilder(String.valueOf(x));
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
        x = Double.parseDouble(buf.toString());
        return getDoubleToken(x, startPosition);
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

      if ("true".equals(s)) {
        return getToken(TRUE, startPosition, false);
      } else if ("false".equals(s)) {
        return getToken(FALSE, startPosition, false);
      }
      return getVariableToken(s, startPosition);
    }

    if (peek == -1) {
      throw new LexicalAnalysisException("Unexpected end of computed point expression string");
    }
    throw new LexicalAnalysisException(
        "Unexpected character '" + (char) peek + "' at " + startPosition);
  }

  public Position getPosition() {
    return position;
  }

  @SuppressWarnings("unused")
  private boolean readChar(char c) throws IOException {
    readChar();
    if (peek != c) {
      return false;
    }
    peek = ' ';
    return true;
  }


  private void readChar() throws IOException {
    peek = in.read();
    position.advanceCharacter();
  }

  private Token getToken(Type type, Position position) {
    return getToken(type, position, true);
  }

  private Token getToken(Type type, Position position, boolean resetPeek) {
    if (resetPeek) {
      peek = ' ';
    }
    return new Token(type, position);
  }

  private Token getVariableToken(String value, Position position) {
    return new VariableToken(value, position);
  }

  private Token getDoubleToken(double value, Position position) {
    return new DoubleToken(value, position);
  }

}

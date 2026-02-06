package com.djt.hvac.domain.model.common.dsl.baseline;

import java.io.IOException;

interface LexicalAnalyzer {
  
  static boolean isValidStringCharacter(char c) {
    return LexicalAnalyzerImpl.isValidStringCharacter(c);
  }
  
  public Token nextToken() throws IOException;
  
  public Position getPosition();
  
}

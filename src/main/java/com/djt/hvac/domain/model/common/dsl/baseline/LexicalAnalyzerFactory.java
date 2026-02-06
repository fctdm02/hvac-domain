package com.djt.hvac.domain.model.common.dsl.baseline;

import java.io.Reader;

interface LexicalAnalyzerFactory {
  
  static LexicalAnalyzerFactory create () {
    return LexicalAnalyzerFactoryImpl.create();
  }
  
  public LexicalAnalyzer create (Reader in);

}

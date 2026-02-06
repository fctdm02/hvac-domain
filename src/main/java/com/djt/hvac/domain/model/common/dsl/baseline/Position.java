package com.djt.hvac.domain.model.common.dsl.baseline;



interface Position {
  
  static Position copyOf(Position position) {
    return new BasicPosition(position.getLine(), position.getCharacter());
  }

  public int getLine();
  public int getCharacter();
  
}

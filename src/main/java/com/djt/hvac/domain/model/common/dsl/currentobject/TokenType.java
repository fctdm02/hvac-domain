package com.djt.hvac.domain.model.common.dsl.currentobject;

import static com.djt.hvac.domain.model.common.dsl.currentobject.TokenTypeCategory.ARGUMENT;
import static com.djt.hvac.domain.model.common.dsl.currentobject.TokenTypeCategory.FUNCTION;
import static com.djt.hvac.domain.model.common.dsl.currentobject.TokenTypeCategory.MODEL_TYPE;
import static com.djt.hvac.domain.model.common.dsl.currentobject.TokenTypeCategory.NODE_TYPE;
import static com.djt.hvac.domain.model.common.dsl.currentobject.TokenTypeCategory.SPECIAL;
import static com.djt.hvac.domain.model.common.dsl.currentobject.TokenTypeCategory.SYMBOL;
import static com.djt.hvac.domain.model.common.dsl.currentobject.TokenTypeCategory.WORD;

enum TokenType {

  // @formatter:off
  // Symbols
  PERIOD(".", SYMBOL),
  LEFT_PAREN("(", SYMBOL),
  RIGHT_PAREN(")", SYMBOL),
  EQUALS("=", SYMBOL),
  COMMA(",", SYMBOL),
  PIPE("|", SYMBOL),
  WILDCARD("*", SYMBOL),
  
  // Arguments
  MODEL("model", ARGUMENT, true),
  TAGS("tags", ARGUMENT, true),
  TYPE("type", ARGUMENT, true),
  SELECT ("select", ARGUMENT, true),
  
  // Functions
  ELSEIF("elseIf", FUNCTION, true),
  PARENT("parent", FUNCTION, true), 
  ANCESTOR("ancestor", FUNCTION, true), 
  CHILD("child", FUNCTION, true), 
  DESCENDANT("descendant", FUNCTION, true),
  
  // Deprecated Functions
  PARENT_EQUIPMENT("parentEquipment", FUNCTION, true), 
  ANCESTOR_EQUIPMENT("ancestorEquipment", FUNCTION, true), 
  CHILD_EQUIPMENT("childEquipment", FUNCTION, true), 
  DESCENDANT_EQUIPMENT("descendantEquipment", FUNCTION, true), 
  
  // Types
  PORTFOLIO("portfolio", NODE_TYPE, true),
  SITE("site", NODE_TYPE, true),
  BUILDING("building", NODE_TYPE, true),
  SUB_BUILDING("sub-building", NODE_TYPE, true),
  FLOOR("floor", NODE_TYPE, true),
  ZONE("zone", NODE_TYPE, true),
  AREA("area", NODE_TYPE, true),
  METER("meter", NODE_TYPE, true),
  EQUIPMENT("equipment", NODE_TYPE, true),
  PLANT("plant", NODE_TYPE, true),
  LOOP("loop", NODE_TYPE, true),
  
  // Models
  STANDARD("standard", MODEL_TYPE, true),
  AIR_SUPPLY("airSupply", MODEL_TYPE, true),
  HOT_WATER("hotWater", MODEL_TYPE, true),
  CHILLED_WATER("chilledWater", MODEL_TYPE, true),
  STEAM("steam", MODEL_TYPE, true),
  
  // Tag
  TAG("a tag", WORD),
  
  // End of file
  EOE("end of expression", SPECIAL);
  // @formatter:on

  private final String value;
  private final boolean reservedWord;
  private final TokenTypeCategory category;

  private TokenType(String value, TokenTypeCategory category) {
    this(value, category, false);
  }

  private TokenType(String value, TokenTypeCategory category, boolean reservedWord) {
    this.value = value;
    this.category = category;
    this.reservedWord = reservedWord;
  }

  String getValue() {
    return value;
  }

  TokenTypeCategory getCategory() {
    return category;
  }

  boolean isReservedWord() {
    return reservedWord;
  }

}

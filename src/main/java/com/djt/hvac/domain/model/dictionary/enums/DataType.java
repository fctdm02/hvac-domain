package com.djt.hvac.domain.model.dictionary.enums;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public enum DataType {
  NUMERIC(1, "Numeric"),
  BOOLEAN(2, "Boolean"),
  ENUM(3, "Enum"),
  STRING(4, "String");
  
  private static final Map<Integer, DataType> DATA_TYPES;
  
  private final int id;
  private final String name;
  
  static {
    Map<Integer, DataType> types = Maps.newHashMap();
    for (DataType type : DataType.values()) {
      types.put(type.id, type);
    }
    DATA_TYPES = ImmutableMap.copyOf(types);
  }
  
  public static DataType get(int id) {
    return DATA_TYPES.get(id);
  }
  
  private DataType(int id, String name) {
    this.id = id;
    this.name = name;
  }
  
  public int getId() {
    return id;
  }
  
  public String getName() {
    return name;
  }
  
  public static DataType getDataTypeFromNiagaraPointType(String pointType) {
    
    if (pointType != null) {
      pointType = pointType.trim();
      if (pointType.equalsIgnoreCase("NumericPoint") || pointType.equalsIgnoreCase("NumericWritable")) {
        return NUMERIC;
      } else if (pointType.equalsIgnoreCase("StringPoint") || pointType.equalsIgnoreCase("StringWritable")) {
        return STRING;
      } else if (pointType.equalsIgnoreCase("BooleanPoint") || pointType.equalsIgnoreCase("BooleanWritable")) {
        return BOOLEAN;
      } else if (pointType.equalsIgnoreCase("EnumPoint") || pointType.equalsIgnoreCase("EnumWritable")) {
        return ENUM;
      } else {
        throw new IllegalStateException("Unsupported Niagara point type: ["
            + pointType
            + "], supported types are: [EnumPoint, BooleanWritable, StringPoint, NumericPoint, BooleanPoint, StringWritable, NumericWritable, or EnumWritable].");
      }
    }
    throw new IllegalStateException("Expected a non-null/non-empty Niagara point type: [EnumPoint, BooleanWritable, StringPoint, NumericPoint, BooleanPoint, StringWritable, NumericWritable, or EnumWritable].");
  }
}
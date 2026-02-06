package com.djt.hvac.domain.model.common.dsl.baseline;

public interface Literal<T> extends Expression<T> {

  public static String getLabel(Class<? extends Literal<?>> clazz) {
    if (NumericLiteral.class.equals(clazz)) {
      return "a number";
    } else if (BooleanLiteral.class.equals(clazz)) {
      return "a boolean literal";
    }
    throw new AssertionError("Inrecognized literal type " + clazz.getName());
  }

}

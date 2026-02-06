package com.djt.hvac.domain.model.common.dsl.computedpoint.v2.function;

public class InvalidFunctionDefinitionException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private static final String defaultMessage(Class<?> c) {
    return "Class '" + c.getName() + "' is not a valid function";
  }

  private static final String defaultMessage(Class<?> c, String message) {
    return defaultMessage(c) + " - " + message;
  }


  public InvalidFunctionDefinitionException(Class<?> c) {
    super(defaultMessage(c));
  }

  public InvalidFunctionDefinitionException(Class<?> c, String message) {
    super(defaultMessage(c, message));
  }

  public InvalidFunctionDefinitionException(Class<?> c, Throwable cause) {
    super(cause);
  }

  public InvalidFunctionDefinitionException(Class<?> c, String message, Throwable cause) {
    super(defaultMessage(c, message), cause);
  }

  public InvalidFunctionDefinitionException(Class<?> c, String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(defaultMessage(c, message), cause, enableSuppression, writableStackTrace);
  }


}

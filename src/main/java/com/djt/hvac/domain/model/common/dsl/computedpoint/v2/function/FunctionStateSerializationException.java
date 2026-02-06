package com.djt.hvac.domain.model.common.dsl.computedpoint.v2.function;

public class FunctionStateSerializationException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public FunctionStateSerializationException() {}

  public FunctionStateSerializationException(String message) {
    super(message);
  }

  public FunctionStateSerializationException(Throwable cause) {
    super(cause);
  }

  public FunctionStateSerializationException(String message, Throwable cause) {
    super(message, cause);
  }

  public FunctionStateSerializationException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }


}

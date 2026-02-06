package com.djt.hvac.domain.model.common.exception;

public final class StaleDataException extends Exception {

  private static final long serialVersionUID = 1L;

  public StaleDataException(String message) {
    super(message);
  }
  
  public StaleDataException(String message, Throwable t) {
    super(message, t);
  }
}
package com.djt.hvac.domain.model.email.exception;

public final class EmailClientException extends Exception {

  private static final long serialVersionUID = 1L;

  public EmailClientException(String message) {
    super(message);
  }
  
  public EmailClientException(String message, Throwable t) {
    super(message, t);
  }
}
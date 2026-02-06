package com.djt.hvac.domain.model.stripe.exception;

public final class StripeClientException extends Exception {

  private static final long serialVersionUID = 1L;

  public StripeClientException(String message) {
    super(message);
  }
  
  public StripeClientException(String message, Throwable t) {
    super(message, t);
  }
}
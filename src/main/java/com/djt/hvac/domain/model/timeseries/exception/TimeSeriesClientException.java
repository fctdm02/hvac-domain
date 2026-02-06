package com.djt.hvac.domain.model.timeseries.exception;

public final class TimeSeriesClientException extends Exception {

  private static final long serialVersionUID = 1L;

  public TimeSeriesClientException(String message) {
    super(message);
  }
  
  public TimeSeriesClientException(String message, Throwable t) {
    super(message, t);
  }
}
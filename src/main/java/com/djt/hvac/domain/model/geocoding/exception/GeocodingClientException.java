//@formatter:off
package com.djt.hvac.domain.model.geocoding.exception;

public final class GeocodingClientException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public GeocodingClientException(String message) {
    super(message);
  }
  
  public GeocodingClientException(String message, Throwable t) {
    super(message, t);
  }
}
//@formatter:on
//@formatter:off
package com.djt.hvac.domain.model.geocoding.exception;

public final class GeocodingClientLookupFailureException extends Exception {

  private static final long serialVersionUID = 1L;

  public GeocodingClientLookupFailureException(String message) {
    super(message);
  }
  
  public GeocodingClientLookupFailureException(String message, Throwable t) {
    super(message, t);
  }
}
//@formatter:on
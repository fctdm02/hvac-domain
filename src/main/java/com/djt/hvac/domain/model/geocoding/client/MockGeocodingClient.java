//@formatter:off
package com.djt.hvac.domain.model.geocoding.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.geocoding.dto.GeocodingAddress;
import com.djt.hvac.domain.model.geocoding.exception.GeocodingClientLookupFailureException;

public class MockGeocodingClient implements GeocodingClient {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(MockGeocodingClient.class);
  
  private static final MockGeocodingClient INSTANCE = new MockGeocodingClient();
  public static final MockGeocodingClient getInstance() {
    return INSTANCE;
  }

  private MockGeocodingClient() {
  }
  
  @Override
  public GeocodingAddress geocode(GeocodingAddress request) throws GeocodingClientLookupFailureException {
    
    GeocodingAddress response = GeocodingAddress
        .builder()
        .withAddress("325 E 7TH ")
        .withCity("Royal Oak")
        .withStateOrProvince("MI")
        .withPostalCode("48067")
        .withCountryCode("us")
        .withLatitude(42.331429)
        .withLongitude(-83.045753)
        .build();
    
    LOGGER.debug("geocode request: {}, response: {}", request, response);
    
    return response;
  }
}
//@formatter:on
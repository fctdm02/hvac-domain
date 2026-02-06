//@formatter:off
package com.djt.hvac.domain.model.geocoding.client;

import com.djt.hvac.domain.model.geocoding.dto.GeocodingAddress;
import com.djt.hvac.domain.model.geocoding.exception.GeocodingClientLookupFailureException;

/**
 * 
 * @author tommyers
 *
 */
public interface GeocodingClient {
  
  /**
   * 
   * @param request
   * 
   * @return The <code>GeocodingAddress</code> that is a modified version of the request, except that the
   * lat/long are filled in, as well as verified elements for address, city, state, postal code and country code
   */
  GeocodingAddress geocode(GeocodingAddress request) throws GeocodingClientLookupFailureException;
}
//@formatter:on
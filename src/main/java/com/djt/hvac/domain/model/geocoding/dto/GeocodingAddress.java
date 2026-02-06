//@formatter:off
package com.djt.hvac.domain.model.geocoding.dto;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = GeocodingAddress.Builder.class)
public class GeocodingAddress {
  private final String address;
  private final String city;
  private final String stateOrProvince;
  private final String postalCode;
  private final String countryCode;
  private final Double latitude;
  private final Double longitude;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (GeocodingAddress geocodingAddress) {
    return new Builder(geocodingAddress);
  }

  private GeocodingAddress (Builder builder) {
    this.address = builder.address;
    this.city = builder.city;
    this.stateOrProvince = builder.stateOrProvince;
    this.postalCode = builder.postalCode;
    this.countryCode = builder.countryCode;
    this.latitude = builder.latitude;
    this.longitude = builder.longitude;
  }

  public String getAddress() {
    return address;
  }

  public String getCity() {
    return city;
  }

  public String getStateOrProvince() {
    return stateOrProvince;
  }
  
  public String getPostalCode() {
    return postalCode;
  }

  public String getCountryCode() {
    return countryCode;
  }
  
  public Double getLatitude() {
    if (latitude != null) {
      return latitude;  
    }
    return Double.valueOf(0);
  }

  public Double getLongitude() {
    if (longitude != null) {
      return longitude;  
    }
    return Double.valueOf(0);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((address == null) ? 0 : address.hashCode());
    result = prime * result + ((city == null) ? 0 : city.hashCode());
    result = prime * result + ((countryCode == null) ? 0 : countryCode.hashCode());
    long temp;
    temp = Double.doubleToLongBits(latitude);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(longitude);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + ((postalCode == null) ? 0 : postalCode.hashCode());
    result = prime * result + ((stateOrProvince == null) ? 0 : stateOrProvince.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    GeocodingAddress other = (GeocodingAddress) obj;
    if (address == null) {
      if (other.address != null)
        return false;
    } else if (!address.equals(other.address))
      return false;
    if (city == null) {
      if (other.city != null)
        return false;
    } else if (!city.equals(other.city))
      return false;
    if (countryCode == null) {
      if (other.countryCode != null)
        return false;
    } else if (!countryCode.equals(other.countryCode))
      return false;
    if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude))
      return false;
    if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude))
      return false;
    if (postalCode == null) {
      if (other.postalCode != null)
        return false;
    } else if (!postalCode.equals(other.postalCode))
      return false;
    if (stateOrProvince == null) {
      if (other.stateOrProvince != null)
        return false;
    } else if (!stateOrProvince.equals(other.stateOrProvince))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("GeocodingAddress [address=")
        .append(address)
        .append(", city=")
        .append(city)
        .append(", stateOrProvince=")
        .append(stateOrProvince)
        .append(", postalCode=")
        .append(postalCode)
        .append(", countryCode=")
        .append(countryCode)
        .append(", latitude=")
        .append(latitude)
        .append(", longitude=")
        .append(longitude)
        .append("]")
        .toString();
  }

  @JsonPOJOBuilder
  public static class Builder {
    private String address;
    private String city;
    private String stateOrProvince;
    private String postalCode;
    private String countryCode;
    private Double latitude;
    private Double longitude;

    private Builder() {}

    private Builder(GeocodingAddress geocodingAddress) {
      requireNonNull(geocodingAddress, "geocodingAddress cannot be null");
      this.address = geocodingAddress.address;
      this.city = geocodingAddress.city;
      this.stateOrProvince = geocodingAddress.stateOrProvince;
      this.postalCode = geocodingAddress.postalCode;
      this.countryCode = geocodingAddress.countryCode;
      this.latitude = geocodingAddress.latitude;
      this.longitude = geocodingAddress.longitude;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withAddress(String address) {
      requireNonNull(address, "address cannot be null");
      this.address = address;
      return this;
    }

    public Builder withCity(String city) {
      requireNonNull(city, "city cannot be null");
      this.city = city;
      return this;
    }

    public Builder withStateOrProvince(String stateOrProvince) {
      requireNonNull(stateOrProvince, "stateOrProvince cannot be null");
      this.stateOrProvince = stateOrProvince;
      return this;
    }
    
    public Builder withPostalCode(String postalCode) {
      this.postalCode = postalCode;
      return this;
    }

    public Builder withCountryCode(String countryCode) {
      this.countryCode = countryCode;
      return this;
    }
    
    public Builder withLatitude(double latitude) {
      this.latitude = latitude;
      return this;
    }

    public Builder withLongitude(double longitude) {
      this.longitude = longitude;
      return this;
    }

    public GeocodingAddress build() {
      requireNonNull(address, "address cannot be null");
      requireNonNull(city, "city cannot be null");
      requireNonNull(stateOrProvince, "stateOrProvince cannot be null");
      return new GeocodingAddress(this);
    }
  }
}
//@formatter:on
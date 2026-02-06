package com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = BuildingAddressData.Builder.class)
public class BuildingAddressData implements Serializable {
  
  private static final long serialVersionUID = 1L;
  
  private final String rubyTimeZoneLabel;
  private final String address;
  private final String city;
  private final String stateOrProvince;
  private final String postalCode;
  private final String countryCode;
  private final String latitude;
  private final String longitude;
  private final Integer weatherStationId;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (BuildingAddressData updateBuildingNodeRequest) {
    return new Builder(updateBuildingNodeRequest);
  }

  private BuildingAddressData (Builder builder) {
    this.rubyTimeZoneLabel = builder.rubyTimeZoneLabel;
    this.address = builder.address;
    this.city = builder.city;
    this.stateOrProvince = builder.stateOrProvince;
    this.postalCode = builder.postalCode;
    this.countryCode = builder.countryCode;
    this.latitude = builder.latitude;
    this.longitude = builder.longitude;
    this.weatherStationId = builder.weatherStationId;
  }
  
  public String getRubyTimeZoneLabel() {
    return rubyTimeZoneLabel;
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
  
  public String getLatitude() {
    return latitude;
  }

  public String getLongitude() {
    return longitude;
  }

  public Integer getWeatherStationId() {
    return weatherStationId;
  }

  @JsonPOJOBuilder
  public static class Builder {
    private String rubyTimeZoneLabel;
    private String address;
    private String city;
    private String stateOrProvince;
    private String postalCode;
    private String countryCode;
    private String latitude;
    private String longitude;
    private Integer weatherStationId;

    private Builder() {}

    private Builder(BuildingAddressData updateBuildingNodeRequest) {
      requireNonNull(updateBuildingNodeRequest, "updateBuildingNodeRequest cannot be null");
      this.rubyTimeZoneLabel = updateBuildingNodeRequest.rubyTimeZoneLabel;
      this.address = updateBuildingNodeRequest.address;
      this.city = updateBuildingNodeRequest.city;
      this.stateOrProvince = updateBuildingNodeRequest.stateOrProvince;
      this.postalCode = updateBuildingNodeRequest.postalCode;
      this.countryCode = updateBuildingNodeRequest.countryCode;
      this.latitude = updateBuildingNodeRequest.latitude;
      this.longitude = updateBuildingNodeRequest.longitude;
      this.weatherStationId = updateBuildingNodeRequest.weatherStationId;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withRubyTimeZoneLabel(String rubyTimeZoneLabel) {
      requireNonNull(rubyTimeZoneLabel, "rubyTimeZoneLabel cannot be null");
      this.rubyTimeZoneLabel = rubyTimeZoneLabel;
      return this;
    }

    public Builder withAddress(String address) {
      this.address = address;
      return this;
    }

    public Builder withCity(String city) {
      this.city = city;
      return this;
    }

    public Builder withStateOrProvince(String stateOrProvince) {
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
    
    public Builder withLatitude(String latitude) {
      this.latitude = latitude;
      return this;
    }

    public Builder withLongitude(String longitude) {
      this.longitude = longitude;
      return this;
    }

    public Builder withWeatherStationId(Integer weatherStationId) {
      this.weatherStationId = weatherStationId;
      return this;
    }

    public BuildingAddressData build() {
      return new BuildingAddressData(this);
    }
  }
}
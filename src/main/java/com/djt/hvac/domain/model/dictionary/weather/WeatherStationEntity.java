package com.djt.hvac.domain.model.dictionary.weather;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;

public class WeatherStationEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;
  
  private static final Logger LOGGER = LoggerFactory.getLogger(WeatherStationEntity.class);
  
  public static final String DEFAULT_TIME_ZONE = "America/New_York";
  
  private final String code;
  private final String city;
  private final String stateOrProvince;
  private final String countryCode;
  private final String timezone;
  private final String latitude;
  private final String longitude;  
  private GlobalComputedPointEntity oatCurrentGlobalPoint;
  private GlobalComputedPointEntity oatCurrentSiGlobalPoint;
  private GlobalComputedPointEntity rhCurrentGlobalPoint;
  
  public WeatherStationEntity(
      Integer persistentIdentity,
      String code,
      String city,
      String stateOrProvince,
      String countryCode,
      String timezone,
      String latitude,
      String longitude,
      GlobalComputedPointEntity oatCurrentGlobalPoint,
      GlobalComputedPointEntity oatCurrentSiGlobalPoint,
      GlobalComputedPointEntity rhCurrentGlobalPoint) {
    super(persistentIdentity);
    requireNonNull(code, "code cannot be null");
    requireNonNull(city, "city cannot be null");
    requireNonNull(stateOrProvince, "stateOrProvince cannot be null");
    this.code = code;
    this.city = city;
    this.stateOrProvince = stateOrProvince;
    
    if (countryCode == null) {
      this.countryCode = countryCode;
    } else {
      this.countryCode = "US";
    }
    
    if (timezone != null) {
      this.timezone = timezone;
    } else {
      this.timezone = DEFAULT_TIME_ZONE;
      LOGGER.error("Time zone not specified for weather station: [{}], using default value of: [{}]",
          code,
          DEFAULT_TIME_ZONE);
    }
    if (latitude == null 
        || latitude.trim().isEmpty() 
        || longitude == null 
        || longitude.trim().isEmpty()) {
      LOGGER.error("Lat/Long zone not specified for weather station: [{}]", code);
    }
    this.latitude = latitude;
    this.longitude = longitude;
    this.oatCurrentGlobalPoint = oatCurrentGlobalPoint;
    this.oatCurrentSiGlobalPoint = oatCurrentSiGlobalPoint;
    this.rhCurrentGlobalPoint = rhCurrentGlobalPoint;
  }
    
  public String getCode() {
    return code;
  }

  public String getCity() {
    return city;
  }

  public String getStateOrProvince() {
    return stateOrProvince;
  }

  public String getCountryCode() {
    return countryCode;
  }

  public String getTimezone() {
    return timezone;
  }
  
  public TimeZone getTimezoneAsTimeZone() {
    return TimeZone.getTimeZone(timezone);
  }

  public String getLatitude() {
    return latitude;
  }

  public String getLongitude() {
    return longitude;
  }

  public GlobalComputedPointEntity getOatCurrentGlobalPoint() {
    
    return oatCurrentGlobalPoint;
  }
  
  public void setOatCurrentGlobalPoint(GlobalComputedPointEntity oatCurrentGlobalPoint) {
    
    this.oatCurrentGlobalPoint = oatCurrentGlobalPoint;
    setIsModified("oatCurrentGlobalPoint");
  }

  public GlobalComputedPointEntity getOatCurrentSiGlobalPoint() {
    
    return oatCurrentSiGlobalPoint;
  }
  
  public void setOatCurrentSiGlobalPoint(GlobalComputedPointEntity oatCurrentSiGlobalPoint) {
    
    this.oatCurrentSiGlobalPoint = oatCurrentSiGlobalPoint;
    setIsModified("oatCurrentSiGlobalPoint");
  }
  
  public GlobalComputedPointEntity getRhCurrentGlobalPoint() {
    
    return rhCurrentGlobalPoint;
  }  

  public void setRhCurrentGlobalPoint(GlobalComputedPointEntity rhCurrentGlobalPoint) {
    
    this.rhCurrentGlobalPoint = rhCurrentGlobalPoint;
    setIsModified("rhCurrentGlobalPoint");
  }
  
  @Override
  public String getNaturalIdentity() {
    return getCode();
  }  
  
  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
  }
}

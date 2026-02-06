package com.djt.hvac.domain.model.dictionary.dto.weather;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/*
<pre>
select 
  ws.id as ws_id,
  ws.code,
  ws.city,
  ws.state_or_province,
  ws.country_code,
  j.name AS timezone,
  ws.latitude,
  ws.longitude,
  gc1.id as oat_id,
  gc1."name" as oat_name,
  gc1.display_name as oat_display_name,
  gc1.description as oat_description,
  gc1.unit_id as oat_unit_id,
  gc1.metric_id as oat_metric_id,
  gc1.earliest_processed_at as oat_earliest_processed_at,
  gc1.last_processed_at as oat_last_processed_at,
  gc2.id as rh_id,
  gc2."name" as rh_name,
  gc2.display_name as rh_display_name,
  gc2.description as rh_description,
  gc2.unit_id as rh_unit_id,
  gc2.metric_id as rh_metric_id,
  gc2.earliest_processed_at as rh_earliest_processed_at,
  gc2.last_processed_at as rh_last_processed_at
from 
  weather_stations ws
  LEFT OUTER JOIN ruby_timezones r ON ws.ruby_timezone_id = r.id
  LEFT OUTER JOIN java_timezones j ON r.java_timezone_id = j.id
  join global_computed_points gc1 on CONCAT(ws.code, ' OaTemp') = gc1.name
  join global_computed_points gc2 on CONCAT(ws.code, ' OaHumidity') = gc2.name
order by
  ws.code;
</pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "ws_id",
    "code",
    "city",
    "state_or_province",
    "country_code",
    "timezone",
    "latitude",
    "longitude",
    "oat_id",
    "oat_name",
    "oat_display_name",
    "oat_description",
    "oat_unit_id",
    "oat_metric_id",
    "oat_earliest_processed_at",
    "oat_last_processed_at",
    "rh_id",
    "rh_name",
    "rh_display_name",
    "rh_description",
    "rh_unit_id",
    "rh_metric_id",
    "rh_earliest_processed_at",
    "rh_last_processed_at",
    "oat_si_id",
    "oat_si_name",
    "oat_si_display_name",
    "oat_si_description",
    "oat_si_unit_id",
    "oat_si_metric_id",
    "oat_si_earliest_processed_at",
    "oat_si_last_processed_at"
})
public class WeatherStationDto implements Serializable
{

    @JsonProperty("ws_id")
    private Integer wsId;
    @JsonProperty("code")
    private String code;
    @JsonProperty("city")
    private String city;
    @JsonProperty("state_or_province")
    private String stateOrProvince;
    @JsonProperty("country_code")
    private String countryCode;
    @JsonProperty("timezone")
    private String timezone;
    @JsonProperty("latitude")
    private String latitude;
    @JsonProperty("longitude")
    private String longitude;
    
    @JsonProperty("oat_id")
    private Integer oatId;
    @JsonProperty("oat_name")
    private String oatName;
    @JsonProperty("oat_display_name")
    private String oatDisplayName;
    @JsonProperty("oat_description")
    private String oatDescription;
    @JsonProperty("oat_unit_id")
    private Integer oatUnitId;
    @JsonProperty("oat_metric_id")
    private String oatMetricId;
    @JsonProperty("oat_earliest_processed_at")
    private String oatEarliestProcessedAt;
    @JsonProperty("oat_last_processed_at")
    private String oatLastProcessedAt;
    
    @JsonProperty("rh_id")
    private Integer rhId;
    @JsonProperty("rh_name")
    private String rhName;
    @JsonProperty("rh_display_name")
    private String rhDisplayName;
    @JsonProperty("rh_description")
    private String rhDescription;
    @JsonProperty("rh_unit_id")
    private Integer rhUnitId;
    @JsonProperty("rh_metric_id")
    private String rhMetricId;
    @JsonProperty("rh_earliest_processed_at")
    private String rhEarliestProcessedAt;
    @JsonProperty("rh_last_processed_at")
    private String rhLastProcessedAt;
    
    @JsonProperty("oat_si_id")
    private Integer oatSiId;
    @JsonProperty("oat_si_name")
    private String oatSiName;
    @JsonProperty("oat_si_display_name")
    private String oatSiDisplayName;
    @JsonProperty("oat_si_description")
    private String oatSiDescription;
    @JsonProperty("oat_si_unit_id")
    private Integer oatSiUnitId;
    @JsonProperty("oat_si_metric_id")
    private String oatSiMetricId;
    @JsonProperty("oat_si_earliest_processed_at")
    private String oatSiEarliestProcessedAt;
    @JsonProperty("oat_si_last_processed_at")
    private String oatSiLastProcessedAt;
    
    private final static long serialVersionUID = -2168097967889596694L;

    @JsonProperty("ws_id")
    public Integer getWsId() {
        return wsId;
    }

    @JsonProperty("ws_id")
    public void setWsId(Integer wsId) {
        this.wsId = wsId;
    }

    @JsonProperty("code")
    public String getCode() {
        return code;
    }

    @JsonProperty("code")
    public void setCode(String code) {
        this.code = code;
    }

    @JsonProperty("city")
    public String getCity() {
        return city;
    }

    @JsonProperty("city")
    public void setCity(String city) {
        this.city = city;
    }

    @JsonProperty("state_or_province")
    public String getStateOrProvince() {
        return stateOrProvince;
    }

    @JsonProperty("state_or_province")
    public void setStateOrProvince(String stateOrProvince) {
        this.stateOrProvince = stateOrProvince;
    }

    @JsonProperty("country_code")
    public String getCountryCode() {
        return countryCode;
    }

    @JsonProperty("country_code")
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
    
    @JsonProperty("timezone")
    public String getTimezone() {
        return timezone;
    }

    @JsonProperty("timezone")
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    @JsonProperty("latitude")
    public String getLatitude() {
        return latitude;
    }

    @JsonProperty("latitude")
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @JsonProperty("longitude")
    public String getLongitude() {
        return longitude;
    }

    @JsonProperty("longitude")
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @JsonProperty("oat_id")
    public Integer getOatId() {
        return oatId;
    }

    @JsonProperty("oat_id")
    public void setOatId(Integer oatId) {
        this.oatId = oatId;
    }

    @JsonProperty("oat_name")
    public String getOatName() {
        return oatName;
    }

    @JsonProperty("oat_name")
    public void setOatName(String oatName) {
        this.oatName = oatName;
    }

    @JsonProperty("oat_display_name")
    public String getOatDisplayName() {
        return oatDisplayName;
    }

    @JsonProperty("oat_display_name")
    public void setOatDisplayName(String oatDisplayName) {
        this.oatDisplayName = oatDisplayName;
    }

    @JsonProperty("oat_description")
    public String getOatDescription() {
        return oatDescription;
    }

    @JsonProperty("oat_description")
    public void setOatDescription(String oatDescription) {
        this.oatDescription = oatDescription;
    }

    @JsonProperty("oat_unit_id")
    public Integer getOatUnitId() {
        return oatUnitId;
    }

    @JsonProperty("oat_unit_id")
    public void setOatUnitId(Integer oatUnitId) {
        this.oatUnitId = oatUnitId;
    }

    @JsonProperty("oat_metric_id")
    public String getOatMetricId() {
        return oatMetricId;
    }

    @JsonProperty("oat_metric_id")
    public void setOatMetricId(String oatMetricId) {
        this.oatMetricId = oatMetricId;
    }

    @JsonProperty("oat_earliest_processed_at")
    public String getOatEarliestProcessedAt() {
        return oatEarliestProcessedAt;
    }

    @JsonProperty("oat_earliest_processed_at")
    public void setOatEarliestProcessedAt(String oatEarliestProcessedAt) {
        this.oatEarliestProcessedAt = oatEarliestProcessedAt;
    }

    @JsonProperty("oat_last_processed_at")
    public String getOatLastProcessedAt() {
        return oatLastProcessedAt;
    }

    @JsonProperty("oat_last_processed_at")
    public void setOatLastProcessedAt(String oatLastProcessedAt) {
        this.oatLastProcessedAt = oatLastProcessedAt;
    }

    
    @JsonProperty("rh_id")
    public Integer getRhId() {
        return rhId;
    }

    @JsonProperty("rh_id")
    public void setRhId(Integer rhId) {
        this.rhId = rhId;
    }

    @JsonProperty("rh_name")
    public String getRhName() {
        return rhName;
    }

    @JsonProperty("rh_name")
    public void setRhName(String rhName) {
        this.rhName = rhName;
    }

    @JsonProperty("rh_display_name")
    public String getRhDisplayName() {
        return rhDisplayName;
    }

    @JsonProperty("rh_display_name")
    public void setRhDisplayName(String rhDisplayName) {
        this.rhDisplayName = rhDisplayName;
    }

    @JsonProperty("rh_description")
    public String getRhDescription() {
        return rhDescription;
    }

    @JsonProperty("rh_description")
    public void setRhDescription(String rhDescription) {
        this.rhDescription = rhDescription;
    }

    @JsonProperty("rh_unit_id")
    public Integer getRhUnitId() {
        return rhUnitId;
    }

    @JsonProperty("rh_unit_id")
    public void setRhUnitId(Integer rhUnitId) {
        this.rhUnitId = rhUnitId;
    }

    @JsonProperty("rh_metric_id")
    public String getRhMetricId() {
        return rhMetricId;
    }

    @JsonProperty("rh_metric_id")
    public void setRhMetricId(String rhMetricId) {
        this.rhMetricId = rhMetricId;
    }

    @JsonProperty("rh_earliest_processed_at")
    public String getRhEarliestProcessedAt() {
        return rhEarliestProcessedAt;
    }

    @JsonProperty("rh_earliest_processed_at")
    public void setRhEarliestProcessedAt(String rhEarliestProcessedAt) {
        this.rhEarliestProcessedAt = rhEarliestProcessedAt;
    }

    @JsonProperty("rh_last_processed_at")
    public String getRhLastProcessedAt() {
        return rhLastProcessedAt;
    }

    @JsonProperty("rh_last_processed_at")
    public void setRhLastProcessedAt(String rhLastProcessedAt) {
        this.rhLastProcessedAt = rhLastProcessedAt;
    }
    
    @JsonProperty("oat_si_id")
    public Integer getOatSiId() {
        return oatSiId;
    }

    @JsonProperty("oat_si_id")
    public void setOatSiId(Integer oatSiId) {
        this.oatSiId = oatSiId;
    }

    @JsonProperty("oat_si_name")
    public String getOatSiName() {
        return oatSiName;
    }

    @JsonProperty("oat_si_name")
    public void setOatSiName(String oatSiName) {
        this.oatSiName = oatSiName;
    }

    @JsonProperty("oat_si_display_name")
    public String getOatSiDisplayName() {
        return oatSiDisplayName;
    }

    @JsonProperty("oat_si_display_name")
    public void setOatSiDisplayName(String oatSiDisplayName) {
        this.oatSiDisplayName = oatSiDisplayName;
    }

    @JsonProperty("oat_si_description")
    public String getOatSiDescription() {
        return oatSiDescription;
    }

    @JsonProperty("oat_si_description")
    public void setOatSiDescription(String oatSiDescription) {
        this.oatSiDescription = oatSiDescription;
    }

    @JsonProperty("oat_si_unit_id")
    public Integer getOatSiUnitId() {
        return oatSiUnitId;
    }

    @JsonProperty("oat_si_unit_id")
    public void setOatSiUnitId(Integer oatSiUnitId) {
        this.oatSiUnitId = oatSiUnitId;
    }

    @JsonProperty("oat_si_metric_id")
    public String getOatSiMetricId() {
        return oatSiMetricId;
    }

    @JsonProperty("oat_si_metric_id")
    public void setOatSiMetricId(String oatSiMetricId) {
        this.oatSiMetricId = oatSiMetricId;
    }

    @JsonProperty("oat_si_earliest_processed_at")
    public String getOatSiEarliestProcessedAt() {
        return oatSiEarliestProcessedAt;
    }

    @JsonProperty("oat_si_earliest_processed_at")
    public void setOatSiEarliestProcessedAt(String oatSiEarliestProcessedAt) {
        this.oatSiEarliestProcessedAt = oatSiEarliestProcessedAt;
    }

    @JsonProperty("oat_si_last_processed_at")
    public String getOatSiLastProcessedAt() {
        return oatSiLastProcessedAt;
    }

    @JsonProperty("oat_si_last_processed_at")
    public void setOatSiLastProcessedAt(String oatSiLastProcessedAt) {
        this.oatSiLastProcessedAt = oatSiLastProcessedAt;
    }
}
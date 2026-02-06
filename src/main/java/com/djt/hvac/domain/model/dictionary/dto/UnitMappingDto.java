package com.djt.hvac.domain.model.dictionary.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class UnitMappingDto {
  
  @JsonProperty("id")
  private Integer id;
  
  @JsonProperty("ip_unit_id")
  private Integer ipUnitId;

  @JsonProperty("ip_unit_name")
  private String ipUnitName;
  
  @JsonProperty("si_unit_id")
  private Integer siUnitId;

  @JsonProperty("si_unit_name")
  private String siUnitName;
  
  @JsonProperty("ip_to_si_conversion_factor")
  private String ipToSiConversionFactor;
  
  @JsonProperty("si_to_ip_conversion_factor")
  private String siToIpConversionFactor;
  
  public UnitMappingDto() {
  }

  public UnitMappingDto(
      Integer id,
      Integer ipUnitId,
      Integer siUnitId,
      String ipToSiConversionFactor,
      String siToIpConversionFactor) {
    this.id = id;
    this.ipUnitId = ipUnitId;
    this.siUnitId = siUnitId;
    this.ipToSiConversionFactor = ipToSiConversionFactor;
    this.siToIpConversionFactor = siToIpConversionFactor; 
  }
  
  public UnitMappingDto(
      Integer id,
      String ipUnitName,
      String siUnitName,
      String ipToSiConversionFactor,
      String siToIpConversionFactor) {
    this.id = id;
    this.ipUnitName = ipUnitName;
    this.siUnitName = siUnitName;
    this.ipToSiConversionFactor = ipToSiConversionFactor;
    this.siToIpConversionFactor = siToIpConversionFactor; 
  }  
  @JsonProperty("id")
  public Integer getId() {
    return id;
  }
  
  @JsonProperty("id")
  public void setId(Integer id) {
    this.id = id;
  }

  @JsonProperty("ip_unit_id")
  public Integer getIpUnitId() {
    return ipUnitId;
  }

  @JsonProperty("ip_unit_id")
  public void setIpUnitId(Integer ipUnitId) {
    this.ipUnitId = ipUnitId;
  }
  
  @JsonProperty("ip_unit_name")
  public String getIpUnitName() {
    return ipUnitName;
  }

  @JsonProperty("ip_unit_name")
  public void setIpUnitName(String ipUnitName) {
    this.ipUnitName = ipUnitName;
  }  

  @JsonProperty("si_unit_id")
  public Integer getSiUnitId() {
    return siUnitId;
  }

  @JsonProperty("si_unit_id")
  public void setSiUnitId(Integer siUnitId) {
    this.siUnitId = siUnitId;
  }

  @JsonProperty("si_unit_name")
  public String getSiUnitName() {
    return siUnitName;
  }

  @JsonProperty("si_unit_name")
  public void setSiUnitName(String siUnitName) {
    this.siUnitName = siUnitName;
  }   
  @JsonProperty("ip_to_si_conversion_factor")
  public String getIpToSiConversionFactor() {
    return ipToSiConversionFactor;
  }

  @JsonProperty("ip_to_si_conversion_factor")
  public void setIpToSiConversionFactor(String ipToSiConversionFactor) {
    this.ipToSiConversionFactor = ipToSiConversionFactor;
  }

  @JsonProperty("si_to_ip_conversion_factor")
  public String getSiToIpConversionFactor() {
    return siToIpConversionFactor;
  }

  @JsonProperty("si_to_ip_conversion_factor")
  public void setSiToIpConversionFactor(String siToIpConversionFactor) {
    this.siToIpConversionFactor = siToIpConversionFactor;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result =
        prime * result + ((ipToSiConversionFactor == null) ? 0 : ipToSiConversionFactor.hashCode());
    result = prime * result + ((ipUnitId == null) ? 0 : ipUnitId.hashCode());
    result =
        prime * result + ((siToIpConversionFactor == null) ? 0 : siToIpConversionFactor.hashCode());
    result = prime * result + ((siUnitId == null) ? 0 : siUnitId.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
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
    UnitMappingDto other = (UnitMappingDto) obj;
    if (ipToSiConversionFactor == null) {
      if (other.ipToSiConversionFactor != null)
        return false;
    } else if (!ipToSiConversionFactor.equals(other.ipToSiConversionFactor))
      return false;
    if (ipUnitId == null) {
      if (other.ipUnitId != null)
        return false;
    } else if (!ipUnitId.equals(other.ipUnitId))
      return false;
    if (siToIpConversionFactor == null) {
      if (other.siToIpConversionFactor != null)
        return false;
    } else if (!siToIpConversionFactor.equals(other.siToIpConversionFactor))
      return false;
    if (siUnitId == null) {
      if (other.siUnitId != null)
        return false;
    } else if (!siUnitId.equals(other.siUnitId))
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("UnitMappingDto [id=").append(id).append(", ipUnitId=")
        .append(ipUnitId).append(", siUnitId=").append(siUnitId).append(", ipToSiConversionFactor=")
        .append(ipToSiConversionFactor).append(", siToIpConversionFactor=")
        .append(siToIpConversionFactor).append("]");
    return builder.toString();
  }
}
//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.dto.custompoint;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class BuildingCustomAsyncComputedPointState implements Serializable {

  private static final long serialVersionUID = 1L;
  
  private Integer buildingId;
  private Map<Integer, CustomAsyncComputedPointState> pointStates; 
  
  public BuildingCustomAsyncComputedPointState() {
    super();
  }

  public BuildingCustomAsyncComputedPointState(Integer buildingId) {
    super();
    
    this.buildingId = buildingId;
    this.pointStates = new TreeMap<>();
  }

  public Integer getBuildingId() {
    return buildingId;
  }

  public void setBuildingId(Integer buildingId) {
    this.buildingId = buildingId;
  }

  public Map<Integer, CustomAsyncComputedPointState> getPointStates() {
    return pointStates;
  }

  public CustomAsyncComputedPointState getPointState(Integer pointId) {
    return pointStates.get(pointId);
  }
  
  public void setPointStates(Map<Integer, CustomAsyncComputedPointState> pointStates) {
    this.pointStates = pointStates;
  }

  public void addPointState(Integer pointId, CustomAsyncComputedPointState pointState) {
    this.pointStates.put(pointId, pointState);
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(buildingId, pointStates);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    BuildingCustomAsyncComputedPointState other = (BuildingCustomAsyncComputedPointState) obj;
    return Objects.equals(buildingId, other.buildingId)
        && Objects.equals(pointStates, other.pointStates);
  }

  @Override
  public String toString() {
    return "BuildingCustomAsyncComputedPointState [buildingId=" + buildingId + ", pointStates="
        + pointStates + "]";
  }
}
//@formatter:on
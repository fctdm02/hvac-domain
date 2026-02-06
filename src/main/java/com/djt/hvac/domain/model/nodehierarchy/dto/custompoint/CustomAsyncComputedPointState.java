//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.dto.custompoint;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class CustomAsyncComputedPointState implements Serializable {

  private static final long serialVersionUID = 1L;
  
  private Timestamp timestamp;
  private Double value;
  private Map<String, String> state;
  
  public CustomAsyncComputedPointState() {
    this(null, null);
  }

  public CustomAsyncComputedPointState(
      Timestamp timestamp,
      Double value) {
    this(timestamp, value, new TreeMap<>());
  }
  
  public CustomAsyncComputedPointState(
      Timestamp timestamp,
      Double value,
      Map<String, String> state) {
    
    super();
    this.timestamp = timestamp;
    this.value = value;
    this.state = state;
  }

  public Timestamp getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Timestamp timestamp) {
    this.timestamp = timestamp;
  }

  public Double getValue() {
    return value;
  }

  public void setValue(Double value) {
    this.value = value;
  }

  public Map<String, String> getState() {
    return state;
  }

  public void setState(Map<String, String> state) {
    this.state = state;
  }

  @Override
  public int hashCode() {
    return Objects.hash(state, timestamp, value);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    CustomAsyncComputedPointState other = (CustomAsyncComputedPointState) obj;
    return Objects.equals(state, other.state) && Objects.equals(timestamp, other.timestamp)
        && Objects.equals(value, other.value);
  }

  @Override
  public String toString() {
    return "CustomAsyncComputedPointState [timestamp=" + timestamp + ", value=" + value + ", state="
        + state + "]";
  }
}
//@formatter:on
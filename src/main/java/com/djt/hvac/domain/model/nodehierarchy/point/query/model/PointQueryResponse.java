//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.point.query.model;

import java.util.List;

import com.djt.hvac.domain.model.common.query.model.QueryResponse;
import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.EnergyExchangeSystemNodeData;

/**
 * 
 * @author tmyers
 */
public class PointQueryResponse extends QueryResponse<PointSearchCriteria, EnergyExchangeSystemNodeData> {
  
  private PointSearchCriteria query;
  private Integer totalRows;
  private List<EnergyExchangeSystemNodeData> data;
  
  public PointQueryResponse() {
  }

  public PointQueryResponse(
      PointSearchCriteria query, 
      Integer totalRows,
      List<EnergyExchangeSystemNodeData> data) {
    super();
    this.query = query;
    this.totalRows = totalRows;
    this.data = data;
  }
 
  public void setQuery(PointSearchCriteria query) {
    this.query = query;
  }

  public void setTotalRows(Integer totalRows) {
    this.totalRows = totalRows;
  }

  public void setData(List<EnergyExchangeSystemNodeData> data) {
    this.data = data;
  }

  public PointSearchCriteria getQuery() {
    return query;
  }
  
  public Integer getTotalRows() {
    return totalRows;
  }

  public List<EnergyExchangeSystemNodeData> getData() {
    return data;
  }
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("EnergyExchangeQueryResponse [query=").append(query).append(", totalRows=")
        .append(totalRows).append(", data=").append(data).append(", getClass()=").append(getClass())
        .append(", hashCode()=").append(hashCode()).append(", toString()=").append(super.toString())
        .append("]");
    return builder.toString();
  }
  
}
//@formatter:on
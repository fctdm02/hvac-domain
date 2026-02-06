//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.energyexchange.query.model;

import java.util.List;

import com.djt.hvac.domain.model.common.query.model.QueryResponse;
import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.EnergyExchangeSystemNodeData;

/**
 * 
 * @author tmyers
 */
public class EnergyExchangeQueryResponse extends QueryResponse<EnergyExchangeSearchCriteria, EnergyExchangeSystemNodeData> {
  
  private EnergyExchangeSearchCriteria query;
  private Integer totalRows;
  private List<EnergyExchangeSystemNodeData> data;
  
  public EnergyExchangeQueryResponse() {
  }

  public EnergyExchangeQueryResponse(
      EnergyExchangeSearchCriteria query, 
      Integer totalRows,
      List<EnergyExchangeSystemNodeData> data) {
    super();
    this.query = query;
    this.totalRows = totalRows;
    this.data = data;
  }
 
  public void setQuery(EnergyExchangeSearchCriteria query) {
    this.query = query;
  }

  public void setTotalRows(Integer totalRows) {
    this.totalRows = totalRows;
  }

  public void setData(List<EnergyExchangeSystemNodeData> data) {
    this.data = data;
  }

  public EnergyExchangeSearchCriteria getQuery() {
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
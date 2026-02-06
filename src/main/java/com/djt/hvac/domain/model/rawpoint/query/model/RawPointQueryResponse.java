//@formatter:off
package com.djt.hvac.domain.model.rawpoint.query.model;

import java.util.List;

import com.djt.hvac.domain.model.common.query.model.QueryResponse;

/**
 * 
 * @author tmyers
 */
public class RawPointQueryResponse extends QueryResponse<RawPointSearchCriteria, RawPointData> {
  
  private RawPointSearchCriteria query;
  private Integer totalRows;
  private List<RawPointData> data;
  
  public RawPointQueryResponse() {
  }

  public RawPointQueryResponse(
      RawPointSearchCriteria query, 
      Integer totalRows,
      List<RawPointData> data) {
    super();
    this.query = query;
    this.totalRows = totalRows;
    this.data = data;
  }
 
  public void setQuery(RawPointSearchCriteria query) {
    this.query = query;
  }

  public void setTotalRows(Integer totalRows) {
    this.totalRows = totalRows;
  }

  public void setData(List<RawPointData> data) {
    this.data = data;
  }

  public RawPointSearchCriteria getQuery() {
    return query;
  }
  
  public Integer getTotalRows() {
    return totalRows;
  }

  public List<RawPointData> getData() {
    return data;
  }
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("RawPointQueryResponse [query=").append(query).append(", totalRows=")
        .append(totalRows).append(", data=").append(data).append(", getClass()=").append(getClass())
        .append(", hashCode()=").append(hashCode()).append(", toString()=").append(super.toString())
        .append("]");
    return builder.toString();
  }
  
}
//@formatter:on
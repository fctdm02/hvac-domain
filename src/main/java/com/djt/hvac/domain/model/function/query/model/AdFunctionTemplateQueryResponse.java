//@formatter:off
package com.djt.hvac.domain.model.function.query.model;

import java.util.List;

import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.AdFunctionInstanceData;

/**
 * 
 * @author tmyers
 */
public class AdFunctionTemplateQueryResponse {
  
  private AdFunctionInstanceSearchCriteria query;
  
  private Integer totalRows;
  
  private List<AdFunctionInstanceData> data;

  public AdFunctionTemplateQueryResponse(
      AdFunctionInstanceSearchCriteria query, 
      Integer totalRows,
      List<AdFunctionInstanceData> data) {
    super();
    this.query = query;
    this.totalRows = totalRows;
    this.data = data;
  }
  
  public AdFunctionTemplateQueryResponse() {
    super();
  }
  
  public AdFunctionInstanceSearchCriteria getQuery() {
    return query;
  }
  
  public Integer getTotalRows() {
    return totalRows;
  }

  public List<AdFunctionInstanceData> getData() {
    return data;
  }

  public void setQuery(AdFunctionInstanceSearchCriteria query) {
    this.query = query;
  }

  public void setTotalRows(Integer totalRows) {
    this.totalRows = totalRows;
  }

  public void setData(List<AdFunctionInstanceData> data) {
    this.data = data;
  }

  @Override
  public String toString() {
    
    return new StringBuilder()
        .append("AdFunctionInstanceQueryResponse [query=")
        .append(query)
        .append(", totalRows=")
        .append(totalRows)
        .append(", data=")
        .append(data)
        .append("]").toString();
  }
}
//@formatter:on
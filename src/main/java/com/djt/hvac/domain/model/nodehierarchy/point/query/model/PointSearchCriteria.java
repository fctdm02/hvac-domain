//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.point.query.model;

import com.djt.hvac.domain.model.common.query.model.SearchCriteria;

/**
 * 
 * @author tmyers
 *
 */
public class PointSearchCriteria extends SearchCriteria {
  
  public static final String ALL = "ALL"; 

  public static final String SORT_KEY_NODE_PATH = "nodePath";
  
  public static final String DEFAULT_SORT = SORT_KEY_NODE_PATH;
  
  private Integer customerId;
  private Integer buildingId;
  private String nodePath;
  
  public PointSearchCriteria() {
  }
  
  public PointSearchCriteria(
      Integer customerId, 
      Integer buildingId, 
      String nodePath,
      String sort, 
      String sortDirection, 
      Integer offset,
      Integer limit) {
    super(
        sort, 
        sortDirection, 
        offset, 
        limit);
    this.customerId = customerId;
    this.buildingId = buildingId;
    this.nodePath = nodePath;
  }

  public Integer getCustomerId() {
    return customerId;
  }

  public Integer getBuildingId() {
    return buildingId;
  }

  public String getNodePath() {
    return nodePath;
  }
  
  public void setCustomerId(Integer customerId) {
    this.customerId = customerId;
  }

  public void setBuildingId(Integer buildingId) {
    this.buildingId = buildingId;
  }

  public void setNodePath(String nodePath) {
    this.nodePath = nodePath;
  }
}
//@formatter:on
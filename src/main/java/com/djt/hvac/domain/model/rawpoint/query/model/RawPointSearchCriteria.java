//@formatter:off
package com.djt.hvac.domain.model.rawpoint.query.model;

import com.djt.hvac.domain.model.common.query.model.SearchCriteria;

/**
 * 
 * @author tmyers
 *
 */
public class RawPointSearchCriteria extends SearchCriteria {
  
  public static final String ALL = "ALL"; 

  public static final String SORT_KEY_CUSTOMER_ID = "customerId";
  
  public static final String DEFAULT_SORT = SORT_KEY_CUSTOMER_ID;
  
  private Integer customerId;
  
  public RawPointSearchCriteria() {
  }

  public RawPointSearchCriteria(Integer customerId) {
    this(
        customerId,
        DEFAULT_SORT,
        SearchCriteria.SORT_DIRECTION_ASC,
        SearchCriteria.DEFAULT_OFFSET,
        SearchCriteria.DEFAULT_LIMIT);
  }
  
  public RawPointSearchCriteria(
      Integer customerId,
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
  }

  public Integer getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Integer customerId) {
    this.customerId = customerId;
  }
}
//@formatter:on
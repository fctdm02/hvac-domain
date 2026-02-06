
package com.djt.hvac.domain.model.user.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "distributor_id",
    "customer_id",
    "email",
    "first_name",
    "last_name",
    "role_id",
    "accepted_terms",
    "enable_report_notifications",
    "account_manager",
    "disabled_email_notifications"
})
public class UserDto implements Serializable {

  @JsonProperty("id")
  private Integer id;
  @JsonProperty("distributor_id")
  private Integer distributorId;
  @JsonProperty("customer_id")
  private Integer customerId;
  @JsonProperty("email")
  private String email;
  @JsonProperty("first_name")
  private String firstName;
  @JsonProperty("last_name")
  private String lastName;
  @JsonProperty("role_id")
  private Integer roleId;
  @JsonProperty("accepted_terms")
  private Boolean acceptedTerms;
  @JsonProperty("enable_report_notifications")
  private Boolean enableReportNotifications;
  @JsonProperty("account_manager")
  private Boolean accountManager;
  @JsonProperty("disabled_email_notifications")
  private List<String> disabledEmailNotifications = new ArrayList<>();
  
  private final static long serialVersionUID = -6400520647834769765L;

  @JsonProperty("id")
  public Integer getId() {
    return id;
  }

  @JsonProperty("id")
  public void setId(Integer id) {
    this.id = id;
  }

  @JsonProperty("distributor_id")
  public Integer getDistributorId() {
    return distributorId;
  }

  @JsonProperty("distributor_id")
  public void setDistributorId(Integer distributorId) {
    this.distributorId = distributorId;
  }
  
  @JsonProperty("customer_id")
  public Integer getCustomerId() {
    return customerId;
  }

  @JsonProperty("customer_id")
  public void setCustomerId(Integer customerId) {
    this.customerId = customerId;
  }

  @JsonProperty("email")
  public String getEmail() {
    return email;
  }

  @JsonProperty("email")
  public void setEmail(String email) {
    this.email = email;
  }

  @JsonProperty("first_name")
  public String getFirstName() {
    return firstName;
  }

  @JsonProperty("first_name")
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  @JsonProperty("last_name")
  public String getLastName() {
    return lastName;
  }

  @JsonProperty("last_name")
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  @JsonProperty("role_id")
  public Integer getRoleId() {
    return roleId;
  }

  @JsonProperty("role_id")
  public void setRoleId(Integer roleId) {
    this.roleId = roleId;
  }

  @JsonProperty("accepted_terms")
  public Boolean getAcceptedTerms() {
    return acceptedTerms;
  }

  @JsonProperty("accepted_terms")
  public void setAcceptedTerms(Boolean acceptedTerms) {
    this.acceptedTerms = acceptedTerms;
  }

  @JsonProperty("enable_report_notifications")
  public Boolean getEnableReportNotifications() {
    return enableReportNotifications;
  }

  @JsonProperty("enable_report_notifications")
  public void setEnableReportNotifications(Boolean enableReportNotifications) {
    this.enableReportNotifications = enableReportNotifications;
  }

  @JsonProperty("account_manager")
  public Boolean getAccountManager() {
    return accountManager;
  }

  @JsonProperty("account_manager")
  public void setAccountManager(Boolean accountManager) {
    this.accountManager = accountManager;
  }
  
  @JsonProperty("disabled_email_notifications")
  public List<String> getDisabledEmailNotifications() {
    return disabledEmailNotifications;
  }

  @JsonProperty("disabled_email_notifications")
  public void setDisabledEmailNotifications(List<String> disabledEmailNotifications) {
    if (disabledEmailNotifications != null && !disabledEmailNotifications.isEmpty()) {
      this.disabledEmailNotifications.addAll(disabledEmailNotifications);
    }
  }
}
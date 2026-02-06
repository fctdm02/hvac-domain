
package com.djt.hvac.domain.model.customer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "distributor_id",
    "name",
    "uuid",
    "unit_system",
    "created_at",
    "updated_at",
    "start_date",
    "status",
    "status_updated_at",
    "internal",
    "demo",
    "demo_expires",
    "payment_status",
    "payment_status_updated_at"
})
public class CustomerDto {

  @JsonProperty("id")
  private Integer id;
  @JsonProperty("distributor_id")
  private Integer distributorId;
  @JsonProperty("name")
  private String name;
  @JsonProperty("uuid")
  private String uuid;
  @JsonProperty("unit_system")
  private String unitSystem;
  @JsonProperty("created_at")
  private String createdAt;
  @JsonProperty("updated_at")
  private String updatedAt;
  @JsonProperty("start_date")
  private String startDate;
  @JsonProperty("status")
  private String status;
  @JsonProperty("status_updated_at")
  private String statusUpdatedAt;
  @JsonProperty("internal")
  private Boolean internal = Boolean.FALSE;
  @JsonProperty("demo")
  private Boolean demo;
  @JsonProperty("demo_expires")
  private Boolean demoExpires;
  @JsonProperty("payment_status")
  private String paymentStatus;
  @JsonProperty("payment_status_updated_at")
  private String paymentStatusUpdatedAt;

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

  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @JsonProperty("name")
  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty("uuid")
  public String getUuid() {
    return uuid;
  }

  @JsonProperty("uuid")
  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  @JsonProperty("unit_system")
  public String getUnitSystem() {
    return unitSystem;
  }

  @JsonProperty("unit_system")
  public void setUnitSystem(String unitSystem) {
    this.unitSystem = unitSystem;
  }
  
  @JsonProperty("created_at")
  public String getCreatedAt() {
    return createdAt;
  }

  @JsonProperty("created_at")
  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  @JsonProperty("updated_at")
  public String getUpdatedAt() {
    return updatedAt;
  }

  @JsonProperty("updated_at")
  public void setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
  }

  @JsonProperty("start_date")
  public String getStartDate() {
    return startDate;
  }

  @JsonProperty("start_date")
  public void setStartDate(String resoluteStartDate) {
    this.startDate = resoluteStartDate;
  }

  @JsonProperty("status")
  public String getStatus() {
    return status;
  }

  @JsonProperty("status")
  public void setStatus(String status) {
    this.status = status;
  }

  @JsonProperty("status_updated_at")
  public String getStatusUpdatedAt() {
    return statusUpdatedAt;
  }

  @JsonProperty("status_updated_at")
  public void setStatusUpdatedAt(String statusUpdatedAt) {
    this.statusUpdatedAt = statusUpdatedAt;
  }

  @JsonProperty("internal")
  public Boolean getInternal() {
    return internal;
  }

  @JsonProperty("internal")
  public void setInternal(Boolean internal) {
    if (internal != null) {
      this.internal = internal;  
    }
  }
  
  @JsonProperty("demo")
  public Boolean getDemo() {
    return demo;
  }

  @JsonProperty("demo")
  public void setDemo(Boolean demo) {
    this.demo = demo;
  }

  @JsonProperty("demo_expires")
  public Boolean getDemoExpires() {
    return demoExpires;
  }

  @JsonProperty("demo_expires")
  public void setDemoExpires(Boolean demoExpires) {
    this.demoExpires = demoExpires;
  }

  @JsonProperty("payment_status")
  public String getPaymentStatus() {
    return paymentStatus;
  }

  @JsonProperty("payment_status")
  public void setPaymentStatus(String paymentStatus) {
    this.paymentStatus = paymentStatus;
  }

  @JsonProperty("payment_status_updated_at")
  public String getPaymentStatusUpdatedAt() {
    return paymentStatusUpdatedAt;
  }

  @JsonProperty("payment_status_updated_at")
  public void setPaymentStatusUpdatedAt(String paymentStatusUpdatedAt) {
    this.paymentStatusUpdatedAt = paymentStatusUpdatedAt;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
    result = prime * result + ((demo == null) ? 0 : demo.hashCode());
    result = prime * result + ((demoExpires == null) ? 0 : demoExpires.hashCode());
    result = prime * result + ((distributorId == null) ? 0 : distributorId.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((internal == null) ? 0 : internal.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((paymentStatus == null) ? 0 : paymentStatus.hashCode());
    result =
        prime * result + ((paymentStatusUpdatedAt == null) ? 0 : paymentStatusUpdatedAt.hashCode());
    result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
    result = prime * result + ((status == null) ? 0 : status.hashCode());
    result = prime * result + ((statusUpdatedAt == null) ? 0 : statusUpdatedAt.hashCode());
    result = prime * result + ((unitSystem == null) ? 0 : unitSystem.hashCode());
    result = prime * result + ((updatedAt == null) ? 0 : updatedAt.hashCode());
    result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
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
    CustomerDto other = (CustomerDto) obj;
    if (createdAt == null) {
      if (other.createdAt != null)
        return false;
    } else if (!createdAt.equals(other.createdAt))
      return false;
    if (demo == null) {
      if (other.demo != null)
        return false;
    } else if (!demo.equals(other.demo))
      return false;
    if (demoExpires == null) {
      if (other.demoExpires != null)
        return false;
    } else if (!demoExpires.equals(other.demoExpires))
      return false;
    if (distributorId == null) {
      if (other.distributorId != null)
        return false;
    } else if (!distributorId.equals(other.distributorId))
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (internal == null) {
      if (other.internal != null)
        return false;
    } else if (!internal.equals(other.internal))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (paymentStatus == null) {
      if (other.paymentStatus != null)
        return false;
    } else if (!paymentStatus.equals(other.paymentStatus))
      return false;
    if (paymentStatusUpdatedAt == null) {
      if (other.paymentStatusUpdatedAt != null)
        return false;
    } else if (!paymentStatusUpdatedAt.equals(other.paymentStatusUpdatedAt))
      return false;
    if (startDate == null) {
      if (other.startDate != null)
        return false;
    } else if (!startDate.equals(other.startDate))
      return false;
    if (status == null) {
      if (other.status != null)
        return false;
    } else if (!status.equals(other.status))
      return false;
    if (statusUpdatedAt == null) {
      if (other.statusUpdatedAt != null)
        return false;
    } else if (!statusUpdatedAt.equals(other.statusUpdatedAt))
      return false;
    if (unitSystem == null) {
      if (other.unitSystem != null)
        return false;
    } else if (!unitSystem.equals(other.unitSystem))
      return false;
    if (updatedAt == null) {
      if (other.updatedAt != null)
        return false;
    } else if (!updatedAt.equals(other.updatedAt))
      return false;
    if (uuid == null) {
      if (other.uuid != null)
        return false;
    } else if (!uuid.equals(other.uuid))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("CustomerDto [id=").append(id).append(", distributorId=").append(distributorId)
        .append(", name=").append(name).append(", uuid=").append(uuid).append(", unitSystem=")
        .append(unitSystem).append(", createdAt=").append(createdAt).append(", updatedAt=")
        .append(updatedAt).append(", resoluteStartDate=").append(startDate)
        .append(", status=").append(status).append(", statusUpdatedAt=").append(statusUpdatedAt)
        .append(", internal=").append(internal).append(", demo=").append(demo)
        .append(", demoExpires=").append(demoExpires).append(", paymentStatus=")
        .append(paymentStatus).append(", paymentStatusUpdatedAt=").append(paymentStatusUpdatedAt)
        .append("]");
    return builder.toString();
  }
}
package com.djt.hvac.domain.model.nodehierarchy.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
    "node_id",
    "node_type_id",
    "node_name",
    "node_display_name",
    "uuid",
    "node_parent_id",
    "node_parent_node_type_id",
    "node_created_at",
    "node_updated_at",
    "equipment_type_id",
    "floor_ordinal",
    "building_timezone",
    "building_address",
    "building_city",
    "building_state_or_province",
    "building_postal_code",
    "building_country_code",
    "building_unit_system",
    "building_latitude",
    "building_longitude",
    "building_weather_station_id",
    "building_temporal_id",
    "building_temporal_effective_date",
    "building_temporal_sqft",
    "building_utility_id",
    "building_utility_computation_interval_id",
    "building_utility_formula",
    "building_utility_rate",
    "building_utility_baseline_description",
    "building_utility_user_notes",
    "building_billing_grace_period",
    "building_status",
    "building_status_updated_at",
    "building_payment_status",
    "building_payment_status_updated_at",
    "building_pending_deletion",
    "building_pending_deletion_updated_at",
    "building_payment_plan_id",
    "building_payment_method_id",
    "building_stripe_subscription_id",
    "building_subscription_started_at",
    "building_subscription_current_interval_started_at",
    "building_pending_payment_plan_id",
    "building_pending_payment_plan_updated_at",
    "building_grace_period_warning_notification_id",
    "building_payment_type"
})
public class NonPointNodeDto implements Comparable<NonPointNodeDto> {

    @JsonProperty("node_id")
    private Integer nodeId;
    
    @JsonProperty("node_type_id")
    private Integer nodeTypeId;
    
    @JsonProperty("node_name")
    private String nodeName;
    
    @JsonProperty("node_display_name")
    private String nodeDisplayName;

    @JsonProperty("uuid")
    private String uuid;
    
    @JsonProperty("node_parent_id")
    private Integer nodeParentId;
    
    @JsonProperty("node_parent_node_type_id")
    private Integer nodeParentNodeTypeId;
    
    @JsonProperty("node_created_at")
    private String nodeCreatedAt;

    @JsonProperty("node_updated_at")
    private String nodeUpdatedAt;
    
    @JsonProperty("equipment_type_id")
    private Integer equipmentTypeId;

    @JsonProperty("floor_ordinal")
    private Integer floorOrdinal;
    
    @JsonProperty("building_timezone")
    private String buildingTimezone;

    @JsonProperty("building_address")
    private String buildingAddress;
    
    @JsonProperty("building_city")
    private String buildingCity;
    
    @JsonProperty("building_state_or_province")
    private String buildingStateOrProvince;
    
    @JsonProperty("building_postal_code")
    private String buildingPostalCode;

    @JsonProperty("building_country_code")
    private String buildingCountryCode;
    
    @JsonProperty("building_unit_system")
    private String buildingUnitSystem;
    
    @JsonProperty("building_latitude")
    private String buildingLatitude;
    
    @JsonProperty("building_longitude")
    private String buildingLongitude;
    
    @JsonProperty("building_weather_station_id")
    private Integer buildingWeatherStationId;

    @JsonProperty("building_temporal_id")
    private String buildingTemporalId;
    
    @JsonProperty("building_temporal_effective_date")
    private String buildingTemporalEffectiveDate;

    @JsonProperty("building_temporal_sqft")
    private String buildingTemporalSqft;
    
    @JsonProperty("building_utility_id")
    private String buildingUtilityId;
    
    @JsonProperty("building_utility_computation_interval_id")
    private String buildingUtilityComputationIntervalId;
    
    @JsonProperty("building_utility_formula")
    private String buildingUtilityFormula;
    
    @JsonProperty("building_utility_rate")
    private String buildingUtilityRate;
    
    @JsonProperty("building_utility_baseline_description")
    private String buildingUtilityBaselineDescription;
    
    @JsonProperty("building_utility_user_notes")
    private String buildingUtilityUserNotes;
    
    @JsonProperty("building_billing_grace_period")
    private String buildingBillingGracePeriod;

    @JsonProperty("building_status")
    private String buildingStatus;

    @JsonProperty("building_status_updated_at")
    private String buildingStatusUpdatedAt;

    @JsonProperty("building_payment_status")
    private String buildingPaymentStatus;

    @JsonProperty("building_payment_status_updated_at")
    private String buildingPaymentStatusUpdatedAt;

    @JsonProperty("building_pending_deletion")
    private Boolean buildingPendingDeletion;

    @JsonProperty("building_pending_deletion_updated_at")
    private String buildingPendingDeletionUpdatedAt;
    
    @JsonProperty("building_payment_plan_id")
    private Integer buildingPaymentPlanId;
    
    @JsonProperty("building_payment_method_id")
    private Integer buildingPaymentMethodId;
    
    @JsonProperty("building_stripe_subscription_id")
    private String buildingStripeSubscriptionId;
    
    @JsonProperty("building_subscription_started_at")
    private String buildingSubscriptionStartedAt;

    @JsonProperty("building_subscription_current_interval_started_at")
    private String buildingSubscriptionCurrentIntervalStartedAt;
    
    @JsonProperty("building_pending_payment_plan_id")
    private Integer buildingPendingPaymentPlanId;
    
    @JsonProperty("building_pending_payment_plan_updated_at")
    private String buildingPendingPaymentPlanUpdatedAt;

    @JsonProperty("building_grace_period_warning_notification_id")
    private Integer buildingGracePeriodWarningNotificationId;

    @JsonProperty("building_payment_type")
    private String buildingPaymentType;
    
    @JsonProperty("node_id")
    public Integer getNodeId() {
        return nodeId;
    }

    @JsonProperty("node_id")
    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }
    

    @JsonProperty("node_type_id")
    public Integer getNodeTypeId() {
        return nodeTypeId;
    }

    @JsonProperty("node_type_id")
    public void setNodeTypeId(Integer nodeTypeId) {
        this.nodeTypeId = nodeTypeId;
    }

    
    @JsonProperty("node_name")
    public String getNodeName() {
        return nodeName;
    }

    @JsonProperty("node_name")
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    
    @JsonProperty("node_display_name")
    public String getNodeDisplayName() {
        return nodeDisplayName;
    }

    @JsonProperty("node_display_name")
    public void setNodeDisplayName(String nodeDisplayName) {
        this.nodeDisplayName = nodeDisplayName;
    }

    
    @JsonProperty("uuid")
    public String getUuid() {
        return uuid;
    }

    @JsonProperty("uuid")
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    
    
    @JsonProperty("node_parent_id")
    public Integer getNodeParentId() {
        return nodeParentId;
    }

    @JsonProperty("node_parent_id")
    public void setNodeParentId(Integer nodeParentId) {
        this.nodeParentId = nodeParentId;
    }

    @JsonProperty("node_parent_node_type_id")
    public Integer getNodeParentNodeTypeId() {
        return nodeParentNodeTypeId;
    }

    @JsonProperty("node_parent_node_type_id")
    public void setNodeParentNodeTypeId(Integer nodeParentNodeTypeId) {
        this.nodeParentNodeTypeId = nodeParentNodeTypeId;
    }

    
    @JsonProperty("node_created_at")
    public String getNodeCreatedAt() {
        return nodeCreatedAt;
    }

    @JsonProperty("node_created_at")
    public void setNodeCreatedAt(String nodeCreatedAt) {
        this.nodeCreatedAt = nodeCreatedAt;
    }

    
    @JsonProperty("node_updated_at")
    public String getNodeUpdatedAt() {
        return nodeUpdatedAt;
    }

    @JsonProperty("node_updated_at")
    public void setNodeUpdatedAt(String nodeUpdatedAt) {
        this.nodeUpdatedAt = nodeUpdatedAt;
    }
    
    
    @JsonProperty("equipment_type_id")
    public Integer getEquipmentTypeId() {
        return equipmentTypeId;
    }

    @JsonProperty("equipment_type_id")
    public void setEquipmentTypeId(Integer equipmentTypeId) {
        this.equipmentTypeId = equipmentTypeId;
    }
    

    @JsonProperty("floor_ordinal")
    public Integer getFloorOrdinal() {
        return floorOrdinal;
    }

    @JsonProperty("floor_ordinal")
    public void setFloorOrdinal(Integer floorOrdinal) {
        this.floorOrdinal = floorOrdinal;
    }

    
    @JsonProperty("building_timezone")
    public String getBuildingTimezone() {
        return buildingTimezone;
    }

    @JsonProperty("building_timezone")
    public void setBuildingTimezone(String buildingTimezone) {
        this.buildingTimezone = buildingTimezone;
    }

    
    @JsonProperty("building_address")
    public String getBuildingAddress() {
        return buildingAddress;
    }

    @JsonProperty("building_address")
    public void setBuildingAddress(String buildingAddress) {
        this.buildingAddress = buildingAddress;
    }

    
    @JsonProperty("building_city")
    public String getBuildingCity() {
        return buildingCity;
    }

    @JsonProperty("building_city")
    public void setBuildingCity(String buildingCity) {
        this.buildingCity = buildingCity;
    }

    
    @JsonProperty("building_state_or_province")
    public String getBuildingStateOrProvince() {
        return buildingStateOrProvince;
    }

    @JsonProperty("building_state_or_province")
    public void setBuildingStateOrProvince(String buildingStateOrProvince) {
        this.buildingStateOrProvince = buildingStateOrProvince;
    }

    
    @JsonProperty("building_postal_code")
    public String getBuildingPostalCode() {
        return buildingPostalCode;
    }

    @JsonProperty("building_postal_code")
    public void setBuildingPostalCode(String buildingPostalCode) {
        this.buildingPostalCode = buildingPostalCode;
    }

    
    @JsonProperty("building_country_code")
    public String getBuildingCountryCode() {
        return buildingCountryCode;
    }

    @JsonProperty("building_country_code")
    public void setBuildingCountryCode(String buildingCountryCode) {
        this.buildingCountryCode = buildingCountryCode;
    }

    
    @JsonProperty("building_unit_system")
    public String getBuildingUnitSystem() {
        return buildingUnitSystem;
    }

    @JsonProperty("building_unit_system")
    public void setBuildingUnitSystem(String buildingUnitSystem) {
        this.buildingUnitSystem = buildingUnitSystem;
    }
    
    
    @JsonProperty("building_latitude")
    public String getBuildingLatitude() {
        return buildingLatitude;
    }

    @JsonProperty("building_latitude")
    public void setBuildingLatitude(String buildingLatitude) {
        this.buildingLatitude = buildingLatitude;
    }

    
    @JsonProperty("building_longitude")
    public String getBuildingLongitude() {
        return buildingLongitude;
    }

    @JsonProperty("building_longitude")
    public void setBuildingLongitude(String buildingLongitude) {
        this.buildingLongitude = buildingLongitude;
    }

    
    @JsonProperty("building_weather_station_id")
    public Integer getBuildingWeatherStationId() {
        return buildingWeatherStationId;
    }

    @JsonProperty("building_weather_station_id")
    public void setBuildingWeatherStationId(Integer buildingWeatherStationId) {
        this.buildingWeatherStationId = buildingWeatherStationId;
    }
    
    
    @JsonProperty("building_temporal_id")
    public String getBuildingTemporalId() {
      return buildingTemporalId;
    }

    @JsonProperty("building_temporal_id")
    public void setBuildingTemporalId(String buildingTemporalId) {
      this.buildingTemporalId = buildingTemporalId;
    }

    
    @JsonProperty("building_temporal_effective_date")
    public String getBuildingTemporalEffectiveDate() {
      return buildingTemporalEffectiveDate;
    }

    @JsonProperty("building_temporal_effective_date")
    public void setBuildingTemporalEffectiveDate(String buildingTemporalEffectiveDate) {
      this.buildingTemporalEffectiveDate = buildingTemporalEffectiveDate;
    }

    
    @JsonProperty("building_temporal_sqft")
    public String getBuildingTemporalSqft() {
      return buildingTemporalSqft;
    }

    @JsonProperty("building_temporal_sqft")
    public void setBuildingTemporalSqft(String buildingTemporalSqft) {
      this.buildingTemporalSqft = buildingTemporalSqft;
    }
    
    
    @JsonProperty("building_utility_id")
    public String getBuildingUtilityId() {
      return buildingUtilityId;
    }

    @JsonProperty("building_utility_id")
    public void setBuildingUtilityId(String buildingUtilityId) {
      this.buildingUtilityId = buildingUtilityId;
    }
    

    @JsonProperty("building_utility_computation_interval_id")
    public String getBuildingUtilityComputationIntervalId() {
      return buildingUtilityComputationIntervalId;
    }

    @JsonProperty("building_utility_computation_interval_id")
    public void setBuildingUtilityComputationIntervalId(String buildingUtilityComputationIntervalId) {
      this.buildingUtilityComputationIntervalId = buildingUtilityComputationIntervalId;
    }

    
    @JsonProperty("building_utility_formula")
    public String getBuildingUtilityFormula() {
      return buildingUtilityFormula;
    }

    @JsonProperty("building_utility_formula")
    public void setBuildingUtilityFormula(String buildingUtilityFormula) {
      this.buildingUtilityFormula = buildingUtilityFormula;
    }
    

    @JsonProperty("building_utility_rate")
    public String getBuildingUtilityRate() {
      return buildingUtilityRate;
    }

    @JsonProperty("building_utility_rate")
    public void setBuildingUtilityRate(String buildingUtilityRate) {
      this.buildingUtilityRate = buildingUtilityRate;
    }

    
    @JsonProperty("building_utility_baseline_description")
    public String getBuildingUtilityBaselineDescription() {
      return buildingUtilityBaselineDescription;
    }

    @JsonProperty("building_utility_baseline_description")
    public void setBuildingUtilityBaselineDescription(String buildingUtilityBaselineDescription) {
      this.buildingUtilityBaselineDescription = buildingUtilityBaselineDescription;
    }

    
    @JsonProperty("building_utility_user_notes")
    public String getBuildingUtilityUserNotes() {
      return buildingUtilityUserNotes;
    }

    @JsonProperty("building_utility_user_notes")
    public void setBuildingUtilityUserNotes(String buildingUtilityUserNotes) {
      this.buildingUtilityUserNotes = buildingUtilityUserNotes;
    }

    
    @JsonProperty("building_billing_grace_period")
    public String getBuildingBillingGracePeriod() {
        return buildingBillingGracePeriod;
    }

    @JsonProperty("building_billing_grace_period")
    public void setBuildingBillingGracePeriod(String buildingBillingGracePeriod) {
        this.buildingBillingGracePeriod = buildingBillingGracePeriod;
    }

    
    @JsonProperty("building_status")
    public String getBuildingStatus() {
        return buildingStatus;
    }

    @JsonProperty("building_status")
    public void setBuildingStatus(String buildingStatus) {
        this.buildingStatus = buildingStatus;
    }

    
    @JsonProperty("building_status_updated_at")
    public String getBuildingStatusUpdatedAt() {
        return buildingStatusUpdatedAt;
    }

    @JsonProperty("building_status_updated_at")
    public void setBuildingStatusUpdatedAt(String buildingStatusUpdatedAt) {
        this.buildingStatusUpdatedAt = buildingStatusUpdatedAt;
    }

    
    @JsonProperty("building_payment_status")
    public String getBuildingPaymentStatus() {
        return buildingPaymentStatus;
    }

    @JsonProperty("building_payment_status")
    public void setBuildingPaymentStatus(String buildingPaymentStatus) {
        this.buildingPaymentStatus = buildingPaymentStatus;
    }

    
    @JsonProperty("building_payment_status_updated_at")
    public String getBuildingPaymentStatusUpdatedAt() {
        return buildingPaymentStatusUpdatedAt;
    }

    @JsonProperty("building_payment_status_updated_at")
    public void setBuildingPaymentStatusUpdatedAt(String buildingPaymentStatusUpdatedAt) {
        this.buildingPaymentStatusUpdatedAt = buildingPaymentStatusUpdatedAt;
    }


    @JsonProperty("building_pending_deletion")
    public Boolean getBuildingPendingDeletion() {
        return buildingPendingDeletion;
    }

    @JsonProperty("building_pending_deletion")
    public void setBuildingPendingDeletion(Boolean buildingPendingDeletion) {
        this.buildingPendingDeletion = buildingPendingDeletion;
    }

    
    @JsonProperty("building_pending_deletion_updated_at")
    public String getBuildingPendingDeletionUpdatedAt() {
        return buildingPendingDeletionUpdatedAt;
    }

    @JsonProperty("building_pending_deletion_updated_at")
    public void setBuildingPendingDeletionUpdatedAt(String buildingPendingDeletionUpdatedAt) {
        this.buildingPendingDeletionUpdatedAt = buildingPendingDeletionUpdatedAt;
    }    
    
    
    @JsonProperty("building_payment_plan_id")
    public Integer getBuildingPaymentPlanId() {
        return buildingPaymentPlanId;
    }

    @JsonProperty("building_payment_plan_id")
    public void setBuildingPaymentPlanId(Integer buildingPaymentPlanId) {
        this.buildingPaymentPlanId = buildingPaymentPlanId;
    }

    
    @JsonProperty("building_payment_method_id")
    public Integer getBuildingPaymentMethodId() {
        return buildingPaymentMethodId;
    }

    @JsonProperty("building_payment_method_id")
    public void setBuildingPaymentMethodId(Integer buildingPaymentMethodId) {
        this.buildingPaymentMethodId = buildingPaymentMethodId;
    }

    
    @JsonProperty("building_stripe_subscription_id")
    public String getBuildingStripeSubscriptionId() {
        return buildingStripeSubscriptionId;
    }

    @JsonProperty("building_stripe_subscription_id")
    public void setBuildingStripeSubscriptionId(String buildingStripeSubscriptionId) {
        this.buildingStripeSubscriptionId = buildingStripeSubscriptionId;
    }    
    
    
    @JsonProperty("building_subscription_started_at")
    public String getBuildingSubscriptionStartedAt() {
        return buildingSubscriptionStartedAt;
    }

    @JsonProperty("building_subscription_started_at")
    public void setBuildingSubscriptionStartedAt(String buildingSubscriptionStartedAt) {
        this.buildingSubscriptionStartedAt = buildingSubscriptionStartedAt;
    }    

    
    @JsonProperty("building_subscription_current_interval_started_at")
    public String getBuildingSubscriptionCurrentIntervalStartedAt() {
        return buildingSubscriptionCurrentIntervalStartedAt;
    }

    @JsonProperty("building_subscription_current_interval_started_at")
    public void setBuildingSubscriptionCurrentIntervalStartedAt(String buildingSubscriptionCurrentIntervalStartedAt) {
        this.buildingSubscriptionCurrentIntervalStartedAt = buildingSubscriptionCurrentIntervalStartedAt;
    }    
 
    
    @JsonProperty("building_pending_payment_plan_id")
    public Integer getBuildingPendingPaymentPlanId() {
        return buildingPendingPaymentPlanId;
    }

    @JsonProperty("building_pending_payment_plan_id")
    public void setBuildingPendingPaymentPlanId(Integer buildingPendingPaymentPlanId) {
        this.buildingPendingPaymentPlanId = buildingPendingPaymentPlanId;
    }

    
    @JsonProperty("building_pending_payment_plan_updated_at")
    public String getBuildingPendingPaymentPlanUpdatedAt() {
        return buildingPendingPaymentPlanUpdatedAt;
    }

    @JsonProperty("building_pending_payment_plan_updated_at")
    public void setBuildingPendingPaymentPlanUpdatedAt(String buildingPendingPaymentPlanUpdatedAt) {
        this.buildingPendingPaymentPlanUpdatedAt = buildingPendingPaymentPlanUpdatedAt;
    }    
    
    
    @JsonProperty("building_grace_period_warning_notification_id")
    public Integer getBuildingGracePeriodWarningNotificationId() {
        return buildingGracePeriodWarningNotificationId;
    }

    @JsonProperty("building_grace_period_warning_notification_id")
    public void setBuildingGracePeriodWarningNotificationId(Integer buildingGracePeriodWarningNotificationId) {
        this.buildingGracePeriodWarningNotificationId = buildingGracePeriodWarningNotificationId;
    }

    
    @JsonProperty("building_payment_type")
    public String getBuildingPaymentType() {
        return buildingPaymentType;
    }

    @JsonProperty("building_payment_type")
    public void setBuildingPaymentType(String buildingPaymentType) {
        this.buildingPaymentType = buildingPaymentType;
    }    


    @Override
    public int compareTo(NonPointNodeDto that) {
      
      return this.nodeTypeId.compareTo(that.nodeTypeId);
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("NonPointNodeDto [nodeId=").append(nodeId).append(", nodeTypeId=")
          .append(nodeTypeId).append(", nodeName=").append(nodeName).append(", nodeDisplayName=")
          .append(nodeDisplayName).append(", nodeParentId=").append(nodeParentId)
          .append(", nodeParentNodeTypeId=").append(nodeParentNodeTypeId).append(", nodeCreatedAt=")
          .append(nodeCreatedAt).append(", nodeUpdatedAt=").append(nodeUpdatedAt)
          .append(", equipmentTypeId=").append(equipmentTypeId).append(", floorOrdinal=")
          .append(floorOrdinal).append(", buildingTimezone=").append(buildingTimezone)
          .append(", buildingAddress=").append(buildingAddress).append(", buildingCity=")
          .append(buildingCity).append(", buildingStateOrProvince=").append(buildingStateOrProvince)
          .append(", buildingPostalCode=").append(buildingPostalCode).append(", buildingCountryCode=")
          .append(buildingCountryCode).append(", buildingUnitSystem=").append(buildingUnitSystem).append(", buildingLatitude=")
          .append(buildingLatitude).append(", buildingLongitude=").append(buildingLongitude)
          .append(", buildingWeatherStationId=").append(buildingWeatherStationId)
          .append(", buildingTemporalId=").append(buildingTemporalId)
          .append(", buildingTemporalEffectiveDate=").append(buildingTemporalEffectiveDate)
          .append(", buildingTemporalSqft=").append(buildingTemporalSqft)
          .append(", buildingUtilityId=").append(buildingUtilityId)
          .append(", buildingUtilityComputationIntervalId=")
          .append(buildingUtilityComputationIntervalId).append(", buildingUtilityFormula=")
          .append(buildingUtilityFormula).append(", buildingUtilityRate=")
          .append(buildingUtilityRate).append(", buildingUtilityBaselineDescription=")
          .append(buildingUtilityBaselineDescription).append(", buildingUtilityUserNotes=")
          .append(buildingUtilityUserNotes).append(", buildingBillingGracePeriod=")
          .append(buildingBillingGracePeriod).append(", buildingStatus=").append(buildingStatus)
          .append(", buildingStatusUpdatedAt=").append(buildingStatusUpdatedAt)
          .append(", buildingPaymentStatus=").append(buildingPaymentStatus)
          .append(", buildingPaymentStatusUpdatedAt=").append(buildingPaymentStatusUpdatedAt)
          .append(", buildingPendingDeletion=").append(buildingPendingDeletion)
          .append(", buildingPendingDeletionUpdatedAt=").append(buildingPendingDeletionUpdatedAt)
          .append(", buildingPaymentPlanId=").append(buildingPaymentPlanId)
          .append(", buildingPaymentMethodId=").append(buildingPaymentMethodId)
          .append(", buildingStripeSubscriptionId=").append(buildingStripeSubscriptionId)
          .append(", buildingSubscriptionStartedAt=").append(buildingSubscriptionStartedAt)
          .append(", buildingSubscriptionCurrentIntervalStartedAt=")
          .append(buildingSubscriptionCurrentIntervalStartedAt)
          .append(", buildingPendingPaymentPlanId=").append(buildingPendingPaymentPlanId)
          .append(", buildingPendingPaymentPlanUpdatedAt=")
          .append(buildingPendingPaymentPlanUpdatedAt)
          .append(", buildingGracePeriodWarningNotificationId=")
          .append(buildingGracePeriodWarningNotificationId).append("]");
      return builder.toString();
    }
}

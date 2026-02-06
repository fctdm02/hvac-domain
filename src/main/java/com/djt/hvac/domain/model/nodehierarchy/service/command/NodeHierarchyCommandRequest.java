//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.service.command;

import java.util.List;

import com.djt.hvac.domain.model.common.service.command.AggregateRootCommandRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author tmyers
 *
 */
public interface NodeHierarchyCommandRequest extends AggregateRootCommandRequest {
  
  String BULK_POINT_MAPPING_OPERATION_CATEGORY = "PointMapping";
  String MAP_RAW_POINT_OPERATION_TYPE = "MapRawPoint";
  String UNMAP_RAW_POINT_OPERATION_TYPE = "UnmapRawPoint";
  String IGNORE_RAW_POINT_OPERATION_TYPE = "IgnoreRawPoint";
  String UNIGNORE_RAW_POINT_OPERATION_TYPE = "UnignoreRawPoint";
  
  String BULK_BUILDINGS_OPERATION_CATEGORY = "Buildings";
  String BULK_ENERGY_EXCHANGE_OPERATION_CATEGORY = "EnergyExchange";
  String BULK_POINT_OPERATION_CATEGORY = "Point";
  String BULK_AD_FUNCTION_INSTANCE_OPERATION_CATEGORY = "AdFunctionInstance";
  String BULK_REPORT_INSTANCE_OPERATION_CATEGORY = "ReportInstance";
  
  String BULK_NODE_OPERATION_CATEGORY = "Node";
  
  String MOVE_OPERATION_TYPE = "Move";
  String UPDATE_CUSTOM_POINT = "UpdateCustomPoint";

  String BULK_PORTFOLIO_OPERATION_CATEGORY = "Portfolio";
  String MAINTENANCE_OPERATION_TYPE = "Maintenance";
  String VALIDATE_OPERATION_TYPE = "Validate";
  String REMEDIATE_OPERATION_TYPE = "Remediate";
  String EVALUATE_REPORTS_OPERATION_TYPE = "EvaluateReports";
  String EVALUATE_PAYMENT_PROCESSING_OPERATION_TYPE = "PaymentProcess";

  String ALL = "All";
  String CREATE_OPERATION_TYPE = "Create";
  String UPDATE_OPERATION_TYPE = "Update";
  String DELETE_OPERATION_TYPE = "Delete";

  String FIND_AD_FUNCTION_CANDIDATE_RULES_OPERATION_TYPE = "FindRuleCandidates";
  String FIND_AD_FUNCTION_CANDIDATE_COMPUTED_POINTS_OPERATION_TYPE = "FindComputedPointCandidates";
  
  String CREATE_AD_FUNCTION_INSTANCE_RULES_OPERATION_TYPE = "CreateRules";
  String CREATE_AD_FUNCTION_INSTANCE_COMPUTED_POINTS_OPERATION_TYPE = "CreateComputedPoints";

  String UPDATE_AD_FUNCTION_INSTANCE_RULES_OPERATION_TYPE = "UpdateRules";
  String UPDATE_AD_FUNCTION_INSTANCE_COMPUTED_POINTS_OPERATION_TYPE = "UpdateComputedPoints";
  
  String DELETE_AD_FUNCTION_INSTANCE_RULES_OPERATION_TYPE = "DeleteRules";
  String DELETE_AD_FUNCTION_INSTANCE_COMPUTED_POINTS_OPERATION_TYPE = "DeleteComputedPoints";

  String SYSTEM = "SYSTEM";
  String EQUIPMENT = "EQUIPMENT";
  String PLANT = "PLANT";
  String LOOP = "LOOP";
  String RULE = "RULE";
  String COMPUTED_POINT = "COMPUTED_POINT";
  
  // Main goal of these operations is to retrieve/compute time-series data,
  // with the exception being the "fast track connector model data" operation,
  // which is responsible for retrieving model data from the connector and 
  // populating the model database.
  //
  // Once the fast track connector model data operation finishes, the user is
  // then permitted to map raw points (also, tag equipment/points), but unable
  // to enable rules, computed points and reports.  Once the fast track connector
  // time series data operation finishes, the user is then able to enable rules
  // and computed points, but unable to enable reports.
  //
  // Concurrent with rule/computed point time series data fast track operations, there
  // can also be custom computed point, scheduled computed point and weather data 
  // time series data fast track operations.  Once ALL these fast track operations 
  // complete, then the user is finally able to enable reports.
  //
  // Finally, once the user is able to enable reports, then they would be able to 
  // generate/download/view the report PDF.
  //
  // NOTES: 
  //  1. In order to reduce/eliminate user "wait time" for enabling rules, computed points or
  //     reports, the notion of "auto-enable" comes into play.  This feature, would automatically
  //     enable any eligible rules or computed points, once the fast track/back fill connector
  //     time-series data operations completes, as well as automatically enable any reports once
  //     the pre-requisite fast track/backfill "group 2" operations (i.e. rule/computed points,
  //     a.k.a. AD function output points, weather station points, scheduled points and custom points)
  //
  //  2. "Fast Track" is defined as the beginning of Sunday to the end of Saturday time frame for the
  //     "last completed week" for the given customer.  That is, if the customer configured the 
  //     connector on Thursday, 08/20/2020, then the "last completed week", a.k.a. "fast track week"
  //     would be defined as Sunday, 08/09/2020 to Saturday, 09/15/2020.  This is because the 
  //     current week, which started on Sunday, 08/16/2020, has not completed yet (again, because
  //     it is still Thursday)
  //
  //  3. Back fill (or "standard back fill) refers to the date range from the current time all the 
  //     way back to the customer's start date (should be the same as the customer "last run date"
  //     for the connector cloudfill fast track job. (Except for weather data, which goes back to the
  //     earliest start date of the oldest customer, which is 2015-01-01.)
  //
  //  4. If the customer fast track "last run date" is more than 2 days old, then no fast track jobs
  //     are triggered, just the standard back fill (verify with Carlos)
  // 
  //  5. If any fast track operations take more than 2 minutes, then they will be treated as complete, 
  //     as far as blocking to the UI is concerned.
  // 
  // The constants below are used to identity the async operations that are used for orchestration:
  String TIME_TO_VALUE_OPERATION_CATEGORY = "TimeToValue";
  String FAST_TRACK_CONNECTOR_MODEL_DATA = "FTConnectorMD";
  String FAST_TRACK_CONNECTOR_TIME_SERIES_DATA = "FTConnectorTS";
  String FAST_TRACK_AD_FUNCTION_TIME_SERIES_DATA = "FTAdFunctionTS";
  String FAST_TRACK_CUSTOM_POINT_TIME_SERIES_DATA = "FTCustPointTS";
  String FAST_TRACK_SCHEDULED_POINT_TIME_SERIES_DATA = "FTSchedPointTS";
  String FAST_TRACK_WEATHER_TIME_SERIES_DATA = "FTWeatherTS";
  
  String BACK_FILL_CONNECTOR_TIME_SERIES_DATA = "BFConnectorTS";
  String BACK_FILL_AD_FUNCTION_TIME_SERIES_DATA = "BFAdFunctionTS";
  String BACK_FILL_CUSTOM_POINT_TIME_SERIES_DATA = "BFCustPointTS";
  String BACK_FILL_SCHEDULED_POINT_TIME_SERIES_DATA = "BFSchedPointTS";
  String BACK_FILL_WEATHER_TIME_SERIES_DATA = "BFWeatherTS";
  
  /**
   * 
   * @return The owning customer id for the node hierarchy command request.  
   * 
   * NOTE: The command queue is actually a set of queues, one for each customer.
   * The result is that all commands for a given customer are processed FIFO, 
   * yet commands for *different* customers can be processed in parallel. 
   */
  Integer getCustomerId();
  
  /**
   * Since customer data is partitioned vertically between buildings, then 
   * specifying a building id helps with load time for the portfolio.
   * 
   * @return The building id for the node hierarchy command request.
   */
  Integer getBuildingId();

  /**
   * Since customer data is partitioned vertically between buildings, then 
   * specifying a building id helps with load time for the portfolio.
   * 
   * @return The building id for the node hierarchy command request.
   */
  List<Integer> getBuildingIds();
  
  /**
   * 
   * @return The email of the user that submitted the command request.
   *         (used for async operation processing)
   */
  String getSubmittedBy();  
  
  /**
   * 
   * @return Whether or not to perform automatic portfolio validation/remediation
   *         after the command request has been serviced.
   */
  Boolean getPerformAutomaticRemediation();

  /**
   * 
   * @return Whether or not to perform automatic generated report evaluation
   *         after the command request has been serviced.
   */
  Boolean getPerformAutomaticEvaluateReports();
  
  /**
   * 
   * @return Whether or not to perform automatic configuration, meaning that after
   *         every operation completes, including portfolio remediation and report
   *         evaluation, if enabled, any AD rule or computed point function
   *         candidates are converted to instances (i.e. enabled) and any reports 
   *         that are now valid, but disabled, are automatically enabled.
   */
  Boolean getPerformAutomaticConfiguration();
  
  /**
   * 
   * @return The operation category for the command request.
   *         (used for async operation processing)
   */
  @JsonIgnore
  String getOperationCategory();
  
  /**
   * 
   * @return The operation type for the command request.
   *         (used for async operation processing)
   */
  @JsonIgnore
  String getOperationType();

  public static interface Builder<T extends NodeHierarchyCommandRequest, B extends Builder<T, B>> {

    public B withCustomerId(Integer customerId);

    public B withBuildingId(Integer buildingId);

    public B withSubmittedBy(String submittedBy);

    public B withPerformAutomaticRemediation(Boolean performAutomaticRemediation);
    
    public B withPerformAutomaticEvaluateReports(Boolean performAutomaticEvaluateReports);

    public T build();
  }  
}
//@formatter:on
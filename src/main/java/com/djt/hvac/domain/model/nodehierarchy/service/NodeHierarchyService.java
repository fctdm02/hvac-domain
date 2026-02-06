//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.service;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.common.exception.StaleDataException;
import com.djt.hvac.domain.model.common.service.AggregateRootService;
import com.djt.hvac.domain.model.common.service.command.AggregateRootCommandProcessor;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.dictionary.event.DictionaryChangeEventPublisher;
import com.djt.hvac.domain.model.distributor.service.DistributorHierarchyStateEvaluator;
import com.djt.hvac.domain.model.function.AbstractAdFunctionInstanceEntity;
import com.djt.hvac.domain.model.function.dto.AdFunctionInstanceDto;
import com.djt.hvac.domain.model.function.status.AdFunctionErrorMessageSearchCriteria;
import com.djt.hvac.domain.model.function.status.AdFunctionErrorMessageSearchResponse;
import com.djt.hvac.domain.model.geocoding.exception.GeocodingClientLookupFailureException;
import com.djt.hvac.domain.model.nodehierarchy.AbstractNodeEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingSubscriptionEntity;
import com.djt.hvac.domain.model.nodehierarchy.dto.AddNodeDto;
import com.djt.hvac.domain.model.nodehierarchy.dto.custompoint.AsyncPoint;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.EnergyExchangeEntity;
import com.djt.hvac.domain.model.nodehierarchy.event.NodeHierarchyChangeEvent;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom.CustomAsyncComputedPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom.enums.ComputationInterval;
import com.djt.hvac.domain.model.nodehierarchy.point.mapped.MappablePointEntity;
import com.djt.hvac.domain.model.nodehierarchy.service.command.AddPointTemplateOverrideRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.CreateAdFunctionInstancesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.CreateNodeRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.DeleteAdFunctionInstancesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.DeleteChildNodesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.EvaluateReportsRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.FindAdFunctionInstanceCandidatesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.IgnoreRawPointsRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.MapRawPointsRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.MoveChildNodesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.NodeHierarchyCommandRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.NodeHierarchyCommandResponse;
import com.djt.hvac.domain.model.nodehierarchy.service.command.RemediatePortfolioRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.RemovePointTemplateOverrideRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.UnignoreRawPointsRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.UnmapRawPointsRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.UpdateAdFunctionInstancesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.UpdateBuildingNodesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.UpdateCustomAsyncComputedPointNodesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.UpdateEnergyExchangeSystemNodesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.UpdateMappablePointNodesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.UpdateReportInstancesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.ValidatePortfolioRequest;
import com.djt.hvac.domain.model.nodehierarchy.utils.LoadPortfolioOptions;
import com.djt.hvac.domain.model.rawpoint.RawPointEntity;
import com.djt.hvac.domain.model.report.ReportInstanceEntity;
import com.djt.hvac.domain.model.report.dto.ReportInstanceStatusDto;
import com.djt.hvac.domain.model.report.status.PortfolioReportSummaryValueObject;
import com.djt.hvac.domain.model.report.status.ReportEquipmentErrorMessageListResponse;
import com.djt.hvac.domain.model.stripe.exception.StripeClientException;
import com.djt.hvac.domain.model.timeseries.exception.TimeSeriesClientException;

/**
 * 
 * @author tommyers
 */
public interface NodeHierarchyService 
extends 
    AggregateRootCommandProcessor<NodeHierarchyCommandRequest, NodeHierarchyCommandResponse>, 
    AggregateRootService<PortfolioEntity, NodeHierarchyChangeEvent>,
    DictionaryChangeEventPublisher {
  
  /**
   * CACHE RELATED
   */
  String CUSTOM_COMPUTED_POINT_CACHE_KEY_PREFIX = "CUSTOM_COMPUTED_POINTS_STATE_BUILDING_ID_";
  
  /**
   * The delimiter for metric ids
   */
  String DEFAULT_METRIC_ID_DELIMITER = "/";
  
  /**
   * The value used to represent NULL in the update request DTOs (i.e. -1)
   */
  Integer NULL = Integer.valueOf(-1);

  /**
   * The value used to represent ANY in the update request DTOs (i.e. -2)
   */
  Integer ANY = Integer.valueOf(-2);

  /**
   * The value used to represent the wildcard char in the update request DTOs (i.e. "*")
   */
  String WILDCARD = "*";
  
  /**
   * Ensures that all the dictionary data is loaded into the appropriate container classes.
   */
  void ensureDictionaryDataIsLoaded();
   
  /**
   * Loads a portfolio from the repository.
   * 
   * @param customerId
   * 
   * @return The portfolio identified by <code>customerId</code>
   * 
   * @throws EntityDoesNotExistException If the customer does not exist
   */
  PortfolioEntity loadPortfolio(
      Integer customerId) throws EntityDoesNotExistException;
  
  /**
   * Loads a portfolio from the repository.
   * 
   * @param loadPortfolioOptions The options to load the portfolio:
   * <pre>
   *        customerId The owning customer
   * 
   *        filterNodeType Can be one of:
   *        null
   *        BUILDING
   *        SUB_BUILDING
   *        FLOOR
   *        EQUIPMENT
   *        POINT
   *        
   *        The distributor, customer and portfolio are always loaded, 
   *        when the filter is specified, only the filter node and below
   *        are loaded (so you can only load a building if you want)
   * 
   *        filterNodePersistentIdentity If filterNodeType is specified, 
   *        then this the ID of that node to filter by.
   * 
   *        depthNodeType (required) Can be one of:
   *        BUILDING
   *        SUB_BUILDING
   *        FLOOR
   *        EQUIPMENT
   *        POINT
   *        
   *        This method signature is designed to NOT load AD functions or 
   *        report instances.  Rather, it is designed to load a portfolio 
   *        down to a node type depth specified. 
   *        
   *        For example, one can load a specific building, by specifying 
   *        both the filter node type and identity and node the depth, 
   *        which means that there could only be four objects returned
   *        in the portfolio entity for an absolute minimum:
   *        1. Resolute Root Distributor
   *        2. Ancestor Distributor Hierarchy (if it exists)
   *        3. Parent Customer
   *        4. Parent Portfolio Node
   *        5. Building Node (of interest)
   * 
   *        loadAdFunctionInstances If <code>false</code>, AD function instances will not be loaded
   * 
   *        loadReportInstances If <code>false</code>, report instances will not be loaded
   *        (operations such as validation, remediation and rule candidate finding, don't need them).
   * 
   *        loadPointLastValues If <code>false</code> point last value and last value timestamps
   *        will not be loaded, as most operations will not need them)
   * 
   *        loadBuildingTemporalData Whether or not to load building temporal data
   * 
   * @return The portfolio identified by customerId that is a partial 
   *         subtree of the portfolio node hierarchy whereby only a 
   *         full subtree is returned for the given combination of 
   *         filterNodeType and filterNodePersistentIdentity and
   *         depthNodeType
   *         
   * NOTE: Allowable combinations of filterNodeType/depthNodeType:
   * ---------------------------------------------------------------------------------------------
   * PORTFOLIO/PORTFOLIO - loads only the portfolio node (used for stale data checking)
   * 
   * null/BUILDING - loads all buildings for customer down to building level only
   * null/SUB_BUILDING - loads all buildings for customer down to sub building level only
   * null/FLOOR - loads all buildings for customer down to floor level only
   * null/EQUIPMENT - loads all buildings for customer down to equipment level only
   * null/POINT - loads all buildings for customer down to point level only
   * 
   * BUILDING/BUILDING - loads the specified building down to building level only 
   * BUILDING/SUB_BUILDING - loads the specified building down to sub building level only
   * BUILDING/FLOOR - loads the specified building down to floor level only
   * BUILDING/EQUIPMENT - loads the specified building down to equipment level only
   * BUILDING/POINT - loads the specified building down to point level only
   * 
   * SUB_BUILDING/SUB_BUILDING - loads the specified sub building down to sub building level only
   * SUB_BUILDING/FLOOR - loads the specified sub building down to floor level only
   * SUB_BUILDING/EQUIPMENT - loads the specified sub building down to equipment level only
   * SUB_BUILDING/POINT - loads the specified sub building down to point level only
   * 
   * FLOOR/FLOOR - loads the specified floor down to floor level only
   * FLOOR/EQUIPMENT - loads the specified floor down to equipment level only
   * FLOOR/POINT - loads the specified floor down to point level only
   * 
   * EQUIPMENT/EQUIPMENT - loads the specified equipment down to the floor level only
   * EQUIPMENT/POINT - loads the specified equipment down to point level only
   * 
   * POINT/POINT - loads the specified point down to point level only
   * 
   * @throws EntityDoesNotExistException If the portfolio node or customer
   *         does not exist.
   */
  PortfolioEntity loadPortfolio(
      LoadPortfolioOptions loadPortfolioOptions) 
  throws 
      EntityDoesNotExistException;
  
  /**
   * @param validatePortfolioRequest A request wrapper containing:
   * <pre>
   *   customerId The owning customer id
   *   issueTypes If non-null/non-empty, contains a list of issue types that
   *              will only be included in the result (i.e. all others are excluded)
   * </pre>
   * 
   * @return A list of validation messages
   * 
   * @throws EntityDoesNotExistException If an entity does not exist
   */
  List<ValidationMessage> validatePortfolio(
      ValidatePortfolioRequest validatePortfolioRequest)
  throws 
      EntityDoesNotExistException;  

  /**
   * @param remediatePortfolioRequest A request wrapper containing:
   * <pre>
   *   customerId The owning customer id
   *   issueTypes If non-null/non-empty, contains a list of issue types that
   *              will only be included in the result (i.e. all others are excluded)
   * </pre>
   * 
   * @return A list of validation messages
   * 
   * @throws EntityDoesNotExistException If an entity does not exist
   * @throws StaleDataException If a stale data exception occurred, meaning that another
   * process updated the portfolio in the interval of time between this process 
   * loading/updating the same portfolio 
   */
  List<ValidationMessage> remediatePortfolio(
      RemediatePortfolioRequest remediatePortfolioRequest)
  throws 
      EntityDoesNotExistException,
      StaleDataException;  
  
  /**
   * 
   * @param request A request wrapper containing:
   * <pre>
   * customerId: owning customer
   * buildingId: the specific building to evaluate (optional)
   * reportTemplateId: the specific report to evaluate (optional)
   * submittedBy: the user requesting the evaluation (will be SYSTEM
   *              for the system scheduled job
   * </pre>
   * 
   * @return The affected report instances (in DTO form)
   * 
   * @throws EntityDoesNotExistException If the portfolio and/or any instances couldn't
   *         be found or do not belong to the specified building (if non-null)
   * @throws StaleDataException If a stale data exception occurred, meaning that another process updated
   *         the portfolio in the interval of time between this process loading/updating the same portfolio         
   */
  List<ReportInstanceStatusDto> evaluateReports(
      EvaluateReportsRequest request)
  throws
      EntityDoesNotExistException,
      StaleDataException;
  
  /**
   * 
   * @param customerId The owning customer
   * @param buildingId The id of the building
   * @param reportTemplateId The id of the report template
   * @param equipmentId The id of the equipment
   * 
   * @return A formatted, color coded, point tuple constraint, 
   *         for the given equipment, in the given building, 
   *         for the given report, where the available point spec
   *         names are in green and the missing point spec names
   * 
   * @throws EntityDoesNotExistException If the portfolio, building
   *         or report template doesn't exist
   */
  String getFormattedReportTupleConstraintErrorMessage(
      Integer customerId,
      Integer buildingId, 
      Integer reportTemplateId, 
      Integer equipmentId)
  throws
      EntityDoesNotExistException;  
  
  /**
   * @param customerId The owning customer id
   * @param noInternalReports If <code>true</code>, then no report templates with 
   * the 'internal' flag set to <code>true</code> will be returned.
   * @param rubyTimezoneLabel The ruby timezone label e.g. "Eastern Time (US & Canada)"
   * 
   * @return A portfolio report status summary (building level info only)
   * 
   * @throws EntityDoesNotExistException If the customer does not exist
   */
  PortfolioReportSummaryValueObject getReportConfigurationStatus(
      Integer customerId,
      boolean noInternalReports,
      String rubyTimezoneLabel)
  throws
      EntityDoesNotExistException;

  /**
   * 
   * @param customerId The owning customer
   * 
   * @return The percentage of green equipment to green equipment plus red equipment
   *         
   * @throws EntityDoesNotExistException If the portfolio for the given 
   *         <code>customerId</code> doesn't exist
   */
  Double getReportConfigurationStatusPercent(
      Integer customerId)
  throws
      EntityDoesNotExistException;

  /**
   * 
   * NOTE: ignored instances do not count toward the total number
   * 
   * @param customerId The owning customer
   * 
   * @return The percentage of enabled reports to enabled plus disabled reports
   *         
   * @throws EntityDoesNotExistException If the portfolio for the given 
   *         <code>customerId</code> doesn't exist
   */
  Double getEnabledReportsPercent(
      Integer customerId)
  throws
      EntityDoesNotExistException;
  
  /**
   * @param customerId The owning customer id
   * @param buildingId The building to get equipment error messages for
   * @param reportTemplateId The report temnplate to get equipment error messages for
   * @param nodePath A search filter for the equipment node path
   * @param sortDirection "asc" or "desc" 
   * @param limit The max number of equipment error messages to retrieve
   * @param offset The starting offset
   * 
   * @return A list of equipment level error messages 
   */
  ReportEquipmentErrorMessageListResponse getReportEquipmentErrorMessages(
      Integer customerId,
      int buildingId, 
      int reportTemplateId,
      String nodePath,
      String sortDirection,
      int limit,
      int offset);
  
  /**
   * 
   * @param updateReportInstancesRequest A request wrapper containing:
   * <pre>
   *   customerId
   *   submittedBy
   *   list of ReportInstanceData
   *   
   *   Each ReportInstanceData has:
   *   buildingId
   *   reportTemplateId
   *   priority: LOW, MEDIUM or HIGH
   *   enabled:  If true, then will enable the report instance (assuming disabled currently)
   *   disabled: If true, then will disable the report instance (assuming enabled currently)
   *   ignored: If true, then will ignore the report instance (assuming disabled currently)
   *   
   *   NOTES: 
   *   - At least one of the above must be non-null
   *   - If a report is invalid, then it cannot be enabled
   *   - If a report cannot both be enabled and ignored
   * </pre>
   * 
   * @return The list of updated report instance entities
   * 
   * @throws EntityDoesNotExistException If the customer or buildings/report templates in
   *         the request do not exist
   * @throws StaleDataException If a stale data exception occurred, meaning that another
   *         process updated the portfolio in the interval of time between this process 
   *         loading/updating the same portfolio 
   */
  List<ReportInstanceEntity> updateReportInstances(UpdateReportInstancesRequest updateReportInstancesRequest) 
  throws 
      EntityDoesNotExistException,
      StaleDataException;
  
  /**
   * @param request A request wrapper containing:
   * <pre>
   *   functionType The specific type of ad function template to generate candidates for
   *   buildingIds The id(s) of the building(s) to process (optional)
   * </pre>
   * 
   * @return The list of ad function instance candidates that were generated
   * 
   * @throws EntityDoesNotExistException If the portfolio for the given customer does not exist
   * @throws StaleDataException If a stale data exception occurred, meaning that another
   *         process updated the portfolio in the interval of time between this process 
   *         loading/updating the same portfolio 
   */
  List<AdFunctionInstanceDto> findAdFunctionInstanceCandidates(
      FindAdFunctionInstanceCandidatesRequest request)
  throws
      EntityDoesNotExistException,
      StaleDataException;
  
  /**
   * 
   * @param request The request to create instances from candidates can be specified
   *        in one of two ways (the first using candidate persistent identities and 
   *        the second using the persistent identities of the candidate AD function 
   *        template and equipment.  That is: a candidate/instance is uniquely 
   *        identified by the combination of template and equipment:
   *        <ol>
   *          <li>candidateIds: The list of AD Function Instance Candidate ids</li>
   *          <li>candidateTemplateEquipmentIds: A map, whose keys are AD function 
   *              template ids and values are the lists of equipment to create the
   *              AD function instances for</li>
   *        </ol>
   *        
   * @return The newly activated AD function instances
   * 
   * @throws EntityDoesNotExistException If the portfolio and/or any instances couldn't
   *         be found or do not belong to the specified building (if non-null)
   * @throws StaleDataException If a stale data exception occurred, meaning that another process updated
   *         the portfolio in the interval of time between this process loading/updating the same portfolio         
   */
  List<AbstractAdFunctionInstanceEntity> createAdFunctionInstancesFromCandidates(
      CreateAdFunctionInstancesRequest request)
  throws
      EntityDoesNotExistException,
      StaleDataException;
  
  /**
   * 
   * @param request AD function instances ids to update.  Each instance contains a map of key/value pairs
   *        where the key is the constant name and the value is the constant value to update
   *         
   * @return The list of updated AD function instances
   * 
   * @throws EntityDoesNotExistException If the portfolio and/or any instances couldn't
   *         be found or do not belong to the specified building (if non-null)
   * @throws StaleDataException If a stale data exception occurred, meaning that another process updated
   *         the portfolio in the interval of time between this process loading/updating the same portfolio         
   */
  List<AbstractAdFunctionInstanceEntity> updateAdFunctionInstances(
      UpdateAdFunctionInstancesRequest request)
  throws
      EntityDoesNotExistException,
      StaleDataException;
  
  /**
   * 
   * @param request AD function instances ids to delete.
   * 
   * @return The list of deleted AD function instances.
   * 
   * @throws EntityDoesNotExistException If the portfolio and/or any instances couldn't
   *         be found or do not belong to the specified building (if non-null)
   * @throws StaleDataException If a stale data exception occurred, meaning that another process updated
   *         the portfolio in the interval of time between this process loading/updating the same portfolio         
   */
  List<AbstractAdFunctionInstanceEntity> deleteAdFunctionInstances(
      DeleteAdFunctionInstancesRequest request)
  throws
      EntityDoesNotExistException,
      StaleDataException;
  
  /**
   * 
   * @param parentCustomer The owning customer
   * @param name The name to use for the portfolio (should be the name of the customer)
   * @param displayName The display name to use
   * 
   * @return The newly created portfolio node
   * 
   * @throws EntityAlreadyExistsException If the portfolio node already exists
   * @throws EntityDoesNotExistException If the parent customer does not exist
   */
  PortfolioEntity createPortfolio(
      AbstractCustomerEntity parentCustomer,
      String name,
      String displayName)
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException;
  
  /**
   * 
   * NOTE: This generalized update method SHOULD NOT BE USED by the 
   * application layer.  Instead the specific operations that correspond
   * to UI use cases should be used:
   * <ol>
   *   <li>map raw points: a.k.a bulk point mapping</li>
   *   <li>update equipment nodes: a.k.a bulk equipment tagging</li>
   *   <li>update mappable point nodes: a.k.a bulk point tagging</li>
   *   <li>update custom async computed point nodes</li>
   *   <li>create child node</li>
   *   <li>move child nodes: a.k.a bulk node move</li>
   *   <li>delete child nodes: a.k.a bulk node delete</li>
   * </ol>
   * 
   * Updates a portfolio to the repository, which means that any:
   * <ol>
   *   <li>Created nodes (i.e. persistentIdentity == null) are inserted</li>
   *   <li>Modified nodes (i.e. isModified == true) are updated</li>
   *   <li>Deleted nodes (i.e. isDeleted == true) are deleted</li>
   * <ol>  
   * 
   * @param portfolio The portfolio to update
   * @param commandRequest The command request containing all the info
   *        needed to service the request
   * 
   * @return a NodeHierarchyChangeEvent that describes what nodes were added,
   *         updated and deleted, as well as any AD function instances/reports
   *         that were enabled/disabled
   *         
   * @throws StaleDataException If a stale data exception occurred, meaning that another
   *         process updated the portfolio in the interval of time between this process 
   *         loading/updating the same portfolio         
   */
  NodeHierarchyChangeEvent updatePortfolio(
      PortfolioEntity portfolio,
      NodeHierarchyCommandRequest commandRequest)
  throws 
      StaleDataException;
  
  /**
   * 
   * @param mapRawPointsRequest A request wrapper that contains:
   * <pre>
   *   customerId The owning customer 
   *   rawPoints The raw points to map, each consists of:
   *       rawPointId The raw point id
   *       metricId The metric id
   *   buildingName The building name filter
   *   subBuildingNames The list of sub building names to exclude/include
   *   plantNames The list of plant names to exclude/include
   *   floorNames The list of floor names to exclude/include
   *   equipmentNames  The list of equipment names to exclude/include
   *   pointNames  The list of point names to exclude/include
   *   performExclusionOnNames Whether or not to exclude/include names (doesn't apply for building name)
   *   mappingExpression The mapping expression to use
   *   metricIdDelimiter The metric id delimiter to use
   * </pre>
   * 
   * @return A list of eligible raw points that were the 
   * result of applying building point caps to the buildings
   * and points that would result from the point mapping.
   * In other words, it filters out those raw points that 
   * would result in the building going over its mapped 
   * point cap/limit
   * 
   * @throws EntityAlreadyExistsException
   * @throws EntityDoesNotExistException
   */
  Collection<RawPointEntity> getEligibleRawPointsForMapping(
      MapRawPointsRequest mapRawPointsRequest)
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException;
  
  /**
   * 
   * @param mapRawPointsRequest A request wrapper that contains:
   * <pre>
   *   customerId The owning customer 
   *   rawPoints The raw points to map
   *   buildingName The building name filter
   *   subBuildingNames The list of sub building names to exclude/include
   *   plantNames The list of plant names to exclude/include
   *   floorNames The list of floor names to exclude/include
   *   equipmentNames  The list of equipment names to exclude/include
   *   pointNames  The list of point names to exclude/include
   *   performExclusionOnNames Whether or not to exclude/include names (doesn't apply for building name)
   *   mappingExpression The mapping expression to use
   *   metricIdDelimiter The metric id delimiter to use
   *   
   *   The following are the allowable node type tokens for mapping expression:
   *   {*} 
   *   {building}
   *   {subBuilding}
   *   {floor}
   *   {equipment}
   *   {point}
   *   
   The following are the allowable combinations of node type tokens:
   =================================================================
    // 2 nodes
    building, point

    // 3 nodes
    building, subBuilding, point
    building, plant, point
    building, floor, point
    building, equipment, point

    // 4 nodes
    building, subBuilding, plant, point
    building, subBuilding, floor, point
    building, subBuilding, equipment, point
    building, floor, equipment, point
    building, equipment, equipment, point
    
    // 5 nodes
    building, subBuilding, floor, equipment, point
    building, subBuilding, equipment, equipment, point
    building, floor, equipment, equipment, point
    
    // 6 nodes
    building, subBuilding, floor, equipment, equipment, point
    
    The following combinations are valid for parsing, but anything under a plant 
    that's not a point will be moved to the immediate parent of the plant, which
    will either be a building or sub building:
    ==========================================
    building, plant, floor, point
    building, plant, floor, equipment, point
    building, plant, floor, equipment, equipment, point
    building, plant, equipment, point
    building, plant, equipment, equipment, point
    building, subBuilding, plant, floor, point
    building, subBuilding, plant, floor, equipment, point
    building, subBuilding, plant, floor, equipment, equipment, point
    building, subBuilding, plant, equipment, point
    building, subBuilding, plant, equipment, equipment, point
   * </pre>
   * 
   * @return A list of the mappable point nodes that were actually
   * created (i.e. point cap constraint honored)
   * 
   * @throws EntityAlreadyExistsException 
   * @throws EntityDoesNotExistException 
   * @throws StaleDataException If a stale data exception occurred, meaning that another
   *         process updated the portfolio in the interval of time between this process 
   *         loading/updating the same portfolio 
   */
  List<MappablePointEntity> mapRawPoints(
      MapRawPointsRequest mapRawPointsRequest)
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException,
      StaleDataException;
  
  /**
   * @param request A request wrapper containing:
   * <pre>
   *        customerId The owning customer
   *        nodeType The node type to create. Can be one of:
   *        BUILDING, 
   *        SUB_BUILDING, 
   *        FLOOR, 
   *        EQUIPMENT, PLANT, LOOP
   *        POINT  
   *        
   *        If POINT, then "pointType" must be
   *        specified in <code>additionalProperties</code>
   *        and must be one of:
   *        ASYNC_COMPUTED_POINT or
   *        SCHEDULED_ASYNC_COMPUTED_POINT
   *        
   *        If LOOP, then "energyExchangeSystemTypeId" must be 
   *        specified in <code>additionalProperties</code>
   *        and must be one of:
   *        1: Chilled Water
   *        2: Hot Water
   *        3: Steam
   *        4: Air Supply
   *        and is used to set energy exchange system type for the
   *        parent plant (so, which has to be specified by parentId)
   *        
   *        parentId The id of the parent node
   *        
   *        name The name to use
   *        
   *        displayName The display name to use
   *        
   *        additionalProperties A map of additional properties, 
   *        which are by node type:
   * </pre>
   *        
   * @return The newly created node 
   * 
   * @throws EntityDoesNotExistException If either the portfolio or parent nodes don't exist 
   * @throws EntityAlreadyExistsException If a node with the specified name already exists under
   *         the given parent     
   * @throws StaleDataException If a stale data exception occurred, meaning that another
   *         process updated the portfolio in the interval of time between this process 
   *         loading/updating the same portfolio 
   */
  AbstractNodeEntity createNode(CreateNodeRequest request)
  throws 
      EntityDoesNotExistException,
      EntityAlreadyExistsException,
      StaleDataException;

  /**
   * 
   * @param updateBuildingNodesRequest A request wrapper containing:
   * <pre>
   *   customerId The owning customer
   *   data Each request object consists of:
   *        id: The id of the building to update
   *        displayName: The display name of the building to set
   *        
   *        NOTE: If all of the values are the same as the existing/old values, then the 
   *        building will not be updated and it will NOT be in the return list.
   *        
   *        buildingPaymentType:RP-10818: If an online distributor and whose "allowOutOfBandBuildings" is true, then if a building is still
   *        in the "trial" period, then it has a special attribute, an enum called "buildingPaymentType" whose values
   *        are ONLINE(default for online customer) and OUT_OF_BAND, can be changed as follows:
   *        
   *        1. If ONLINE, can be changed to OUT_OF_BAND
   *        2. If OUT_OF_BAND, can be changed back to ONLINE if, and only if, the building point cap is under the max point cap
   *        of the payment band with the highest cap.
   * </pre>
   * 
   * @return A list of the building nodes that were actually updated
   * 
   * @throws GeocodingClientLookupFailureException If there was a problem geocoding the building address into lat/long coordinates
   * @throws EntityDoesNotExistException If any nodes in the request could not be found
   * @throws EntityAlreadyExistsException If there already exists a temporal config with the given date
   * @throws StaleDataException If a stale data exception occurred, meaning that another
   *         process updated the portfolio in the interval of time between this process 
   *         loading/updating the same portfolio 
   */
  List<BuildingEntity> updateBuildingNodes(
      UpdateBuildingNodesRequest updateBuildingNodesRequest)
  throws 
      GeocodingClientLookupFailureException,
      EntityDoesNotExistException,
      EntityAlreadyExistsException,
      StaleDataException;
  
  /**
   * 
   * @param updateEnergyExchangeSystemNodesRequest A request wrapper containing:
   * <pre>
   *   customerId The owning customer id
   *   buildingId The building id
   *   energyExchangeSystemNodeId The id of the single energy exchange node to load (for node editor)
   *   submittedBy The email of the user that the request is being submitted on behalf of
   *   performAutomaticRemediation Whether or not to perform automatic remediation after the operation work is complete
   *   performAutomaticEvaluateReports Whether or not to perform automatic report evaluation after the operation work is complete
   *   performAutomaticConfiguration Whether or not to perform automatic configuration after the operation work is complete
   *   data Each request object consists of:
   *        id: The id of the equipment to update
   *        
   *        displayName: The display name of the equipment to set
   *        
   *        typeId: The id of the equipment type to set
   *        
   *        nodeType: The type of energy exchange node: EQUIPMENT, PLANT or LOOP
   *        
   *        systemTypeId: Must be specified when either parentIds or childIds are specified. One of:
   *            1: Chilled Water
   *            2: Hot Water
   *            3: Steam
   *            4: Air Supply        
   *        
   *        parentIds: The ids of the parents for the energy exchange systems
   *        To ignore this field, set to NULL.  To remove all parents, set to be an empty list.
   *        If non-null, then any existing parents not in the list will be removed.
   *        
   *        childIds: The ids of the children for the energy exchange systems
   *        To ignore this field, set to NULL.  To remove all children, set to be an empty list.
   *        If non-null, then any existing children not in the list will be removed.
   *        
   *        metadataTagIds: A list of tag ids that correspond to tags of the "Metadata" tag group (tag group id=8)
   *        
   *        convertToNodeType: One of EQUIPMENT, PLANT or LOOP.  If the current node type is EQUIPMENT, the node can be 
   *        "converted" to either PLANT or LOOP, provided that the parent node types are compatible:
   *        EQUIPMENT: can have sub-buildings, buildings, floors or other equipment as parents
   *        PLANT: can have sub-buildings or buildings as parents
   *        LOOP: can have sub-buildings or buildings as parents
   *        
   *        Only energy exchange nodes that have only points as children can be converted, as plants/loops do not support child equipment.
   *        
   *        NOTE: If all of the values are the same as the existing/old values, then the 
   *        equipment/plants/loops will not be updated and it will NOT be in the return list.
   * </pre>
   * 
   * @return A list of the energy exchange system nodes (equipment, plants or loops) that were actually updated
   * 
   * @throws EntityDoesNotExistException If any of the nodes or equipment/plants/loops types specified by the ids
   *         in the request cannot be found
   * @throws EntityAlreadyExistsException If there already exists an energy exchange system relation for a given system
   *         and parent node
   * @throws StaleDataException If a stale data exception occurred, meaning that another
   *         process updated the portfolio in the interval of time between this process 
   *         loading/updating the same portfolio 
   */
  List<EnergyExchangeEntity> updateEnergyExchangeSystemNodes(
      UpdateEnergyExchangeSystemNodesRequest updateEnergyExchangeSystemNodesRequest)
  throws 
      EntityDoesNotExistException,
      EntityAlreadyExistsException,
      StaleDataException;

  /**
   * 
   * @param updateMappablePointNodesRequest A request wrapper containing:
   * <pre>
   *        useGrouping: If true, then the values in the individual point request update objects
   *                     may represent a GROUP of points, all of whom have the same values of the
   *                     OLD values, and are to be updated to all have the same values for the
   *                     NEW values specified.  Each point/point group request object consists of:
   *                     
   *        id: When 'useGrouping' is false, the id of the mappable point node to update. When false,
   *        this value will be set in the point group request object to be a unique artificial id, in
   *        order to distinguish it from the other point groups in the outer request object.         
   *            
   *        name: The name of the mappable point(s) NOTE: This value is never changed
   *         
   *        oldDisplayName: The old/existing display name
   *        displayName: The new display name to set
   *        
   *        oldPointTemplateId: The old/existing point template id
   *        pointTemplateId: The new point template id to set
   *        
   *        oldUnitId: The old/existing unit id
   *        unitId: The new unit id to set
   *        
   *        pointDataTypeId: When 'useGrouping' is true, then data type id of the mappable point. 
   *        If Boolean or Enum and a non null/empty range is specified, then the range is validated. 
   *        parentEquipmentTypeId:
   *        
   *        range: The value of the range to set (Used ONLY when 'useGrouping' is false)
   *        
   *        metadataTags: The point tags to set (tag group 6, NOT haystack tags, used ONLY when 'useGrouping' is false
   *        
   *        quantity: When 'useGrouping' is true, this value will be set in the point group request
   *        object to be the number of individual mappable points that were updated to have the values
   *        specified in the request.
   *        
   *        NOTE: If all of the values are the same as the existing/old values, then the 
   *        mappable point/point group will not be updated and it will NOT be in the return list.
   * 
   *        performAutomaticRemediation If <code>true</code>, then automatic portfolio 
   *        remediation is performed.
   * </pre>
   * 
   * @return A list of the mappable point nodes that were actually updated
   * 
   * @throws EntityDoesNotExistException If any of the nodes, point templates parent equipment 
   *         types specified by the ids in the request cannot be found
   * @throws StaleDataException If a stale data exception occurred, meaning that another
   *         process updated the portfolio in the interval of time between this process 
   *         loading/updating the same portfolio 
   */
  List<MappablePointEntity> updateMappablePointNodes(
      UpdateMappablePointNodesRequest updateMappablePointNodesRequest)
  throws 
      EntityDoesNotExistException,
      StaleDataException;
  
  /**
   * 
   * @param request A request wrapper containing:
   * <pre>
   *        customerId The owning customer
   *        customAsyncComputedPointNodes A list of update requests for custom async computed points
   *        that can (initially) update the following:
   *        
   *        displayName: The new display name to set
   *        pointTemplateId: The new point template id to set
   *        unitId: The new unit id to set
   *        
   *        TODO: TDM: Add support for non-haystack tags (i.e. direct assignment), as well
   *        as everything that is i the "computed point" tab of the node editor UI
   * </pre>
   * 
   * @return A list of the custom async computed point nodes that were actually updated
   * 
   * @throws EntityDoesNotExistException If the customer, or any of the points, specified
   *         by the ids in the request cannot be found
   * @throws EntityAlreadyExistsException If an entity already exists
   * @throws StaleDataException If a stale data exception occurred, meaning that another
   *         process updated the portfolio in the interval of time between this process 
   *         loading/updating the same portfolio 
   */
  List<CustomAsyncComputedPointEntity> updateCustomAsyncComputedPointNodes(
      UpdateCustomAsyncComputedPointNodesRequest request)
  throws 
      EntityDoesNotExistException,
      EntityAlreadyExistsException,
      StaleDataException;
  
  /**
   * 
   * @param moveChildNodesRequest A request wrapper containing:
   * <pre>
   *   customerId The owning portfolio
   *   buildingId The building id (optional)
   *   newParentId The new parent node id
   *   childIds A list of child node ids to move
   *   submittedBy Email address of the submitting user
   * </pre>
   *  
   * @return The list of child nodes that are <b>eligible</b> to be moved.
   * That is, with the moving of the child node to the building belonging
   * to new parent, the total mapped point count would be less than or equal
   * to that building's point cap.
   * 
   * @throws EntityAlreadyExistsException
   * @throws EntityDoesNotExistException
   */
  List<AbstractNodeEntity> getEligibleChildNodesforMoveToNewParentNode(
      MoveChildNodesRequest moveChildNodesRequest)
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException;
  
  /**
   * 
   * @param moveChildNodesRequest A request wrapper containing:
   * <pre>
   *   customerId The owning portfolio
   *   buildingId The building id (optional)
   *   newParentId The new parent node id
   *   childIds A list of child node ids to move
   *   submittedBy Email address of the submitting user
   * </pre>
   *  
   * @return A list of nodes that were actually moved
   * (i.e. point cap honored)
   * 
   * @throws EntityAlreadyExistsException 
   * @throws EntityDoesNotExistException 
   * @throws StaleDataException If a stale data exception occurred, meaning that another
   *         process updated the portfolio in the interval of time between this process 
   *         loading/updating the same portfolio 
   */
  List<AbstractNodeEntity> moveChildNodesToNewParentNode(
      MoveChildNodesRequest moveChildNodesRequest)
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException,
      StaleDataException;
  
  /**
   * 
   * @param deleteChildNodesRequest A request wrapper containing:
   * <pre>
   *   customerId The owning portfolio
   *   buildingId The building id (optional)
   *   childIds A list of child node ids to delete
   *   ignoreMappablePointRawPoint When true and deleting mappable points, 
   *       then mark the corresponding raw points as ignored as well
   *   submittedBy Email address of the submitting user
   * </pre>
   *  
   * @return The list of nodes that were deleted. NOTE: They will be detached
   * from their parents in that they are not reachable from the root portfolio
   * node anymore.
   * 
   * @throws EntityDoesNotExistException 
   * @throws StaleDataException If a stale data exception occurred, meaning that another
   *         process updated the portfolio in the interval of time between this process 
   *         loading/updating the same portfolio 
   */
  List<AbstractNodeEntity> deleteChildNodes(
      DeleteChildNodesRequest deleteChildNodesRequest)
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException,
      StaleDataException;
  
  /**
   * 
   * @param request A wrapper containing:
   * <pre>
   *   customerId The owning customer
   *   rawPointIds The raw points to ignore
   * </pre>  
   *  
   * @return A map whose keys are the raw points from the request and 
   *         values are the mappable points that they had been associated with.
   *         NOTE: They will be detached from their parents in that they are 
   *         not reachable from the root portfolio node anymore.
   * 
   * @throws EntityDoesNotExistException 
   * @throws StaleDataException If a stale data exception occurred, meaning that another
   *         process updated the portfolio in the interval of time between this process 
   *         loading/updating the same portfolio 
   */
  Map<Integer, MappablePointEntity> unmapRawPoints(UnmapRawPointsRequest request)
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException,
      StaleDataException;
  
  /**
   * 
   * @param request A wrapper containing:
   * <pre>
   *   customerId The owning customer
   *   rawPointIds The raw points to ignore
   * </pre>
   * 
   * @return true, if successful
   * 
   * @throws EntityDoesNotExistException If the customer does not exist
   * @throws StaleDataException If a stale data exception occurred, meaning that another
   *         process updated the portfolio in the interval of time between this process 
   *         loading/updating the same portfolio 
   */
  Boolean ignoreRawPoints(IgnoreRawPointsRequest request)
  throws
      EntityDoesNotExistException,
      StaleDataException;
  
  /**
   * 
   * @param request A wrapper containing:
   * <pre>
   *   customerId The owning customer
   *   rawPointIds The raw points to ignore
   * </pre>
   * 
   * @return true, if successful
   * 
   * @throws EntityDoesNotExistException If the customer does not exist
   */
  Boolean unignoreRawPoints(UnignoreRawPointsRequest request)
  throws
      EntityDoesNotExistException;
  
  /**
   * 
   * This operation will succeed if and only if:
   * <pre>
   * 1: The Stripe client was able to create a Stripe subscription
   * 2: The Stripe client was able to post the first payment successfully
   * using the corresponding Stripe payment method for the given Resolute
   * payment method
   * 
   * NOTE: It is assumed the parent customer/distributor are online and that
   * the given payment method belongs to the distributor. This applies to 
   * online customers only.
   * </pre>
   * @param customerId The owning customer
   * @param billableBuildingId The billable building to create the subscription for
   * @param paymentPlanId The Resolute payment plan to create the subscription for
   * @param paymentMethodId The Resolute payment method to use
   * 
   * @return A successfully processed subscription. (including all the Stripe actions)
   * 
   * @throws EntityAlreadyExistsException If the building subscription has already been created
   * @throws EntityDoesNotExistException If an entity does not exist
   * @throws StaleDataException If a stale data exception occurred, meaning that another
   *         process updated the portfolio in the interval of time between this process 
   *         loading/updating the same portfolio 
   * @throws StripeClientException If a problem with Stripe occurred
   */
  BuildingSubscriptionEntity createBuildingSubscription(
      Integer customerId,
      Integer billableBuildingId,
      Integer paymentPlanId,
      Integer paymentMethodId) 
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException,
      StaleDataException,
      StripeClientException;  
  
  /**
   * This operation does two things that are done atomically:
   * <pre>
   * 1. Cancel the Stripe subscription
   * 2. Set the 'pending deletion' flag on the building, which means that the building
   * will be hard deleted only AFTER the current payment interval has expired.
   * </pre>
   * @param customerId The owning customer
   * @param billableBuildingId The building to cancel the subscription for
   * 
   * @throws EntityDoesNotExistException If the building does not have a subscription
   * @throws StaleDataException If a stale data exception occurred, meaning that another
   *         process updated the portfolio in the interval of time between this process 
   *         loading/updating the same portfolio 
   * @throws StripeClientException If a problem with Stripe occurred
   */
  void cancelBuildingSubscription(
      Integer customerId,
      Integer billableBuildingId) 
  throws 
      EntityDoesNotExistException,
      StaleDataException,
      StripeClientException;  
  
  /**
   * GENERAL SETTING CHANGE
   * 
   * NOTE: This applies to online customers only
   * 
   * @param customerId The owning customer
   * @param billableBuildingId The billable building to update the subscription for
   * @param paymentMethodId The Resolute payment method to use
   * 
   * @return The updated building subscription entity
   * 
   * @throws EntityDoesNotExistException If an entity does not exist
   * @throws StaleDataException If a stale data exception occurred, meaning that another
   *         process updated the portfolio in the interval of time between this process 
   *         loading/updating the same portfolio 
   * @throws StripeClientException If a problem with Stripe occurred
   */
  BuildingSubscriptionEntity updateBuildingSubscriptionForNewPaymentMethod(
      Integer customerId,
      Integer billableBuildingId,
      Integer paymentMethodId) 
  throws 
      EntityDoesNotExistException,
      StaleDataException,
      StripeClientException;
    
  /**
   * GENERAL SETTING CHANGE
   * 
   * NOTE: This change occurs on-demand and is initiated by the user and applies to
   * online customers only
   * 
   * @param customerId The owning customer
   * @param billableBuildingId The billable building to update the subscription for
   * @param paymentPlanId The Resolute payment plan to use
   * 
   * @return The updated building subscription entity
   * 
   * @throws EntityDoesNotExistException If an entity does not exist
   * @throws StaleDataException If a stale data exception occurred, meaning that another
   *         process updated the portfolio in the interval of time between this process 
   *         loading/updating the same portfolio 
   * @throws StripeClientException If a problem with Stripe occurred
   */
  BuildingSubscriptionEntity updateBuildingSubscriptionForNewPaymentPlanSameInterval(
      Integer customerId,
      Integer billableBuildingId,
      Integer paymentPlanId) 
  throws 
      EntityDoesNotExistException,
      StaleDataException,
      StripeClientException;
  
  /**
   * RENEWAL SETTING CHANGE
   * 
   * NOTE: This method is invoked by the system-scheduled "batch job" that performs nightly
   * processing.  This method would be invoked when the subscription's current payment interval
   * has passed/expired/ended. This applies to online customers only.
   * 
   * @param customerId The owning customer
   * @param billableBuildingId The billable building to update the subscription for
   * @param paymentPlanId The Resolute payment plan to use
   * 
   * @return The updated building subscription entity
   * 
   * @throws EntityDoesNotExistException If an entity does not exist
   * @throws StaleDataException If a stale data exception occurred, meaning that another
   *         process updated the portfolio in the interval of time between this process 
   *         loading/updating the same portfolio 
   * @throws StripeClientException If a problem with Stripe occurred
   */
  BuildingSubscriptionEntity updateBuildingSubscriptionForNewPaymentPlanDifferentInterval(
      Integer customerId,
      Integer billableBuildingId,
      Integer paymentPlanId) 
  throws 
      EntityDoesNotExistException,
      StaleDataException,
      StripeClientException;

  /**
   * NOTE: This applies to online customers only and should only be invoked by the system 
   * scheduled job that performs nightly payment processing. This method should be invoked
   * every time the scheduled job runs to see if any delinquent subscriptions have had
   * their payment issues resolved (i.e. this is to be invoked many times per day)
   * 
   * @param portfolio The portfolio to perform Stripe payment processing on
   * 
   * @throws StripeClientException If a problem occurred
   */
  void performStripeDelinquentPaymentProcessing(PortfolioEntity portfolio) throws StripeClientException;
  
  /**
   * NOTE: This applies to online customers only and should only be invoked by the system 
   * scheduled job that performs nightly payment processing.
   * 
   * @param portfolio The portfolio to perform Stripe payment processing on
   * 
   * @throws StripeClientException If a problem occurred
   */
  void performStripePaymentProcessing(PortfolioEntity portfolio) throws StripeClientException;

  /**
   * This method chains together a series of other service calls, but this method
   * does all the operations on the portfolio domain entity for all the operations
   * and then stores the results to the database once (i.e. added entities are INSERTED,
   * modified entities are UPDATED and removed entities are DELETED)
   * 
   * <ol>
   *   <li>Portfolio Validation</li>
   *   <li>Portfolio Remediation</li>
   *   <li>Report Evaluation</li>
   *   <li>Find AD Rule Function Candidates</li>
   *   <li>Find AD Computed Point Function Candidates</li>
   *   <li>Payment Processing</li>
   * </ol>
   * 
   * @param customerIds The customers to process
   * @param distributorHierarchyStateEvaluator The distributor hierarchy state evaluator to use
   * @param performStripePaymentProcessing Whether or not to perform Stripe processing
   * 
   * @return A list of error messages, if no errors were encountered during processing, then the list will be empty
   * 
   * @throws EntityDoesNotExistException If the portfolio does not exist
   * @throws StripeClientException If there was a problem with Stripe
   */
  List<String> performPortfolioMaintenance(
      List<Integer> customerIds,
      DistributorHierarchyStateEvaluator distributorHierarchyStateEvaluator,
      boolean performStripePaymentProcessing);  
  
  /**
   * 
   * @return true if the underlying repository is JDBC 
   * (needed as some operations aren't needed)
   */
  boolean hasDatabaseRepository();
  
  /**
   * 
   * USED TO FACILITATE TESTING WITH REAL DATA
   * 
   * Exports a portfolio from the DB to the filesystem.  
   * 
   * @param customerId The owning customer
   * 
   * @throws EntityDoesNotExistException If the customer portfolio does not exist
   */
  void exportPortfolio(Integer customerId) throws EntityDoesNotExistException;

  /**
   * 
   * USED TO FACILITATE TESTING WITH REAL DATA
   * 
   * Exports a portfolio from the DB to the filesystem.  
   * 
   * @param customerId The owning customer
   * @param exportDictionaryData If <code>true</code> exports required dictionary data
   * 
   * @throws EntityDoesNotExistException If the customer portfolio does not exist
   */
  void exportPortfolio(Integer customerId, boolean exportDictionaryData) throws EntityDoesNotExistException;
  
  /**
   * 
   * USED TO FACILITATE TESTING WITH REAL DATA
   * 
   * Exports a portfolio from the DB to the filesystem.  
   * 
   * @param customerId The owning customer
   * @param exportDictionaryData If <code>true</code> exports required dictionary data
   * @param exportPath The local filesystem path to export to
   * 
   * @throws EntityDoesNotExistException If the customer portfolio does not exist
   */
  void exportPortfolio(Integer customerId, boolean exportDictionaryData, String exportPath) throws EntityDoesNotExistException;  

  /**
   * 
   * USED TO FACILITATE TESTING WITH REAL DATA
   * 
   * Exports a portfolio from the DB to the filesystem.  
   * 
   * @param customerId The owning customer
   * @param buildingId The building to filter on (optional)
   * @param exportDictionaryData If <code>true</code> exports required dictionary data
   * @param exportPath The local filesystem path to export to
   * 
   * @throws EntityDoesNotExistException If the customer portfolio does not exist
   */
  void exportPortfolio(Integer customerId, Integer buildingId, boolean exportDictionaryData, String exportPath) throws EntityDoesNotExistException;  

  /**
   * 
   * USED TO FACILITATE TESTING WITH REAL DATA
   * 
   * Exports a portfolio from the DB to the filesystem.  
   * 
   * @param customerId The owning customer
   * @param buildingId The building to filter on (optional)
   * @param exportDictionaryData If <code>true</code> exports required dictionary data
   * @param exportCustomAsyncComputedPointId The custom async computed point 
   *        to get time-series data for (along with their variables's time-series data)
   * @param exportTimeSeriesDataStartTimestamp If not null, the start time for time series data
   * @param exportTimeSeriesDataEndTimestamp If not null, the end time for time series data
   * @param exportPath The local filesystem path to export to
   * 
   * @throws EntityDoesNotExistException If the customer portfolio does not exist
   */
  void exportPortfolio(
      Integer customerId,
      Integer buildingId,
      boolean exportDictionaryData,
      Integer exportCustomAsyncComputedPointId,
      Timestamp exportTimeSeriesDataStartTimestamp,
      Timestamp exportTimeSeriesDataEndTimestamp, 
      String exportPath) throws EntityDoesNotExistException;  
  
  /**
   * This service method will delegate to the repository and is used in the application layer in order to get the 
   * ancestor building for any given descendant type, which can then be used for other service method calls that
   * take a building id as a filter.
   * 
   * @param customerId The owning customer
   * @param descendantId The persistent identity of the descendant
   * 
   * @return The persistent identity of the ancestor building
   * 
   * @throws EntityDoesNotExistException If either the customer or the descendant doesn't exist
   */
  Integer getBuildingIdForDescendantId(
      Integer customerId, 
      Integer descendantId)
  throws 
      EntityDoesNotExistException;

  /**
   * This service method will delegate to the repository and is used in the application layer in order to get the 
   * ancestor building(s) for the given customer portfolio.
   * 
   * @param customerId The owning customer
   * 
   * @return The persistent identities of the portfolio building(s)
   */
  List<Integer> getBuildingIds(Integer customerId);
  
  /**
   * This service method will delegate to the repository and is used in the application layer in order to get the 
   * ancestor building(s) for any given descendant type, which can then be used for other service method calls that
   * take a building id, or building ids, as a filter.
   * 
   * @param customerId The owning customer
   * @param descendantIds The persistent identities of the descendant nodes
   * 
   * @return The persistent identities of the ancestor building(s)
   * 
   * @throws EntityDoesNotExistException If either the customer or the descendant doesn't exist
   */
  Set<Integer> getBuildingIdsForDescendantIds(
      Integer customerId, 
      Collection<Integer> descendantIds)
  throws 
      EntityDoesNotExistException;
  
  /**
   * This service method will delegate to the repository and is used in the application layer in order to get the 
   * ancestor building(s) for a given set of AD function instance ids, which can then be used for other service method
   * calls that take a building id, or building ids, as a filter.
   * 
   * @param customerId The owning customer
   * @param instanceIds The persistent identities of the AD function instances
   * 
   * @return The persistent identities of the ancestor building(s)
   * 
   * @throws EntityDoesNotExistException If either the customer or the descendant doesn't exist
   */
  Set<Integer> getBuildingIdsForAdFunctionInstanceIds(
      Integer customerId,
      Collection<Integer> instanceIds)
  throws 
      EntityDoesNotExistException;
  
  /**
   * This service method will delegate to the repository and is used in the application layer in order to get the 
   * ancestor building(s) for a given set of raw point ids, which can then be used for other service method
   * calls that take a building id, or building ids, as a filter.
   * 
   * @param customerId The owning customer
   * @param rawPointIds The persistent identities of the raw points
   * 
   * @return The persistent identities of the ancestor building(s)
   * 
   * @throws EntityDoesNotExistException If either the customer or the descendant doesn't exist
   */
  Set<Integer> getBuildingIdsForRawPointIds(
      Integer customerId,
      Collection<Integer> rawPointIds)
  throws 
      EntityDoesNotExistException;
  
  /**
   * 
   * @param distributorId The distributor
   * 
   * @return A map, whose keys are the payment method ids, and whose values are
   * the "ref count" for those payment methods.  That is, if a payment method is
   * associated with 3 building subscriptions, then its ref count would be 3.
   * 
   * NOTE: Payment methods cannot be deleted when they have a non-zero ref count.
   * 
   * @throws EntityDoesNotExistException If the distributor does not exist
   */
  Map<Integer, Integer> getPaymentMethodRefCounts(
      int distributorId)
  throws 
      EntityDoesNotExistException;

  /**
   * 
   * @param customerId The owning customer
   * @param functionType Either "Rule" or "Computed Point"
   * 
   * @return The percentage of equipment/plants/loops that are compliant, in that
   *         they have all required points and/or meet the point tuple constraint
   *         expression (i.e. being in either the candidate/instance state) against
   *         the total number of eligible equipment/plants/loop (i.e. with the 
   *         right type and matching node filter expression).
   *         
   * @throws EntityDoesNotExistException If the portfolio for the given 
   *         <code>customerId</code> doesn't exist
   */
  Double getAdFunctionConfigurationStatusPercent(
      Integer customerId,
      String functionType)
  throws
      EntityDoesNotExistException;
  
  /**
   * 
   * NOTE: ignored instances do not count toward the total number
   * 
   * @param customerId The owning customer
   * @param functionType Either "Rule" or "Computed Point"
   * 
   * @return The percentage of enabled AD function instances to enabled plus disabled (i.e. candidates)
   *         
   * @throws EntityDoesNotExistException If the portfolio for the given 
   *         <code>customerId</code> doesn't exist
   */
  Double getEnabledAdFunctionInstancesPercent(
      Integer customerId,
      String functionType)
  throws
      EntityDoesNotExistException;
  
  /**
   * 
   * @param customerId The owning customer
   * @param searchCriteria The search criteria, which consists of:
   * <pre>
       functionTypeId (required): Either 1 for Rule or 2 for Computed Point
       energyExchangeId (optional): The id of the equipment, plant or loop
       adFunctionTemplateId (optional): The id of the AD rule/computed point function template
       nodePath (optional): The node path
       sortDirection (required): The sort direction (default is 'asc')
       offset (required): The offset (default is 0)
       limit (required): The limit (default is 1,000)
   * </pre>
   * @return The AD function error messsages that correspond to the given search criteria 
   */
  AdFunctionErrorMessageSearchResponse getAdFunctionErrorMessages(
      Integer customerId, 
      AdFunctionErrorMessageSearchCriteria searchCriteria);

  /**
   * 
   * NOTE: To be used only for testing purposes!!!
   * 
   * Duplicates ALL buildings in a portfolio, duplicationFactor number of times
   * <pre>
   * GIVEN: Two buildings in the portfolio (buildingA, buildingB)
   * GIVEN: startingIndex=1
   * GIVEN: duplicationFactor=2
   * 
   * BEFORE:
   *   buildingA
   *   buildingB
   *   
   * AFTER:
   *   buildingA
   *   buildingB
   *   buildingA_1
   *   buildingB_1
   *   buildingA_2
   *   buildingB_2
   * </pre>   
   * 
   * @param customerId The owning customer
   * @param duplicationFactor The number of copies to make
   * @param startingIndex The starting index (will default to 1 if null/not specified)
   * @throws EntityDoesNotExistException If the portfolio doesn't exist
   * @throws StaleDataException If another process updated the repository 
   *         after this method loaded the portfolio
   */
  void duplicatePortfolio(
      Integer customerId,
      Integer startingIndex,
      Integer duplicationFactor)
  throws
      EntityDoesNotExistException,
      StaleDataException;
  
  /**
   * 
   * NOTE: To be used only for testing purposes!!!
   * 
   * Duplicates source building, duplicationFactor number of times
   * Duplicates ALL buildings in a portfolio, duplicationFactor number of times
   * <pre>
   * SOURCE BUILDING: buildingA
   * GIVEN: startingIndex=1
   * GIVEN: duplicationFactor=2
   * 
   * BEFORE:
   *   buildingA
   *   
   * AFTER:
   *   buildingA
   *   buildingA_1
   *   buildingA_2
   * </pre>   
   * 
   * @param customerId The owning customer
   * @param sourceBuildingId The building to duplicate
   * @param startingIndex The starting index (will default to 1 if null/not specified)
   * @param duplicationFactor The number of copies to make
   * 
   * @throws EntityDoesNotExistException If the portfolio or building doesn't exist
   * @throws StaleDataException If another process updated the repository 
   *         after this method loaded the portfolio
   */
  void duplicateBuilding(
      Integer customerId,
      Integer sourceBuildingId,
      Integer startingIndex,
      Integer duplicationFactor)
  throws
      EntityDoesNotExistException,
      StaleDataException;
  
  /**
   * 
   * @param request The point template unit mapping override request to add
   * 
   * @return <code>true</code> If added successfully, <code>false</code> otherwise. 
   * 
   * @throws EntityAlreadyExistsException If the given point template unit mapping request already exists
   * @throws EntityDoesNotExistException If the given distributor, customer, building, point template or unit mapping does not exist
   * @throws StaleDataException If another process updated the repository 
   *         after this method loaded the portfolio
   */
  Boolean addPointTemplateUnitMappingOverride(
      AddPointTemplateOverrideRequest request)
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException,
      StaleDataException;
  
  /**
   * 
   * @param request The point template unit mapping override request to remove
   * 
   * @return <code>true</code> If removed successfully, <code>false</code> otherwise. 
   * 
   * @throws EntityDoesNotExistException If the given distributor, customer, building, point template or unit mapping does not exist
   * @throws StaleDataException If another process updated the repository 
   *         after this method loaded the portfolio
   */
  Boolean removePointTemplateUnitMappingOverride(
      RemovePointTemplateOverrideRequest request)
  throws 
      EntityDoesNotExistException,
      StaleDataException;

  /**
   * 
   * @param customerId The owning customer
   * @param buildingId (Optional) The specific building to process
   * @param pointId (Optional) The specific point to process (must belong to building, if building is specified), default is all buildings
   * @param computationIntervals (Optional) The specific computation interval typed points to process, default is all types (quarter hour, daily and monthly)
   * @param performRecalculate (Optional) Whether or a recalculation is to be performed, default is false. startTimestamp and endTimestamp cannot be specified when performRecalculate is true
   * @param startTimestamp (Optional) The start time for processing, default is: last processed time, or effective date if not exists, or performRecalculate is true (per point basis)
   * @param endTimestamp (Optional) The end time for processing, default is current time, adjusted for computation interval (per point basis)
   * 
   * @return A list of errors, if any, during processing
   * 
   * @throws EntityDoesNotExistException If the customer, building or point within the building doesn't exist
   * @throws TimeSeriesClientException If there was an issue retrieving/posting time-series data
   */
  List<String> evaluateCustomAsyncPoints(
      Integer customerId,
      Integer buildingId,
      Integer pointId,
      Set<ComputationInterval> computationIntervals,
      Boolean performRecalculate,
      Timestamp startTimestamp,
      Timestamp endTimestamp)
  throws
      EntityDoesNotExistException,
      TimeSeriesClientException;
  
  // FAST LANE WRITER OPERATIONS (I.E. DOES NOT LOAD PORTFOLIO, DOES STRAIGHT INSERT/UPDATE)
  /**
   * 
   * @param customerId The owning customerId
   * @param nodeId The nodeId
   * @param tagId The tagId
   * @return The number of rows added
   */
  int addNodeTag(int customerId, int nodeId, int tagId);

  /**
   * 
   * @param customerId The owning customerId
   * @param nodeId The nodeId
   * @param tagId The tagId
   * @return The number of rows removed
   */
  int removeNodeTag(int customerId, int nodeId, int tagId);
  
  /**
   * 
   * @param customerId The owning customerId
   * @param dtoList The dtoList 
   * @return The enhanced node list
   */
  List<AddNodeDto> addNodes(int customerId, List<AddNodeDto> dtoList);

  /**
   * 
   * @param customerId The owning customerId
   * @param nodeData The node to create
   * @return The newly created node
   */
  AddNodeDto addNode(int customerId, AddNodeDto nodeData);
  
  /**
   * 
   * @param dto The async point
   * @return The async point
   */
  AsyncPoint addCustomAsyncComputedPoint(AsyncPoint dto);
  
  /**
   * 
   * @param dto The async point
   * @return The async point
   */
  AsyncPoint updateCustomAsyncComputedPoint(AsyncPoint dto);
  
  /**
   * 
   * @param customerId The owning customerId
   * @param nodeId The nodeId
   * @param nodeTypeId The nodeTypeId
   * @param pointTypeId The point type (if the node is of type point)
   * @param nodeDisplayName The nodeDisplayName
   */
  void updateNodeDisplayName(int customerId, int nodeId, int nodeTypeId, int pointTypeId, String nodeDisplayName);
  
}
//@formatter:on
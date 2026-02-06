//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.repository;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.common.exception.StaleDataException;
import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.dictionary.PaymentPlanEntity;
import com.djt.hvac.domain.model.dictionary.enums.FunctionType;
import com.djt.hvac.domain.model.distributor.paymentmethod.AbstractPaymentMethodEntity;
import com.djt.hvac.domain.model.function.status.AdFunctionErrorMessageSearchCriteria;
import com.djt.hvac.domain.model.function.status.AdFunctionErrorMessagesValueObject;
import com.djt.hvac.domain.model.nodehierarchy.AbstractNodeEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BillableBuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingSubscriptionEntity;
import com.djt.hvac.domain.model.nodehierarchy.dto.AddNodeDto;
import com.djt.hvac.domain.model.nodehierarchy.dto.TagInfo;
import com.djt.hvac.domain.model.nodehierarchy.dto.custompoint.AsyncPoint;
import com.djt.hvac.domain.model.nodehierarchy.event.NodeHierarchyChangeEvent;
import com.djt.hvac.domain.model.nodehierarchy.service.command.NodeHierarchyCommandRequest;
import com.djt.hvac.domain.model.nodehierarchy.utils.LoadPortfolioOptions;
import com.djt.hvac.domain.model.report.status.PortfolioReportSummaryValueObject;
import com.djt.hvac.domain.model.report.status.ReportEquipmentErrorMessageSearchCriteria;
import com.djt.hvac.domain.model.report.status.ReportEquipmentErrorMessageValueObject;

/**
 * 
 * @author tommyers
 * 
 */
public interface NodeHierarchyRepository {

  /**
   * Loads a portfolio from the repository.
   * 
   * Everything is loaded:
   * - AD Function Instances and Instance Candidates
   * - Report Instances
   * - Point last values
   * 
   * @param customerId
   * 
   * @return The portfolio identified by <code>customerId</code>
   * 
   * @throws EntityDoesNotExistException
   */
  PortfolioEntity loadPortfolio(int customerId) throws EntityDoesNotExistException;

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
   * 
   * @param customerId The owning customer id
   * @return The "updatedAt" timestamp for the given portfolio
   */
  Timestamp getPortfolioNodeUpdatedAt(int customerId);
  
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
      int customerId,
      boolean noInternalReports,
      String rubyTimezoneLabel)
  throws
      EntityDoesNotExistException;

  /**
   * 
   * @param customerId The owning customer id
   * @param searchCriteria The parameters of the search
   * 
   * @return A total count of for the given 
   */
  int getReportEquipmentErrorMessagesCount(
      int customerId,
      ReportEquipmentErrorMessageSearchCriteria searchCriteria);  
  
  /**
   * 
   * @param customerId The owning customer id
   * @param searchCriteria The parameters of the search
   * 
   * @return A list of equipment level error messages in a response wrapper
   * that includes the given search criteria and count
   */
  List<ReportEquipmentErrorMessageValueObject> getReportEquipmentErrorMessages(
      int customerId,
      ReportEquipmentErrorMessageSearchCriteria searchCriteria);
  
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
   * Updates a portfolio to the repository, which consists of:
   * <ol>
   *   <li>Created nodes (i.e. persistentIdentity == null) are inserted</li>
   *   <li>Updated nodes (i.e. isModified == true) are updated</li>
   *   <li>Deleted nodes (i.e. isDeleted == true) are deleted</li>
   *   <li>Enabled AD Function Instances</li>
   *   <li>Enabled Report Instances</li>
   *   <li>Disabled AD Function Instances</li>
   *   <li>Disabled Report Instances</li>
   * <ol>  
   * 
   * @param portfolio The portfolio to update
   * @param commandRequest The command request that initiated the call
   * @param reportsWereEvaluated If reports were evaluated (report timestamps will be updated)
   * 
   * @return A map, keyed an operation type of CREATED, UPDATED or DELETED,
   *         where each contains a list of nodes of that type
   *         
   * @throws StaleDataException If the portfolio's updated at timestamp
   *         does not match that which is stored in the repository.
   */
  String CREATED = "CREATED";
  String UPDATED = "UPDATED";
  String DELETED = "DELETED";
  String ENABLED_AD_FUNCTION_INSTANCES = "ENABLED_AD_FUNCTION_INSTANCES";
  String ENABLED_REPORT_INSTANCES = "ENABLED_REPORT_INSTANCES";
  String DISABLED_AD_FUNCTION_INSTANCES = "DISABLED_AD_FUNCTION_INSTANCES";
  String DISABLED_REPORT_INSTANCES = "DISABLED_REPORT_INSTANCES";
  Map<String, List<AbstractNodeEntity>> storePortfolio(
      PortfolioEntity portfolio,
      NodeHierarchyCommandRequest commandRequest,
      boolean reportsWereEvaluated)
  throws
      StaleDataException;
  
  /**
   * 
   * This operation will succeed if and only if:
   * 1: If the Stripe customer does not exist, one is created
   * 2: The Stripe client was able to create a Stripe subscription
   * 3: The Stripe client was able to post the first payment successfully
   * using the corresponding Stripe payment method for the given Resolute
   * payment method
   * 
   * NOTE: It is assumed the parent customer/distributor are online and that
   * the given payment method belongs to the distributor.
   * 
   * @param billableBuilding The billable building to create the subscription for
   * @param paymentPlan The Resolute payment plan to create the subscription for
   * @param paymentMethod The Resolute payment method to use
   * 
   * @return A successfully processed subscription. (including all the Stripe actions)
   * 
   * @throws EntityAlreadyExistsException If an entity already exists
   * @throws EntityDoesNotExistException If an entity does not exist
   */
  BuildingSubscriptionEntity createBuildingSubscription(
      BillableBuildingEntity billableBuilding,
      PaymentPlanEntity paymentPlan,
      AbstractPaymentMethodEntity paymentMethod,
      String stripeSubscriptionId) 
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException;
  
  /**
   * This is used in the application layer in order to get the ancestor building for any 
   * given descendant type, which can then be used for other service method calls that
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
   * @param customerId The owning customer
   * 
   * @return The list of building ids for the given customer
   */
  List<Integer> getBuildingIds(Integer customerId);
  
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
      FunctionType functionType)
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
      FunctionType functionType)
  throws
      EntityDoesNotExistException;
  
  /**
   * 
   * @param customerId The owning customer id
   * @param searchCriteria The parameters of the search
   * 
   * @return A total count of for the given 
   */
  int getAdFunctionErrorMessagesCount(
      int customerId,
      AdFunctionErrorMessageSearchCriteria searchCriteria);  
  
  /**
   * 
   * @param customerId The owning customer id
   * @param searchCriteria The parameters of the search
   * 
   * @return A list of equipment level error messages
   */
  List<AdFunctionErrorMessagesValueObject> getAdFunctionErrorMessagesData(
      int customerId,
      AdFunctionErrorMessageSearchCriteria searchCriteria);
  
  /**
   * 
   * @param nodeHierarchyChangeEvent The node hierarchy change event to store
   */
  void storeNodeHierarchyChangeEvent(NodeHierarchyChangeEvent nodeHierarchyChangeEvent);
  
  // FAST LANE WRITER OPERATIONS (I.E. DOES NOT LOAD PORTFOLIO, DOES STRAIGHT INSERT/UPDATE)
  /**
   * 
   * @param tagId The tagId
   * @return The tag info
   */
  TagInfo getTagInfo(int tagId);
  
  /**
   * 
   * @param customerId The owning customerId
   * @param nodeId The nodeId
   * @param tagId The tagId
   * @return The number of rows inserted
   */
  int insertNodeTag(int customerId, int nodeId, int tagId);

  /**
   * 
   * @param customerId The owning customerId
   * @param nodeId The nodeId
   * @param tagId The tagId
   * @return The number of rows deleted
   */
  int deleteNodeTag(int customerId, int nodeId, int tagId);
  
  /**
   * 
   * @param customerId The owning customerId
   * @param The node to add 
   * @return THe newly created node
   */
  AddNodeDto insertNode(int customerId, AddNodeDto dto);
  
  /**
   * 
   * @param customerId The owning customerId
   * @param dtoList The dtoList 
   * @return The enhanced node list
   */
  List<AddNodeDto> insertNodes(int customerId, List<AddNodeDto> dtoList);
  
  /**
   * 
   * @param dto The async point
   * @return The async point
   */
  AsyncPoint insertCustomAsyncComputedPoint(AsyncPoint dto);

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
   * @param nodeDisplayName The nodeDisplayName
   * @param nodeTypeId The nodeTypeId
   * @param pointTypeId The point type (if the node is of type point)
   */
  void updateNodeDisplayName(int customerId, int nodeId, int nodeTypeId, int pointTypeId, String nodeDisplayName);
  
}
//@formatter:on
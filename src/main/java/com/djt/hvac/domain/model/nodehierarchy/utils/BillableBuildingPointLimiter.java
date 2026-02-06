//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.djt.hvac.domain.model.common.dsl.pointmap.Node;
import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.customer.OnlineCustomerEntity;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.container.PaymentPlansContainer;
import com.djt.hvac.domain.model.nodehierarchy.AbstractNodeEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BillableBuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingSubscriptionEntity;
import com.djt.hvac.domain.model.nodehierarchy.service.command.MapRawPointsRequest;
import com.djt.hvac.domain.model.rawpoint.RawPointEntity;
import com.google.common.collect.Maps;

/**
 * 
 * @author tommyers
 * 
 */
public final class BillableBuildingPointLimiter {

  /**
   * USE CASES: (ONLINE CUSTOMERS ONLY)
   * 
   * ONE: Point mapping that results in one or more points being 
   * mapped to a new (billable) building: The point cap is that of 
   * the payment plan with the highest point cap. This building is
   * immediately transitioned to the PENDING_ACTIVATION state.
   * 
   * TWO: Point mapping that results in points mapped to an 
   * existing building, but that building is still in the 
   * PENDING_ACTIVATION state: The point cap is that of 
   * the payment plan with the highest point cap.
   * 
   * THREE: The building is in the ACTIVE state (meaning that a 
   * subscription for the building has been created): The point 
   * cap is from the subscription's associated payment plan.
   * 
   * NOTE: The point mapping process is only concerned about
   * limiting mapped points to be at or below the point cap, 
   * which again, is either that of the greatest payment plan when
   * there is no subscription yet, or that of the associated 
   * payment plan of the subscription. That is, the point mapping
   * process is *not* concerned with the actual payment status for
   * the building.
   * 
   * @param portfolio The online customer's portfolio to perform
   * point mapping against
   * 
   * @param rawPoints A map containing the set of raw points and corresponding nodes to be mapped
   * 
   * @return A filtered list of exons that would keep the mapped
   * point count under the point caps for any buildings that are involved
   * in the point mapping.
   */
  public static Map<RawPointEntity,List<Node>> processForBuildingPointCaps(
      PortfolioEntity portfolio,
      Map<RawPointEntity,List<Node>> rawPoints,
      MapRawPointsRequest request) {
    
    // Verify that the customer is online.
    AbstractCustomerEntity customer = portfolio.getParentCustomer();
    if (!(customer instanceof OnlineCustomerEntity)) {
      throw new IllegalStateException("Customer: ["
          + customer
          + "] is not an instance of OnlineCustomerEntity");
    }
    
    // Build a map of building names and their existing mapped point count.
    Map<String, Integer> buildingCurrentMappedPointCounts = new HashMap<>();
    
    // Build a map of building names whose values will be the pending mapped point count.
    Map<String, Integer> buildingPendingMappedPointCounts = new HashMap<>();
    
    // Keep track of the new buildings.
    Set<String> newBuildingNames = new TreeSet<>();
    
    // Build a map of building names and their point cap. This is from the building 
    // subscription.  If one does not exist yet, then the point cap is that of the 
    // greatest payment plan.
    Map<String, Integer> buildingMappedPointCaps = new HashMap<>();
    
    // For those buildings that do not have a subscription yet, the point cap is that
    // of the greatest payment plan.
    PaymentPlansContainer paymentPlansContainer = DictionaryContext.getPaymentPlansContainer();
    Integer maxPointCap = paymentPlansContainer.getMaxPointCap();
    if (paymentPlansContainer == null || maxPointCap.intValue() < 0) {
      throw new IllegalStateException("paymentPlansContainer has not been initialized: "
          + paymentPlansContainer);
    }
    
    // For each building, get its current mapped point count and its point cap.
    for (BuildingEntity building: portfolio.getChildBuildings()) {
      
      buildingCurrentMappedPointCounts.put(
          building.getName(), 
          building.getTotalMappedPointCount());
      
      buildingPendingMappedPointCounts.put(
          building.getName(), 
          Integer.valueOf(0));
      
      if (building instanceof BillableBuildingEntity) {

        BillableBuildingEntity billableBuilding = (BillableBuildingEntity)building;
        BuildingSubscriptionEntity subscription = billableBuilding.getChildBuildingSubscriptionNullIfNotExists();
        if (subscription != null) {
          
          buildingMappedPointCaps.put(
              building.getName(), 
              subscription.getParentPaymentPlan().getPointCap());
          
        } else {
          
          buildingMappedPointCaps.put(
              building.getName(), 
              maxPointCap);
        }
      } else {

        // RP-10818: If we are dealing with an out of band building that belongs to an online customer, via online 
        // distributor that has "allowOutOfBandBuildings=true", then set the effective max point cap to be MAX_INTEGER.
        buildingMappedPointCaps.put(
            building.getName(), 
            Integer.MAX_VALUE);
      }
    }
    
    // For each raw point that we are to map, here identified only by its metricId, we see what
    // nodes it maps to, and increment the value for the pendingMappedPointCount.  If the sum
    // of currentMappedPointCount and pendingMappedPointCount is under the mappedPointCap for the
    // building that it maps to, then we add that metricId to the filtered list that we return
    // (so all the others for that building are rejected and not added to the return list)
    Map<RawPointEntity,List<Node>> map = Maps.newHashMapWithExpectedSize(rawPoints.size());
    for (Map.Entry<RawPointEntity, List<Node>> entry: rawPoints.entrySet()) {

      RawPointEntity rawPoint = entry.getKey();
      List<Node> nodes = entry.getValue();
      
      Node buildingNode = nodes.get(0);
      String buildingName = buildingNode.getName();
      
      Integer currentMappedPointCount = buildingCurrentMappedPointCounts.get(buildingName);
      Integer pendingMappedPointCount = buildingPendingMappedPointCounts.get(buildingName);
      Integer buildingPointCap = buildingMappedPointCaps.get(buildingName);
      if (currentMappedPointCount == null || pendingMappedPointCount == null || buildingPointCap == null) {
        
        newBuildingNames.add(buildingName);
        
        currentMappedPointCount = Integer.valueOf(0);
        pendingMappedPointCount = Integer.valueOf(0);
        buildingPointCap = maxPointCap;
        
        buildingCurrentMappedPointCounts.put(
            buildingName, 
            currentMappedPointCount);

        buildingPendingMappedPointCounts.put(
            buildingName, 
            pendingMappedPointCount);
        
        buildingMappedPointCaps.put(
            buildingName, 
            buildingPointCap);
      }
                              
      // See if we are eligible (by being at or below the point cap with the addition of the point)
      int count = currentMappedPointCount.intValue() + pendingMappedPointCount.intValue();
      if ((count + 1) <= buildingPointCap.intValue()) {
        
        pendingMappedPointCount = Integer.valueOf(pendingMappedPointCount.intValue() + 1);
        
        buildingPendingMappedPointCounts.put(
            buildingName, 
            pendingMappedPointCount);
        
        map.put(rawPoint, nodes);
      }
    }
    return map;
  }
  
  /**
   * USE CASES: (ONLINE CUSTOMERS ONLY)
   * 
   * Node move that results in one or more mappable points being 
   * moved to a new (billable) parent building.
   * 
   * ONE: If the new parent building is in the PENDING_ACTIVATION state, 
   * then the point cap for that building is that of the payment plan with 
   * the highest point cap. 
   * 
   * TWO: If the  new parent building is in the ACTIVE stae, then the point
   * cap for that building is that of the associated building subscription's
   * payment plan point cap.
   * 
   * The new parent building will have a current mapped point count.  For each
   * child node in the request, if the sum of the building current mapped
   * point count and the mapped point count for the child node is equal to,
   * or less than, the building point cap, then the move is allowed.  This 
   * child node is added to the list that is returned.  Otherwise, if the 
   * new point count would be greater than the building point cap, then this
   * node is "rejected" and not added to the return list.
   * 
   * NOTE: For each child to be moved to the new parent node, a number of 
   * checks/changes may be made:
   * 
   * @param portfolio The online customer's portfolio to perform
   * the node move against
   * @param newParentNode The node that is to be the new parent node
   * @param childNodes The list of potential new children for the given new parent node
   * 
   * @return A filtered list of new child nodes that would keep the mapped
   * point count under the point caps for the parent building that is involved with the 
   * node move.
   */  
  public static List<AbstractNodeEntity> processForBuildingPointCaps(
      PortfolioEntity portfolio,
      AbstractNodeEntity newParentNode,
      List<AbstractNodeEntity> childNodes) {
    
    // If the new parent building does not have a subscription yet, then 
    // the point cap is that of the greatest payment plan.
    PaymentPlansContainer paymentPlansContainer = DictionaryContext.getPaymentPlansContainer();
    int maxPointCap = paymentPlansContainer.getMaxPointCap();
    if (paymentPlansContainer == null || maxPointCap < 0) {
      throw new IllegalStateException("paymentPlansContainer has not been initialized: "
          + paymentPlansContainer);
    }
    
    // Verify that the customer is online.
    AbstractCustomerEntity customer = portfolio.getParentCustomer();
    if (!(customer instanceof OnlineCustomerEntity)) {
      throw new IllegalStateException("Customer: ["
          + customer
          + "] is not an instance of OnlineCustomerEntity");
    }
    
    // Verify that the new parent node is at the building level or below.
    if (newParentNode instanceof PortfolioEntity) {
      throw new IllegalStateException("Child nodes cannot be moved to the portfolio node: ["
          + portfolio
          + "]. They can only be moved from anywhere in one building to another.");
    }
    
    // Get the building that is associated with the new parent node
    // (which could be the building itself).
    BuildingEntity b = newParentNode.getAncestorBuilding();
    
    // Verify that the building is billable (this would only occur with a programming error).
    if (!(b instanceof BillableBuildingEntity)) {
      throw new IllegalStateException("Parent building for the node move: ["
          + b
          + "] is not an instance of BillableBuildingEntity.  This method should not be called.");
    }
    BillableBuildingEntity newParentBuilding = (BillableBuildingEntity)b;    
    
    // Get the current mappable point count associated with the new parent building.
    int currentPointCount = newParentBuilding.getTotalMappedPointCount();
    
    // Get the point cap associated with the new parent building.
    int pointCap = 0;
    BuildingSubscriptionEntity subscription = newParentBuilding.getChildBuildingSubscriptionNullIfNotExists();
    if (subscription != null) {
      pointCap = subscription.getParentPaymentPlan().getPointCap(); 
    } else {
      pointCap = maxPointCap;
    }
    
    // Keep track of the new point count as we process each child node.
    int pendingPointCount = currentPointCount;
    
    // For each child node that we are to move, we need to get a count of mappable point nodes.  
    // The sum total of all mapped points to be moved needs to be under the building associated with
    // the new parent node.  Note, that we increment the point count for each child that would "fit"
    // under the building point cap.
    List<AbstractNodeEntity> eligibleChildNodes = new ArrayList<>();
    for (AbstractNodeEntity childNode: childNodes) {
      
      // RP-9265: If the new ancestor building is the same as the old ancestor building, then there's  
      // no need to perform any point limiting, as everything will still be in the one building.
      if (!childNode.getAncestorBuilding().equals(newParentBuilding)) {
        
        // Get the child subtree mapped point count.
        int childPointCount = childNode.getTotalMappedPointCount();
        
        // If the pending count plus the child count is still at or under the point cap, then eligible. 
        int count = pendingPointCount + childPointCount;
        if (count <= pointCap) {

          // Increment the running pending point count.
          pendingPointCount = pendingPointCount + childPointCount;
          
          // Add the child node to the list of eligible nodes.
          eligibleChildNodes.add(childNode);
        }      
      } else {
        eligibleChildNodes.add(childNode);
      }
    }
    return eligibleChildNodes;
  }
  
  private BillableBuildingPointLimiter() {}
}
//@formatter:on
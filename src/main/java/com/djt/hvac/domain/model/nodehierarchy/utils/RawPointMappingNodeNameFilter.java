//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.dsl.pointmap.Node;
import com.djt.hvac.domain.model.common.dsl.pointmap.NodeType;
import com.djt.hvac.domain.model.common.dsl.pointmap.PointMapExpression;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.mapped.MappablePointEntity;
import com.djt.hvac.domain.model.nodehierarchy.service.command.MapRawPointsRequest;
import com.djt.hvac.domain.model.rawpoint.RawPointEntity;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * 
 * Determines if a set of "exons", or candidates for point mapping, 
 * are valid, according to the given name filters. 
 * 
 * @author tmyers
 *
 */
public abstract class RawPointMappingNodeNameFilter {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(RawPointMappingNodeNameFilter.class);

  /**
   * 
   * @param portfolio The parent portfolio
   * @param rawPoints The raw points to perform node name matching on
   * @param request The request
   * 
   * @return A map containing matching raw points as the key and the corresponding nodes to be mapped as the value
   */
  public static Map<RawPointEntity,List<Node>> getEligibleRawPoints(
      PortfolioEntity portfolio,
      Collection<RawPointEntity> rawPoints,
      MapRawPointsRequest request) {
    
    Map<RawPointEntity,List<Node>> map = Maps.newHashMapWithExpectedSize(rawPoints.size());

    String buildingNameFilter = request.getBuildingName();
    String mappingExpression = request.getMappingExpression();
    String metricIdDelimiter = request.getMetricIdDelimiter();
    Boolean performExclusionOnNames = request.getPerformExclusionOnNames();

    Set<String> subBuildingNameFilters = populateFilter(request.getSubBuildingNames());
    Set<String> plantNameFilters = populateFilter(request.getPlantNames());
    Set<String> floorNameFilters = populateFilter(request.getFloorNames());
    Set<String> equipmentNameFilters = populateFilter(request.getEquipmentNames());
    Set<String> pointNameFilters = populateFilter(request.getPointNames());

    PointMapExpression pointMapExpression = PointMapExpression.parse(mappingExpression);
    for (RawPointEntity rawPoint: rawPoints) {
      
      String metricId = rawPoint.getMetricId();
      
      // Ensure that the raw point has NOT been mapped already.
      MappablePointEntity check = portfolio.getChildMappablePointByRawPointMetricIdNullIfNotExists(metricId);
      if (check == null) {

        Optional<List<Node>> optionalNodes = pointMapExpression.match(metricId, metricIdDelimiter);
        if (optionalNodes.isPresent()) {

          boolean isMatchOnNodeNameFilters = true;
          List<Node> nodes = optionalNodes.get();
          if (nodes.size() < 2) {
            
            throw new IllegalStateException("Expected at least 2 nodes for point mapping, but encountered only: " 
                + nodes 
                + " instead."); 

          } else {
            
            Node node = nodes.get(0);
            String nodeName = node.getDisplayName();
            NodeType nodeType = node.getType(); 
            if (!nodeType.equals(NodeType.BUILDING)) {
              
              throw new IllegalStateException("First node for point mapping: [" 
                  + nodeName 
                  + "] was expected to be of type BUILDING, but instead was: [" 
                  + nodeType 
                  + "]"); 
            }
            
            if (buildingNameFilter != null && !nodeName.equalsIgnoreCase(buildingNameFilter)) {
              
              isMatchOnNodeNameFilters = false;
              if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Building node: [{}] did not match building name filter: [{}]",
                    nodeName,
                    buildingNameFilter);
              }
            } else {
              
              int size = nodes.size();
              for (int i=1; i < size && isMatchOnNodeNameFilters; i++) {
                
                node = nodes.get(i);
                nodeName = node.getDisplayName();
                nodeType = node.getType();
                
                if (nodeType.equals(NodeType.SUB_BUILDING)) {
                  
                  isMatchOnNodeNameFilters = RawPointMappingNodeNameFilter.isMatchOnNodeNameFilter(
                      nodeName,
                      nodeType,
                      subBuildingNameFilters, 
                      performExclusionOnNames);
                  
                } else if (nodeType.equals(NodeType.PLANT)) {
                  
                  isMatchOnNodeNameFilters = RawPointMappingNodeNameFilter.isMatchOnNodeNameFilter(
                      nodeName,
                      nodeType,
                      plantNameFilters, 
                      performExclusionOnNames);
                  
                } else if (nodeType.equals(NodeType.FLOOR)) {
                  
                  isMatchOnNodeNameFilters = RawPointMappingNodeNameFilter.isMatchOnNodeNameFilter(
                      nodeName,
                      nodeType,
                      floorNameFilters, 
                      performExclusionOnNames);
                  
                } else if (nodeType.equals(NodeType.EQUIPMENT)) {
                  
                  isMatchOnNodeNameFilters = RawPointMappingNodeNameFilter.isMatchOnNodeNameFilter(
                      nodeName,
                      nodeType,
                      equipmentNameFilters, 
                      performExclusionOnNames);
                  
                } else if (nodeType.equals(NodeType.POINT)) {
                  
                  isMatchOnNodeNameFilters = RawPointMappingNodeNameFilter.isMatchOnNodeNameFilter(
                      nodeName,
                      nodeType,
                      pointNameFilters, 
                      performExclusionOnNames);
                  
                }
              }
            }
          }          
          if (isMatchOnNodeNameFilters) {
            map.put(rawPoint, nodes);
          }
        }
      }
    }
    portfolio.resetMappablePointsByRawPointMetricIdMap();
    return map;
  }
  
  private static Set<String> populateFilter(List<String> filter) {
    
    Set<String> set = null;
    if (filter != null && !filter.isEmpty()) {
      
      set = Sets.newHashSet();
      set.addAll(filter);
    }
    return set;
  }
  
  private static boolean isMatchOnNodeNameFilter(
      String nodeName, 
      NodeType nodeType,
      Set<String> nodeNameFilters, 
      Boolean performExclusionOnNames) {
    
    boolean isMatchOnNodeNameFilter = true;
    if (nodeNameFilters != null && !nodeNameFilters.isEmpty()) {
      
      boolean isInList = false;
      if (nodeNameFilters.contains(nodeName)) { 
        isInList = true;
      }
      
      if ((performExclusionOnNames && isInList) || (!performExclusionOnNames && !isInList)) {
        isMatchOnNodeNameFilter = false; 
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("Node: [{}] of type: [{}] did not node name filters: {} with performExclusionOnNames: [{}]",
              nodeName,
              nodeType,
              nodeNameFilters,
              performExclusionOnNames);
        }
      }
    }
    return isMatchOnNodeNameFilter;
  }  
  
  private RawPointMappingNodeNameFilter() {}
}
//@formatter:on
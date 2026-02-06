package com.djt.hvac.domain.model.dictionary.template.currentobjectexpression.crawler.nodehierarchy;

import java.util.List;
import java.util.Optional;

import com.djt.hvac.domain.model.common.dsl.currentobject.NodeType;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.dictionary.template.currentobjectexpression.crawler.StandardPortfolioCrawler;
import com.djt.hvac.domain.model.nodehierarchy.AbstractNodeEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.google.common.collect.Lists;

public class NodeHierarchyParentPortfolioCrawler extends StandardPortfolioCrawler {

  NodeHierarchyParentPortfolioCrawler(
      Optional<NodeType> nodeType, 
      List<String> tags,
      boolean wildcardTag) {
    super(
        nodeType,
        tags,
        wildcardTag);
  }

  @Override
  public List<Integer> getCurrentObjects(
      PortfolioEntity portfolio,
      List<Integer> startingNodeIds) {
    
    try {
      
      List<Integer> list = Lists.newArrayList();
      for (Integer startingNodeId: startingNodeIds) {

        AbstractNodeEntity startingNode = portfolio.getChildNode(startingNodeId);
        AbstractNodeEntity targetNode = startingNode.getParentNode();

        if (matches(targetNode)) {
          
          list.add(targetNode.getPersistentIdentity());
        }
      }
      return list;
      
    } catch (EntityDoesNotExistException ednee) {
      throw new IllegalStateException(ednee.getMessage(), ednee);
    }
  }
}

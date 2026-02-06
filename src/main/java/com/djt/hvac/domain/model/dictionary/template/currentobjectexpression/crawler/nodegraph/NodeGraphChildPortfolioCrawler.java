package com.djt.hvac.domain.model.dictionary.template.currentobjectexpression.crawler.nodegraph;

import java.util.List;
import java.util.Optional;

import com.djt.hvac.domain.model.common.dsl.currentobject.Model;
import com.djt.hvac.domain.model.common.dsl.currentobject.NodeType;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.dictionary.energyexchange.enums.EnergyExchangeSystemType;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.EnergyExchangeEntity;
import com.google.common.collect.Lists;

public class NodeGraphChildPortfolioCrawler extends NodeGraphPortfolioCrawler {

  public NodeGraphChildPortfolioCrawler(
      Model model,
      Optional<NodeType> nodeType,
      List<String> tags,
      boolean wildcardTag) {
    super(
        model,
        nodeType,
        tags,
        wildcardTag);
  }

  @Override
  public List<Integer> getCurrentObjects(
      PortfolioEntity portfolio,
      List<Integer> startingNodeIds) {
    
    try {
      
      EnergyExchangeSystemType energyExchangeSystemType = EnergyExchangeSystemType.get(this.model.getId());

      List<Integer> list = Lists.newArrayList();
      for (Integer startingNodeId: startingNodeIds) {

        EnergyExchangeEntity startingNode = portfolio.getEnergyExchangeSystemNode(startingNodeId);
        
        for (EnergyExchangeEntity targetNode: startingNode.getChildEnergyExchangeSystemNodes(energyExchangeSystemType)) {

          if (matches(targetNode)) {
            
            list.add(targetNode.getPersistentIdentity());
          }
        }
      }
      return list;
      
    } catch (EntityDoesNotExistException ednee) {
      throw new IllegalStateException(ednee.getMessage(), ednee);
    }
  }
}

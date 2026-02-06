package com.djt.hvac.domain.model.dictionary.template.currentobjectexpression.crawler.nodegraph;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;

import com.djt.hvac.domain.model.common.dsl.currentobject.Model;
import com.djt.hvac.domain.model.common.dsl.currentobject.NodeType;
import com.djt.hvac.domain.model.dictionary.template.currentobjectexpression.crawler.StandardPortfolioCrawler;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.EnergyExchangeEntity;

public abstract class NodeGraphPortfolioCrawler extends StandardPortfolioCrawler {
  protected final Model model;

  protected NodeGraphPortfolioCrawler(
      Model model, 
      Optional<NodeType> nodeType,
      List<String> tags,
      boolean wildcardTag) {
    super(
        nodeType,
        tags,
        wildcardTag);
    this.model = requireNonNull(model, "model cannot be null");
  }
  
  protected boolean matches(EnergyExchangeEntity targetNode) {
    
    boolean matches = true;
    
    if (wildcardTag) {
      
      if ((this.nodeType.isPresent() && this.nodeType.get().getId() != targetNode.getNodeType().getId())
          || (!this.tags.isEmpty() && !targetNode.getNodeTagNames().containsAll(this.tags))) {
        
        matches = false;
      }
      
    } else {

      if ((this.nodeType.isPresent() && this.nodeType.get().getId() != targetNode.getNodeType().getId())
          || (!this.tags.isEmpty() && !this.tags.equals(targetNode.getNodeTagNamesAsSortedList()))) {
        
        matches = false;
      }
      
    }
    
    return matches;
  }

  // Test Methods

  public Model getModel() {
    return model;
  }

}

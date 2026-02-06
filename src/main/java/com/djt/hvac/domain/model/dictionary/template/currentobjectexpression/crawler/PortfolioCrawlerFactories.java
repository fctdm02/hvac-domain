package com.djt.hvac.domain.model.dictionary.template.currentobjectexpression.crawler;

import static java.util.Objects.requireNonNull;

import com.djt.hvac.domain.model.common.dsl.currentobject.Model;
import com.djt.hvac.domain.model.common.dsl.currentobject.StandardFunctionCall;
import com.djt.hvac.domain.model.dictionary.template.currentobjectexpression.crawler.equipmenthierarchy.EquipmentHierarchyPortfolioCrawlerFactory;
import com.djt.hvac.domain.model.dictionary.template.currentobjectexpression.crawler.nodegraph.NodeGraphPortfolioCrawlerFactory;
import com.djt.hvac.domain.model.dictionary.template.currentobjectexpression.crawler.nodehierarchy.NodeHierarchyPortfolioCrawlerFactory;

class PortfolioCrawlerFactories {

  private static final PortfolioCrawlerFactory EQUIPMENT_HIERARCHY_FACTORY = new EquipmentHierarchyPortfolioCrawlerFactory();
  
  private static final PortfolioCrawlerFactory NODE_HIERARCHY_FACTORY = new NodeHierarchyPortfolioCrawlerFactory();
  
  private static final PortfolioCrawlerFactory NODE_GRAPH_FACTORY = new NodeGraphPortfolioCrawlerFactory();

  static PortfolioCrawlerFactory get(StandardFunctionCall call) {
    
    requireNonNull(call, "call cannot be null");
    if (call.getFunction().isDeprecated() || call.getModel() == Model.AIR_SUPPLY) {
      return EQUIPMENT_HIERARCHY_FACTORY;
      
    } else if (call.getModel() == Model.STANDARD) {
      return NODE_HIERARCHY_FACTORY;
      
    } else {
      
      return NODE_GRAPH_FACTORY;
    }
  }

  private PortfolioCrawlerFactories() {}
}

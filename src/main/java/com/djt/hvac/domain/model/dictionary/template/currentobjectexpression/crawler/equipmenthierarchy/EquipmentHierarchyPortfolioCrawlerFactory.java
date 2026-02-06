package com.djt.hvac.domain.model.dictionary.template.currentobjectexpression.crawler.equipmenthierarchy;

import static com.djt.hvac.domain.model.common.dsl.currentobject.Model.AIR_SUPPLY;

import com.djt.hvac.domain.model.common.dsl.currentobject.StandardFunctionCall;
import com.djt.hvac.domain.model.dictionary.template.currentobjectexpression.crawler.PortfolioCrawler;
import com.djt.hvac.domain.model.dictionary.template.currentobjectexpression.crawler.PortfolioCrawlerFactory;
import com.djt.hvac.domain.model.dictionary.template.currentobjectexpression.crawler.nodegraph.NodeGraphAncestorPortfolioCrawler;
import com.djt.hvac.domain.model.dictionary.template.currentobjectexpression.crawler.nodegraph.NodeGraphChildPortfolioCrawler;
import com.djt.hvac.domain.model.dictionary.template.currentobjectexpression.crawler.nodegraph.NodeGraphDescendantPortfolioCrawler;
import com.djt.hvac.domain.model.dictionary.template.currentobjectexpression.crawler.nodegraph.NodeGraphParentPortfolioCrawler;

public class EquipmentHierarchyPortfolioCrawlerFactory implements PortfolioCrawlerFactory {

  @Override
  public PortfolioCrawler getParentFunctionPortfolioCrawlerGenerator(StandardFunctionCall call) {
    return new NodeGraphParentPortfolioCrawler(
        AIR_SUPPLY, 
        call.getType(),
        call.getTags(),
        call.getWildcardTag());
  }

  @Override
  public PortfolioCrawler getChildFunctionPortfolioCrawlerGenerator(StandardFunctionCall call) {
    return new NodeGraphChildPortfolioCrawler(
        AIR_SUPPLY, 
        call.getType(),
        call.getTags(),
        call.getWildcardTag());
  }

  @Override
  public PortfolioCrawler getAncestorFunctionPortfolioCrawlerGenerator(StandardFunctionCall call) {
    return new NodeGraphAncestorPortfolioCrawler(
        AIR_SUPPLY, 
        call.getType(),
        call.getTags(),
        call.getWildcardTag());
  }

  @Override
  public PortfolioCrawler getDescendantFunctionPortfolioCrawlerGenerator(StandardFunctionCall call) {
    return new NodeGraphDescendantPortfolioCrawler(
        AIR_SUPPLY, 
        call.getType(),
        call.getTags(),
        call.getWildcardTag());
  }
}

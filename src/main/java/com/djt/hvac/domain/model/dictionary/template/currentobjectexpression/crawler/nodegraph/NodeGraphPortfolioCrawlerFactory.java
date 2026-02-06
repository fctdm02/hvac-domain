package com.djt.hvac.domain.model.dictionary.template.currentobjectexpression.crawler.nodegraph;

import com.djt.hvac.domain.model.common.dsl.currentobject.StandardFunctionCall;
import com.djt.hvac.domain.model.dictionary.template.currentobjectexpression.crawler.PortfolioCrawler;
import com.djt.hvac.domain.model.dictionary.template.currentobjectexpression.crawler.PortfolioCrawlerFactory;

public class NodeGraphPortfolioCrawlerFactory implements PortfolioCrawlerFactory {

  @Override
  public PortfolioCrawler getParentFunctionPortfolioCrawlerGenerator(StandardFunctionCall call) {
    return new NodeGraphParentPortfolioCrawler(
        call.getModel(), 
        call.getType(),
        call.getTags(),
        call.getWildcardTag());
  }

  @Override
  public PortfolioCrawler getChildFunctionPortfolioCrawlerGenerator(StandardFunctionCall call) {
    return new NodeGraphChildPortfolioCrawler(
        call.getModel(), 
        call.getType(),
        call.getTags(),
        call.getWildcardTag());
  }

  @Override
  public PortfolioCrawler getAncestorFunctionPortfolioCrawlerGenerator(StandardFunctionCall call) {
    return new NodeGraphAncestorPortfolioCrawler(
        call.getModel(), 
        call.getType(),
        call.getTags(),
        call.getWildcardTag());
  }

  @Override
  public PortfolioCrawler getDescendantFunctionPortfolioCrawlerGenerator(StandardFunctionCall call) {
    return new NodeGraphDescendantPortfolioCrawler(
        call.getModel(), 
        call.getType(),
        call.getTags(),
        call.getWildcardTag());
  }
}

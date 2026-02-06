package com.djt.hvac.domain.model.dictionary.template.currentobjectexpression.crawler.nodehierarchy;

import com.djt.hvac.domain.model.common.dsl.currentobject.StandardFunctionCall;
import com.djt.hvac.domain.model.dictionary.template.currentobjectexpression.crawler.PortfolioCrawler;
import com.djt.hvac.domain.model.dictionary.template.currentobjectexpression.crawler.PortfolioCrawlerFactory;

public class NodeHierarchyPortfolioCrawlerFactory implements PortfolioCrawlerFactory {

  @Override
  public PortfolioCrawler getParentFunctionPortfolioCrawlerGenerator(StandardFunctionCall call) {
    return new NodeHierarchyParentPortfolioCrawler(
        call.getType(), 
        call.getTags(),
        call.getWildcardTag());
  }

  @Override
  public PortfolioCrawler getChildFunctionPortfolioCrawlerGenerator(StandardFunctionCall call) {
    return new NodeHierarchyChildPortfolioCrawler(
        call.getType(), 
        call.getTags(),
        call.getWildcardTag());
  }

  @Override
  public PortfolioCrawler getAncestorFunctionPortfolioCrawlerGenerator(StandardFunctionCall call) {
    return new NodeHierarchyAncestorPortfolioCrawler(
        call.getType(), 
        call.getTags(),
        call.getWildcardTag());
  }

  @Override
  public PortfolioCrawler getDescendantFunctionPortfolioCrawlerGenerator(StandardFunctionCall call) {
    return new NodeHierarchyDescendantPortfolioCrawler(
        call.getType(), 
        call.getTags(),
        call.getWildcardTag());
  }
}

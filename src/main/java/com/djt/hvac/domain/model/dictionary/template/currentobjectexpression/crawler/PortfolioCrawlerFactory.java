package com.djt.hvac.domain.model.dictionary.template.currentobjectexpression.crawler;

import com.djt.hvac.domain.model.common.dsl.currentobject.StandardFunctionCall;

public interface PortfolioCrawlerFactory {

  public PortfolioCrawler getParentFunctionPortfolioCrawlerGenerator(StandardFunctionCall call);

  public PortfolioCrawler getChildFunctionPortfolioCrawlerGenerator(StandardFunctionCall call);

  public PortfolioCrawler getAncestorFunctionPortfolioCrawlerGenerator(StandardFunctionCall call);

  public PortfolioCrawler getDescendantFunctionPortfolioCrawlerGenerator(StandardFunctionCall call);
}

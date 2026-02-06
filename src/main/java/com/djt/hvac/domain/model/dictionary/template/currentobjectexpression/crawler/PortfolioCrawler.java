package com.djt.hvac.domain.model.dictionary.template.currentobjectexpression.crawler;

import java.util.List;

import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;

public interface PortfolioCrawler {
  public List<Integer> getCurrentObjects(PortfolioEntity portfolio, List<Integer> startingNodeIds);
}

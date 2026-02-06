package com.djt.hvac.domain.model.dictionary.template.currentobjectexpression.crawler;

import static java.util.Objects.requireNonNull;

import java.util.List;

import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;

class ElseIfPortfolioCrawler implements PortfolioCrawler {

  private final CompositePortfolioCrawler portfolioCrawler;

  ElseIfPortfolioCrawler(CompositePortfolioCrawler portfolioCrawler) {
    this.portfolioCrawler = requireNonNull(portfolioCrawler, "portfolioCrawler cannot be null");
  }

  @Override
  public List<Integer> getCurrentObjects(
      PortfolioEntity portfolio, 
      List<Integer> currentNodeIds) {
    
    return getCurrentObjects(
        portfolio,
        currentNodeIds, 
        currentNodeIds);
  }

  public List<Integer> getCurrentObjects(
      PortfolioEntity portfolio,
      List<Integer> currentNodeIds,
      List<Integer> priorNodeIds) {
    
    requireNonNull(portfolio, "portfolio cannot be null");
    requireNonNull(currentNodeIds, "currentNodeIds cannot be null");
    requireNonNull(priorNodeIds, "priorNodeIds cannot be null");
    if (currentNodeIds.isEmpty() && priorNodeIds.isEmpty()) {
      return priorNodeIds;
    }
    if (currentNodeIds.isEmpty()) {
      return portfolioCrawler.getCurrentObjects(portfolio, priorNodeIds);
    }
    return currentNodeIds;
  }


  // Test Methods

  CompositePortfolioCrawler getPortfolioCrawlerGenerators() {
    return portfolioCrawler;
  }

}

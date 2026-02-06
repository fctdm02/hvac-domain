package com.djt.hvac.domain.model.dictionary.template.currentobjectexpression.crawler;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.List;

import com.djt.hvac.domain.model.common.dsl.currentobject.CompositeFunctionCall;
import com.djt.hvac.domain.model.common.dsl.currentobject.CurrentObjectExpressionParser;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;

public class CurrentObjectExpressionPortfolioCrawler {

  private final PortfolioCrawler portfolioCrawler;

  public static CurrentObjectExpressionPortfolioCrawler create(
      String expression) {
    requireNonNull(expression, "expression cannot be null");
    CompositeFunctionCall calls = CurrentObjectExpressionParser.parse(expression);
    PortfolioCrawler portfolioCrawler = toPortfolioCrawlerGenerator(calls);
    return new CurrentObjectExpressionPortfolioCrawler(portfolioCrawler);
  }

  private static PortfolioCrawler toPortfolioCrawlerGenerator(
      CompositeFunctionCall calls) {
    
    PortfolioCrawlerFunctionCallVisitor visitor = new PortfolioCrawlerFunctionCallVisitor();
    calls.accept(visitor);
    return visitor.getResult();
  }

  public CurrentObjectExpressionPortfolioCrawler(PortfolioCrawler portfolioCrawler) {
    this.portfolioCrawler = portfolioCrawler;
  }

  public List<Integer> getCurrentObjects(
      PortfolioEntity portfolio,
      int thisId) {
    
    return portfolioCrawler.getCurrentObjects(portfolio, Arrays.asList(thisId));
  }

  public List<Integer> getCurrentObjects(
      PortfolioEntity portfolio,
      List<Integer> ids) {
    
    requireNonNull(portfolio, "portfolio cannot be null");
    requireNonNull(ids, "ids cannot be null");
    if (ids.isEmpty()) {
      return ids;
    }
    return portfolioCrawler.getCurrentObjects(portfolio, ids);
  }

  // this method used for testing only
  PortfolioCrawler getPortfolioCrawlerGenerator() {
    return portfolioCrawler;
  }
}

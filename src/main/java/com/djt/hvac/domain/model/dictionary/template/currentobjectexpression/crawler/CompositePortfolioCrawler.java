package com.djt.hvac.domain.model.dictionary.template.currentobjectexpression.crawler;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.List;

import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

class CompositePortfolioCrawler implements PortfolioCrawler {

  private final List<PortfolioCrawler> portfolioCrawlers;

  static Builder builder() {
    return new Builder();
  }

  private CompositePortfolioCrawler(Builder builder) {
    this.portfolioCrawlers = ImmutableList.copyOf(builder.portfolioCrawlers);
  }

  @Override
  public List<Integer> getCurrentObjects(
      PortfolioEntity portfolio, 
      List<Integer> startingNodeIds) {
    
    requireNonNull(portfolio, "portfolio cannot be null");
    requireNonNull(startingNodeIds, "startingNodeIds cannot be null");
    List<Integer> currentNodeIds = startingNodeIds;
    List<Integer> priorNodeIds = currentNodeIds;
    for (int i = 0; i < portfolioCrawlers.size(); i++) {
      if (ElseIfPortfolioCrawler.class.isInstance(portfolioCrawlers.get(i))) {
        if (i == 0) {
          throw new IllegalArgumentException("Expected a standard portfolio crawler");
        }
        ElseIfPortfolioCrawler elseIfPortfolioCrawler = ElseIfPortfolioCrawler.class.cast(portfolioCrawlers.get(i));
        currentNodeIds = elseIfPortfolioCrawler.getCurrentObjects(portfolio, currentNodeIds, priorNodeIds);
      } else {
        priorNodeIds = currentNodeIds;
        currentNodeIds = portfolioCrawlers.get(i).getCurrentObjects(portfolio, currentNodeIds);
      }
    }
    return currentNodeIds;
  }

  // Test Methods

  List<PortfolioCrawler> getPortfolioCrawlerGenerators() {
    return portfolioCrawlers;
  }

  static class Builder {
    private List<PortfolioCrawler> portfolioCrawlers = Lists.newArrayList();

    private Builder() {}

    Builder withPortfolioCrawlerGenerator(PortfolioCrawler portfolioCrawler) {
      requireNonNull(portfolioCrawler, "portfolioCrawler cannot be null");
      this.portfolioCrawlers.add(portfolioCrawler);
      return this;
    }

    CompositePortfolioCrawler build() {
      checkArgument(!portfolioCrawlers.isEmpty(), "expected at least one portfolioCrawler");
      return new CompositePortfolioCrawler(this);
    }
  }
}

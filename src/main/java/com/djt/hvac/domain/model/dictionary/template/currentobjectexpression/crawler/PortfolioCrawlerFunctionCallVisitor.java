package com.djt.hvac.domain.model.dictionary.template.currentobjectexpression.crawler;

import static java.util.Objects.requireNonNull;

import com.djt.hvac.domain.model.common.dsl.currentobject.ElseIfFunctionCall;
import com.djt.hvac.domain.model.common.dsl.currentobject.FunctionCallVisitor;
import com.djt.hvac.domain.model.common.dsl.currentobject.StandardFunctionCall;

class PortfolioCrawlerFunctionCallVisitor implements FunctionCallVisitor {

  private final CompositePortfolioCrawler.Builder builder = CompositePortfolioCrawler.builder();

  PortfolioCrawlerFunctionCallVisitor() {
  }

  @Override
  public void visit(StandardFunctionCall call) {
    
    requireNonNull(call, "call cannot be null");
    PortfolioCrawlerFactory factory = PortfolioCrawlerFactories.get(call);
    PortfolioCrawler portfolioCrawler;
    switch (call.getFunction()) {
      case PARENT:
      case PARENT_EQUIPMENT:
        portfolioCrawler = factory.getParentFunctionPortfolioCrawlerGenerator(call);
        break;
      case ANCESTOR:
      case ANCESTOR_EQUIPMENT:
        portfolioCrawler = factory.getAncestorFunctionPortfolioCrawlerGenerator(call);
        break;
      case CHILD:
      case CHILD_EQUIPMENT:
        portfolioCrawler = factory.getChildFunctionPortfolioCrawlerGenerator(call);
        break;
      case DESCENDANT:
      case DESCENDANT_EQUIPMENT:
        portfolioCrawler = factory.getDescendantFunctionPortfolioCrawlerGenerator(call);
        break;
      default:
        throw new AssertionError("Unexpected function call: " + call.getFunction());
    }
    builder.withPortfolioCrawlerGenerator(portfolioCrawler);
  }

  @Override
  public void visit(ElseIfFunctionCall call) {
    
    requireNonNull(call, "call cannot be null");
    PortfolioCrawlerFunctionCallVisitor visitor = new PortfolioCrawlerFunctionCallVisitor();
    call.getCalls().accept(visitor);
    CompositePortfolioCrawler portfolioCrawlerGenerators = visitor.getResult();
    ElseIfPortfolioCrawler portfolioCrawlerGenerator = new ElseIfPortfolioCrawler(portfolioCrawlerGenerators);
    builder.withPortfolioCrawlerGenerator(portfolioCrawlerGenerator);
  }

  CompositePortfolioCrawler getResult() {
    return builder.build();
  }
}

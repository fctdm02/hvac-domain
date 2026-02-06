package com.djt.hvac.domain.model.common.dsl.pointmap;

@FunctionalInterface
interface PointMapExpressionVisitor {

  public void visit(Expression expression);

}

package com.djt.hvac.domain.model.common.dsl.pointmap;

interface CompositeExpressionBuilder {

  CompositeExpressionBuilder add(Expression expression);

  Expression build();

}

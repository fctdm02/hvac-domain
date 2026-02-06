package com.djt.hvac.domain.model.common.dsl.currentobject;

public interface FunctionCallVisitor {

  public void visit(StandardFunctionCall call);

  public void visit(ElseIfFunctionCall call);

}

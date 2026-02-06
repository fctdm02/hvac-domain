package com.djt.hvac.domain.model.common.dsl.currentobject;

import static java.util.Objects.requireNonNull;

public class ElseIfFunctionCall implements FunctionCall {
  private final CompositeFunctionCall calls;

  static Builder builder() {
    return new Builder();
  }

  private ElseIfFunctionCall(Builder builder) {
    this.calls = builder.calls;
  }

  @Override
  public void accept(FunctionCallVisitor visitor) {
    requireNonNull(visitor, "visitor cannot be null");
    visitor.visit(this);
  }

  public CompositeFunctionCall getCalls() {
    return calls;
  }

  static class Builder {
    private CompositeFunctionCall calls;

    private Builder() {}

    Builder withCalls(CompositeFunctionCall calls) {
      requireNonNull(calls, "calls cannot be null");
      this.calls = calls;
      return this;
    }

    public ElseIfFunctionCall build() {
      requireNonNull(calls, "calls cannot be null");
      return new ElseIfFunctionCall(this);
    }
  }

}

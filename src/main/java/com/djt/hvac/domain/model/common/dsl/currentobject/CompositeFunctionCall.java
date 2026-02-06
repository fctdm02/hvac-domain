package com.djt.hvac.domain.model.common.dsl.currentobject;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class CompositeFunctionCall implements FunctionCall {
  private final List<FunctionCall> calls;

  static Builder builder() {
    return new Builder();
  }

  private CompositeFunctionCall(Builder builder) {
    this.calls = ImmutableList.copyOf(builder.calls);
  }

  @Override
  public void accept(FunctionCallVisitor visitor) {
    requireNonNull(visitor, "visitor cannot be null");
    calls.forEach(call -> call.accept(visitor));
  }

  public List<FunctionCall> getCalls() {
    return calls;
  }

  static class Builder {
    private final List<FunctionCall> calls = Lists.newArrayList();

    private Builder() {}

    Builder withCall(FunctionCall call) {
      requireNonNull(call, "call cannot be null");
      this.calls.add(call);
      return this;
    }

    CompositeFunctionCall build() {
      checkArgument(!calls.isEmpty(), "expected at least one function call");
      return new CompositeFunctionCall(this);
    }
  }



}

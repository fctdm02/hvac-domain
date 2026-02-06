package com.djt.hvac.domain.model.common.dsl.currentobject;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;

public class StandardFunctionCall implements FunctionCall {
  private final CurrentObjectFunction function;
  private final Model model;
  private final NodeType type;
  private final boolean wildcardTag;
  private final List<String> tags;

  static Builder builder(CurrentObjectFunction function) {
    return new Builder(function);
  }

  private StandardFunctionCall(Builder builder) {
    this.function = builder.function;
    this.model = builder.model;
    this.type = builder.type;
    this.wildcardTag = builder.wildcardTag;
    this.tags = builder.tags;
  }

  @Override
  public void accept(FunctionCallVisitor visitor) {
    requireNonNull(visitor, "visitor cannot be null");
    visitor.visit(this);
  }

  public CurrentObjectFunction getFunction() {
    return function;
  }

  public Model getModel() {
    return model;
  }

  public Optional<NodeType> getType() {
    return Optional.ofNullable(type);
  }

  public boolean getWildcardTag() {
    return wildcardTag;
  }

  public List<String> getTags() {
    return tags;
  }

  static class Builder {
    private final CurrentObjectFunction function;
    private Model model = Model.STANDARD;
    private NodeType type;
    private boolean wildcardTag = false;
    private List<String> tags = ImmutableList.of();

    private Builder(CurrentObjectFunction function) {
      this.function = requireNonNull(function, "function cannot be null");
    }

    Builder withModel(Model model) {
      this.model = requireNonNull(model, "model cannot be null");
      return this;
    }

    Builder withType(NodeType type) {
      this.type = requireNonNull(type, "type cannot be null");
      return this;
    }

    Builder withWildcardTag(boolean wildcardTag) {
      this.wildcardTag = wildcardTag;
      return this;
    }

    Builder withTags(List<String> tags) {
      requireNonNull(tags, "tags cannot be null");
      this.tags = ImmutableList.copyOf(tags);
      return this;
    }

    StandardFunctionCall build() {
      return new StandardFunctionCall(this);
    }
  }
}

package com.djt.hvac.domain.model.common.dsl.computedpoint.v2.function;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import static java.util.Objects.requireNonNull;

public class Arguments implements Iterable<Optional<Double>> {
  private static Arguments EMPTY = Arguments.builder().build();

  private final List<Optional<Double>> args;

  public static Arguments noArgs() {
    return EMPTY;
  }

  public static Builder builder() {
    return new Builder();
  }

  private Arguments(Builder builder) {
    this.args = ImmutableList.copyOf(builder.args);
  }

  @Override
  public Iterator<Optional<Double>> iterator() {
    return args.iterator();
  }

  public boolean isEmpty() {
    return args.isEmpty();
  }

  public int size() {
    return args.size();
  }

  public Optional<Double> get(int idx) {
    return args.get(idx);
  }

  public static class Builder {
    private List<Optional<Double>> args = Lists.newArrayList();

    private Builder() {}

    private Builder(Arguments args) {
      this.args = Lists.newArrayList(args.args);
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withArgument(Optional<Double> arg) {
      this.args.add(arg);
      return this;
    }

    public Arguments build() {
      return new Arguments(this);
    }
  }

}

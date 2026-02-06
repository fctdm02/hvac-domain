package com.djt.hvac.domain.model.common.dsl.computedpoint.v2.function;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.lang.annotation.Annotation;

import com.djt.hvac.domain.model.common.dsl.computedpoint.v2.IntervalAlignment;

public abstract class AbstractFunction implements Function {

  private final String name;
  private final boolean stateful;
  private final int minParams;
  private final int maxParams;
  private final IntervalAlignment intervalAlignment;

  protected AbstractFunction() {
    Annotation annotation = getClass().getDeclaredAnnotation(ComputedPointFunction.class);
    if (annotation == null) {
      throw new AssertionError("Expected the " + getClass().getName()
          + " class to have an annotation of type "
          + ComputedPointFunction.class.getName());
    }
    ComputedPointFunction typedAnnotation = ComputedPointFunction.class.cast(annotation);
    this.name = typedAnnotation.name();
    this.stateful = typedAnnotation.stateful();
    validateStateful(this.stateful);
    this.minParams = typedAnnotation.minParams();
    this.maxParams = typedAnnotation.maxParams();
    this.intervalAlignment = typedAnnotation.intervalAlignment();

    checkArgument(this.minParams >= 0,
        "expected the value of the minParams property to be greater than or equal to 0");
    checkArgument(this.minParams <= this.maxParams,
        "expected the value of the minParams property (" + this.minParams
            + ") to be less than or equal to the value of the maxParams property (" + this.maxParams
            + ")");
  }

  @Override
  public final String getName() {
    return name;
  }

  @Override
  public final boolean isStateful() {
    return stateful;
  }

  @Override
  public final int getMinParams() {
    return minParams;
  }

  @Override
  public final int getMaxParams() {
    return maxParams;
  }

  @Override
  public final IntervalAlignment getIntervalAlignment() {
    return intervalAlignment;
  }

  protected void validateArgs(Arguments args) {
    requireNonNull(args, "args cannot be null");
    checkArgument(args.size() >= minParams && args.size() <= maxParams,
        "expected " + getParamsMessage() + " argument(s), but received " + args.size());
  }

  protected abstract void validateStateful(boolean stateful);

  private String getParamsMessage() {
    if (this.minParams == this.maxParams) {
      return "" + this.minParams;
    }
    return "no fewer than " + this.minParams
        + (this.maxParams < Integer.MAX_VALUE ? " argument(s) and no more than " + this.maxParams
            : "");
  }


}

package com.djt.hvac.domain.model.common.dsl.computedpoint.v2.function;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.djt.hvac.domain.model.common.dsl.computedpoint.v2.IntervalAlignment;

@Target({TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ComputedPointFunction {
  String name();

  boolean stateful();

  int minParams();

  int maxParams();

  IntervalAlignment intervalAlignment() default IntervalAlignment.Floor;
}

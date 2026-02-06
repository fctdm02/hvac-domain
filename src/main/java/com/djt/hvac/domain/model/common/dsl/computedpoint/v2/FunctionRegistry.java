package com.djt.hvac.domain.model.common.dsl.computedpoint.v2;

import static java.util.Objects.requireNonNull;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.dsl.computedpoint.v2.function.ComputedPointFunction;
import com.djt.hvac.domain.model.common.dsl.computedpoint.v2.function.Function;
import com.djt.hvac.domain.model.common.dsl.computedpoint.v2.function.InvalidFunctionDefinitionException;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;

class FunctionRegistry {
  private static final Logger log = LoggerFactory.getLogger(FunctionRegistry.class);

  private static class InstanceHolder {
    private static final FunctionRegistry INSTANCE =
        FunctionRegistry.createInstance();
  }


  private final Map<String, Function> functions;

  static FunctionRegistry getInstance() {
    return InstanceHolder.INSTANCE;
  }

  private static FunctionRegistry createInstance() {
    Map<String, Function> functions = Maps.newHashMap();
    new FastClasspathScanner(ComputedPointFunction.class.getPackage().getName())
        // .verbose()
        .matchClassesWithAnnotation(ComputedPointFunction.class, c -> {
          try {
            Annotation annotation = c.getDeclaredAnnotation(ComputedPointFunction.class);
            if (annotation == null) {
              throw new AssertionError("Expected the " + c.getName()
                  + " class to have an annotation of type "
                  + ComputedPointFunction.class.getName());
            }
            ComputedPointFunction typedAnnotation = ComputedPointFunction.class.cast(annotation);
            String name = typedAnnotation.name();
            Object instance;
            try {
              instance = c.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
              throw new InvalidFunctionDefinitionException(c,
                  "it must expose a no-argument constructor", e);
            }
            Function function;
            try {
              function = Function.class.cast(instance);
            } catch (ClassCastException e) {
              throw new InvalidFunctionDefinitionException(c,
                  "it must implement the " + Function.class.getName() + " interface.", e);
            }
            functions.put(name, function);
          } catch (Exception e) {
            log.error(
                (e.getMessage() != null && !e.getMessage().isEmpty() ? e.getMessage() + "; " : "")
                    + "this function will not be loaded into the function registry",
                e);
          }
        })
        .scan();
    return new FunctionRegistry(functions);
  }


  private FunctionRegistry(Map<String, Function> functions) {
    this.functions = ImmutableMap.copyOf(requireNonNull(functions, "functions cannot be null"));
  }

  public Set<String> names() {
    return functions.keySet();
  }

  public Function get(String name) {
    return functions.get(name);
  }
}

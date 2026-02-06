package com.djt.hvac.domain.model.common.dsl.computedpoint.v2.function;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.dsl.computedpoint.v2.Result;
import com.djt.hvac.domain.model.common.utils.ObjectMappers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;

import static java.util.Objects.requireNonNull;

import static com.google.common.base.Preconditions.checkArgument;

public abstract class AbstractStatefulFunction<T> extends AbstractFunction {
  Logger log = LoggerFactory.getLogger(AbstractStatefulFunction.class);

  private static final ObjectMapper MAPPER = ObjectMappers.create(); // new ObjectMapper();

  @SuppressWarnings("rawtypes")
  private static Map<Class<? extends AbstractStatefulFunction>, Type> STATE_TYPES =
      Maps.newHashMap();

  @Override
  public final Result eval(long timestamp, Arguments args,
      Map<String, String> functionState, String functionCallId) {
    validateArgs(args);
    requireNonNull(functionState, "functionState cannot be null");
    requireNonNull(functionCallId, "functionCallId cannot be null");

    Optional<T> state = deserializeFunctionState(functionState, functionCallId);

    FunctionCallResult callResult = eval(timestamp, args, state);

    Map<String, String> resultState =
        serializeFunctionState(functionState, functionCallId, callResult);

    Result result = Result.builder()
        .withValue(callResult.value)
        .withState(resultState)
        .build();

    return result;
  }

  @Override
  protected final void validateStateful(boolean stateful) {
    checkArgument(stateful == true, "expected the value of the stateful property to be true");
  }

  protected FunctionCallResult createResult(Optional<Double> value, Optional<T> state) {
    return new FunctionCallResult(value, state);
  }

  protected abstract FunctionCallResult eval(long timestamp, Arguments args,
      Optional<T> state);

  private Optional<T> deserializeFunctionState(Map<String, String> functionState,
      String functionCallId) {
    String stateString = functionState.get(functionCallId);
    T state = null;
    if (stateString != null) {
      try {
        Type parameterizedType = getStateType();
        JavaType javaType = MAPPER.constructType(parameterizedType);
        state = MAPPER.readValue(stateString, javaType);
      } catch (IOException e) {
        throw new FunctionStateSerializationException(
            "A problem occurred while attempting to deserialize the following function state string: "
                + stateString,
            e);
      }
    }
    return Optional.ofNullable(state);
  }

  private Map<String, String> serializeFunctionState(Map<String, String> functionState,
      String functionCallId, FunctionCallResult callResult) {
    Map<String, String> resultState = Maps.newHashMap(functionState);
    if (callResult.state.isPresent()) {
      String callResultStateString;
      try {
        callResultStateString = MAPPER.writeValueAsString(callResult.state.get());
      } catch (JsonProcessingException e) {
        throw new FunctionStateSerializationException(
            "A problem occurred while attempting to serialize the following function state: "
                + callResult.state.get(),
            e);
      }
      resultState.put(functionCallId, callResultStateString);
    }
    return resultState;
  }

  private Type getStateType() {
    Type type = STATE_TYPES.get(this.getClass());
    if (type == null) {
      synchronized (this) {
        type = STATE_TYPES.get(this.getClass());
        if (type == null) {
          ParameterizedType superClass =
              ParameterizedType.class.cast(this.getClass().getGenericSuperclass());
          Type[] genericParams = superClass.getActualTypeArguments();
          type = genericParams[0];
          STATE_TYPES.put(this.getClass(), type);
        }
      }
    }
    return type;
  }

  protected class FunctionCallResult {
    private final Optional<Double> value;
    private final Optional<T> state;

    private FunctionCallResult(Optional<Double> value, Optional<T> state) {
      this.value = requireNonNull(value, "value cannot be null");
      this.state = requireNonNull(state, "state cannot be null");
    }

  }

}

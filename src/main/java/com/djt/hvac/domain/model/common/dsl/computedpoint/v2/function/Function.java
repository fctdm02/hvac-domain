package com.djt.hvac.domain.model.common.dsl.computedpoint.v2.function;

import java.util.Map;

import com.djt.hvac.domain.model.common.dsl.computedpoint.v2.IntervalAlignment;
import com.djt.hvac.domain.model.common.dsl.computedpoint.v2.Result;

public interface Function {

  public String getName();

  public boolean isStateful();

  public int getMinParams();

  public int getMaxParams();

  public IntervalAlignment getIntervalAlignment();

  public Result eval(long timestamp, Arguments args,
      Map<String, String> functionState, String functionCallId);

}

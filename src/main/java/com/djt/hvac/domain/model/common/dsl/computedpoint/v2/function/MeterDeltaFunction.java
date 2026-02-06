package com.djt.hvac.domain.model.common.dsl.computedpoint.v2.function;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.djt.hvac.domain.model.common.dsl.computedpoint.v2.IntervalAlignment;
import com.google.common.collect.Lists;

@ComputedPointFunction(name = "meterdelta", stateful = true, minParams = 1, maxParams = 1,
intervalAlignment = IntervalAlignment.Nearest)
public class MeterDeltaFunction extends AbstractStatefulFunction<MeterDeltaFunctionState> {

  final static double INITIAL_VALUE_IF_NOT_FOUND = 0.0001d;
  final static int maxSampleSize = 500;
  final static int minSampleSize = 50;
  @Override
  protected FunctionCallResult eval(long timestamp,
      Arguments args, Optional<MeterDeltaFunctionState> state) {
    
    Optional<Double> resultValue = Optional.empty();
    Optional<MeterDeltaFunctionState> resultState = state;
    Optional<Double> arg = args.get(0);
    Optional<Double> negativeSlope = Optional.empty();
    List<Double> historicValues = ((state.isPresent())?state.get().getHistoricValues():Lists.newArrayList());
    
    if (arg.isPresent()) {
      
      // If the point is not found the value of 0.0001 is inserted, the standard deviation filter 
      // will filter out everything else after that if we have more than the minial history 
      // of not found values. So we filter it.
      if ( arg.get() > INITIAL_VALUE_IF_NOT_FOUND ) {
               
        if (state.isPresent() ) {
          
          double v = arg.get() - state.get().getValue();
          v = Math.round(v * 100000.0) / 100000.0;

          // if we get a negative slope and the next slope is the mirror image,
          // with a little error skip it.
          boolean skipRebound = false;
          negativeSlope = state.get().getNegativeSlope();
          if ( negativeSlope != null && negativeSlope.isPresent() && negativeSlope.get() >  0.0 ) {
            skipRebound = (v/negativeSlope.get() > 0.96 );
            
            if ( !skipRebound ) {
              negativeSlope = Optional.empty();
            }
          }
          
          // skip negative delta and immediate rebound on delta
          if ( arg.get() >= state.get().getValue() && !skipRebound ) {            
        	  resultValue = Optional.of(v);
          } else if (skipRebound ) {
            negativeSlope = Optional.empty();
          } else {
            negativeSlope = Optional.of( v * -1.0 );
          }
          
          // calculate standard deviation and skip if value beyond 3 standard deviations
          
          if ( resultValue.isPresent() ) {
        	  
        	  if ( historicValues.size() > minSampleSize ) {
        		  double standardDev = Math.abs( calculateSD(historicValues));
        		  
        		  if ( standardDev > 0.5 && resultValue.get() > (standardDev*1000) ) {
        			  resultValue = Optional.empty();
        		  }
        	  }
        	  
	          if ( historicValues.size() > maxSampleSize ) {
	        	  historicValues.remove(0);
	          }
	          
	          // store value as sample
	          if ( resultValue.isPresent()) {
	        	  historicValues.add(resultValue.get());
	          }
          }
        }
        
        resultState = Optional.of(MeterDeltaFunctionState.builder()
            .withValue(arg.get())
            .withTimestamp(timestamp)
            .withNegativeSlope(negativeSlope)
            .withHistoricValues(historicValues)
            .build());
      } 
    }
        
    return createResult(resultValue, resultState);
  }
  
  private double calculateSD(List<Double> numArray )
  {
      double sum = 0.0, standardDeviation = 0.0;
      int length = numArray.size();

      for(double num : numArray) {
          sum += num;
      }
      
      double mean = sum/length;

      for(double num: numArray) {
          standardDeviation += (num - mean)*(num - mean);
      }

      return Math.sqrt(standardDeviation/length);
  }
}

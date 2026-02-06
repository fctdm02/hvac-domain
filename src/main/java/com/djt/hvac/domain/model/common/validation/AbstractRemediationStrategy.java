package com.djt.hvac.domain.model.common.validation;

public abstract class AbstractRemediationStrategy implements RemediationStrategy {

  protected ThreadLocal<StringBuilder> stringBuilderThreadLocal = new ThreadLocal<StringBuilder>() {
    protected StringBuilder initialValue() {
        return new StringBuilder(256);
    }
  };  
  
  protected AbstractRemediationStrategy() {
  }
}

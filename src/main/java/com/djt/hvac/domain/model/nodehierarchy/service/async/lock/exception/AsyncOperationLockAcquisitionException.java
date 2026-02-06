package com.djt.hvac.domain.model.nodehierarchy.service.async.lock.exception;

public class AsyncOperationLockAcquisitionException extends Exception {

  private static final long serialVersionUID = 1L;

  public AsyncOperationLockAcquisitionException() {
      super();
  }

  public AsyncOperationLockAcquisitionException(
      String message, 
      Throwable cause, 
      boolean enableSuppression,
      boolean writableStackTrace) {
      super(
          message, 
          cause, 
          enableSuppression, 
          writableStackTrace);
  }

  public AsyncOperationLockAcquisitionException(String message, Throwable cause) {
      super(message, cause);
  }

  public AsyncOperationLockAcquisitionException(String message) {
      super(message);
  }

  public AsyncOperationLockAcquisitionException(Throwable cause) {
      super(cause);
  }
}

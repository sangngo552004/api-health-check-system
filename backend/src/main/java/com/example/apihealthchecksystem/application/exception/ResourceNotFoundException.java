package com.example.apihealthchecksystem.application.exception;

public class ResourceNotFoundException extends AppException {
  public ResourceNotFoundException() {
    super(AppErrorCode.RESOURCE_NOT_FOUND);
  }

  public ResourceNotFoundException(AppErrorCode errorCode, Object identifier) {
    super(errorCode, String.format("%s với định danh: %s", errorCode.getMessage(), identifier));
  }
}

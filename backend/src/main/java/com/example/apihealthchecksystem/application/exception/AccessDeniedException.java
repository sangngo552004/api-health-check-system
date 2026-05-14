package com.example.apihealthchecksystem.application.exception;

public class AccessDeniedException extends AppException {
  public AccessDeniedException(AppErrorCode errorCode) {
    super(errorCode);
  }

  public AccessDeniedException(String message) {
    super(AppErrorCode.ACCESS_DENIED, message);
  }

  public AccessDeniedException() {
    super(AppErrorCode.WORKSPACE_RESOURCE_ACCESS_DENIED);
  }
}

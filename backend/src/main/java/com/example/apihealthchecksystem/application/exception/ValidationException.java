package com.example.apihealthchecksystem.application.exception;

public class ValidationException extends AppException {
  public ValidationException(AppErrorCode errorCode) {
    super(errorCode);
  }

  public ValidationException(AppErrorCode errorCode, String detailMessage) {
    super(errorCode, detailMessage);
  }
}

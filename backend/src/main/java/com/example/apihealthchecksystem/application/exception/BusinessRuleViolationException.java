package com.example.apihealthchecksystem.application.exception;

public class BusinessRuleViolationException extends AppException {
  public BusinessRuleViolationException(AppErrorCode errorCode) {
    super(errorCode);
  }

  public BusinessRuleViolationException(AppErrorCode errorCode, String detailMessage) {
    super(errorCode, detailMessage);
  }
}

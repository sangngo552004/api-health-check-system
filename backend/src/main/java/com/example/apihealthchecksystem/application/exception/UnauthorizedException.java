package com.example.apihealthchecksystem.application.exception;

public class UnauthorizedException extends AppException {
  public UnauthorizedException() {
    super(AppErrorCode.UNAUTHORIZED);
  }

  public UnauthorizedException(AppErrorCode errorCode) {
    super(errorCode);
  }

  public UnauthorizedException(AppErrorCode errorCode, String detailMessage) {
    super(errorCode, detailMessage);
  }
}

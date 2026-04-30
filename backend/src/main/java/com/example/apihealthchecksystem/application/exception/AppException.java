package com.example.apihealthchecksystem.application.exception;

import com.example.apihealthchecksystem.domain.exception.DomainException;
import lombok.Getter;

@Getter
public class AppException extends DomainException {
  private final AppErrorCode errorCode;

  public AppException(AppErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public AppException(AppErrorCode errorCode, String detailMessage) {
    super(detailMessage);
    this.errorCode = errorCode;
  }
}

package com.example.apihealthchecksystem.application.exception;

public class AccessDeniedException extends AppException {
  public AccessDeniedException(String message) {
    super(AppErrorCode.ACCESS_DENIED, message);
  }

  public AccessDeniedException() {
    super(AppErrorCode.ACCESS_DENIED, "Không có quyền truy cập tài nguyên của Workspace này.");
  }
}

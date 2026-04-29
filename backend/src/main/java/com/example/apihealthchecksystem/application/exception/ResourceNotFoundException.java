package com.example.apihealthchecksystem.application.exception;

public class ResourceNotFoundException extends AppException {
  public ResourceNotFoundException() {
    super(AppErrorCode.RESOURCE_NOT_FOUND);
  }

  public ResourceNotFoundException(String resourceName, Object identifier) {
    super(
        AppErrorCode.RESOURCE_NOT_FOUND,
        String.format("Không tìm thấy %s với định danh: %s", resourceName, identifier));
  }
}

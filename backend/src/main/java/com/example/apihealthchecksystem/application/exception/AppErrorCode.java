package com.example.apihealthchecksystem.application.exception;

import lombok.Getter;

@Getter
public enum AppErrorCode {
  // General Errors
  INTERNAL_SERVER_ERROR("50000", "Lỗi hệ thống bất ngờ"),
  VALIDATION_ERROR("40001", "Dữ liệu đầu vào không hợp lệ"),
  RESOURCE_NOT_FOUND("40401", "Không tìm thấy dữ liệu yêu cầu"),

  // Endpoint Errors
  ENDPOINT_ALREADY_EXISTS("40010", "Endpoint này đã tồn tại trong hệ thống"),
  ENDPOINT_INVALID_URL("40011", "URL endpoint không hợp lệ");

  private final String code;
  private final String message;

  AppErrorCode(String code, String message) {
    this.code = code;
    this.message = message;
  }
}

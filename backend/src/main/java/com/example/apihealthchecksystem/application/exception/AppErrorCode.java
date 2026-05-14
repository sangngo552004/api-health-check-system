package com.example.apihealthchecksystem.application.exception;

import lombok.Getter;

@Getter
public enum AppErrorCode {
  // General Errors
  INTERNAL_SERVER_ERROR("50000", "Lỗi hệ thống bất ngờ"),
  VALIDATION_ERROR("40001", "Dữ liệu đầu vào không hợp lệ"),
  RESOURCE_NOT_FOUND("40401", "Không tìm thấy dữ liệu yêu cầu"),
  INVALID_WORKSPACE_MEMBER_ROLE("40020", "Vai trò thành viên workspace không hợp lệ"),

  // Endpoint Errors
  ENDPOINT_NOT_FOUND("40410", "Không tìm thấy endpoint"),
  ENDPOINT_ALREADY_EXISTS("40010", "Endpoint này đã tồn tại trong hệ thống"),
  ENDPOINT_INVALID_URL("40011", "URL endpoint không hợp lệ"),
  CHECK_POLICY_NOT_FOUND("40411", "Không tìm thấy check policy"),
  ALERT_RULE_NOT_FOUND("40412", "Không tìm thấy alert rule"),
  CONTACT_GROUP_NOT_FOUND("40413", "Không tìm thấy contact group"),
  WORKSPACE_NOT_FOUND("40414", "Không tìm thấy workspace"),

  // Security Errors
  ACCESS_DENIED("40301", "Không có quyền truy cập tài nguyên này"),
  WORKSPACE_RESOURCE_ACCESS_DENIED(
      "40302", "Không có quyền truy cập tài nguyên của Workspace này."),
  UNAUTHORIZED("40101", "Phiên đăng nhập không hợp lệ hoặc đã hết hạn");

  private final String code;
  private final String message;

  AppErrorCode(String code, String message) {
    this.code = code;
    this.message = message;
  }
}

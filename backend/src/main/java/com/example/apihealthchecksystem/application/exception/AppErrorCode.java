package com.example.apihealthchecksystem.application.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AppErrorCode {
  // General Errors
  INTERNAL_SERVER_ERROR("50000", "Lỗi hệ thống bất ngờ", HttpStatus.INTERNAL_SERVER_ERROR),
  VALIDATION_ERROR("40001", "Dữ liệu đầu vào không hợp lệ", HttpStatus.BAD_REQUEST),
  RESOURCE_NOT_FOUND("40401", "Không tìm thấy dữ liệu yêu cầu", HttpStatus.NOT_FOUND),
  INVALID_WORKSPACE_MEMBER_ROLE(
      "40020", "Vai trò thành viên workspace không hợp lệ", HttpStatus.BAD_REQUEST),
  INVALID_ROLE("40021", "Role không hợp lệ", HttpStatus.BAD_REQUEST),
  INVALID_INCIDENT_STATUS("40022", "Trạng thái incident không hợp lệ", HttpStatus.BAD_REQUEST),
  INVALID_INCIDENT_SEVERITY(
      "40023", "Mức độ nghiêm trọng incident không hợp lệ", HttpStatus.BAD_REQUEST),
  INVALID_ENDPOINT_STATUS("40024", "Trạng thái endpoint không hợp lệ", HttpStatus.BAD_REQUEST),
  INVALID_HTTP_METHOD("40025", "HTTP method không hợp lệ", HttpStatus.BAD_REQUEST),
  INVALID_CHECK_TYPE("40026", "Loại check không hợp lệ", HttpStatus.BAD_REQUEST),
  INVALID_ALERT_RULE_TYPE("40027", "Loại alert rule không hợp lệ", HttpStatus.BAD_REQUEST),
  INVALID_COMPARISON_OPERATOR("40028", "Toán tử so sánh không hợp lệ", HttpStatus.BAD_REQUEST),
  USER_NOT_FOUND("40402", "Không tìm thấy user", HttpStatus.NOT_FOUND),
  INCIDENT_NOT_FOUND("40403", "Không tìm thấy incident", HttpStatus.NOT_FOUND),
  USERNAME_ALREADY_EXISTS("40031", "Username đã tồn tại trong hệ thống", HttpStatus.BAD_REQUEST),
  EMAIL_ALREADY_EXISTS("40032", "Email đã tồn tại trong hệ thống", HttpStatus.BAD_REQUEST),
  WORKSPACE_SLUG_ALREADY_EXISTS(
      "40033", "Slug workspace đã tồn tại trong hệ thống", HttpStatus.BAD_REQUEST),
  USER_IS_WORKSPACE_OWNER(
      "40034", "Không thể xóa user đang được gán làm owner của workspace", HttpStatus.CONFLICT),
  ADMIN_CANNOT_JOIN_WORKSPACE(
      "40036", "Không thể thêm tài khoản quản trị vào workspace", HttpStatus.CONFLICT),
  WORKSPACE_HAS_DEPENDENT_DATA(
      "40035",
      "Không thể xóa workspace khi vẫn còn dữ liệu nghiệp vụ phụ thuộc bên trong",
      HttpStatus.CONFLICT),
  ALERT_RULE_WORKSPACE_MISMATCH(
      "40029", "Alert rule phải thuộc cùng workspace", HttpStatus.BAD_REQUEST),
  CONTACT_GROUP_WORKSPACE_MISMATCH(
      "40030", "Contact group phải thuộc cùng workspace", HttpStatus.BAD_REQUEST),
  ALERT_RULE_NOT_FOUND_IN_WORKSPACE(
      "40415", "Không tìm thấy alert rule trong workspace", HttpStatus.NOT_FOUND),
  CONTACT_GROUP_NOT_FOUND_IN_WORKSPACE(
      "40416", "Không tìm thấy contact group trong workspace", HttpStatus.NOT_FOUND),

  // Endpoint Errors
  ENDPOINT_NOT_FOUND("40410", "Không tìm thấy endpoint", HttpStatus.NOT_FOUND),
  ENDPOINT_ALREADY_EXISTS(
      "40010", "Endpoint này đã tồn tại trong hệ thống", HttpStatus.BAD_REQUEST),
  ENDPOINT_INVALID_URL("40011", "URL endpoint không hợp lệ", HttpStatus.BAD_REQUEST),
  CHECK_POLICY_NOT_FOUND("40411", "Không tìm thấy check policy", HttpStatus.NOT_FOUND),
  ALERT_RULE_NOT_FOUND("40412", "Không tìm thấy alert rule", HttpStatus.NOT_FOUND),
  CONTACT_GROUP_NOT_FOUND("40413", "Không tìm thấy contact group", HttpStatus.NOT_FOUND),
  WORKSPACE_NOT_FOUND("40414", "Không tìm thấy workspace", HttpStatus.NOT_FOUND),

  // Security Errors
  ACCESS_DENIED("40301", "Không có quyền truy cập tài nguyên này", HttpStatus.FORBIDDEN),
  WORKSPACE_RESOURCE_ACCESS_DENIED(
      "40302", "Không có quyền truy cập tài nguyên của Workspace này", HttpStatus.FORBIDDEN),
  UNAUTHORIZED("40101", "Phiên đăng nhập không hợp lệ hoặc đã hết hạn", HttpStatus.UNAUTHORIZED),
  INVALID_CREDENTIALS("40102", "Sai tài khoản hoặc mật khẩu", HttpStatus.UNAUTHORIZED),
  REFRESH_TOKEN_MISSING("40103", "Thiếu refresh token", HttpStatus.UNAUTHORIZED),
  REFRESH_TOKEN_INVALID("40104", "Refresh token không hợp lệ", HttpStatus.UNAUTHORIZED),
  REFRESH_TOKEN_NOT_FOUND("40105", "Refresh token không tồn tại", HttpStatus.UNAUTHORIZED),
  REFRESH_TOKEN_EXPIRED("40106", "Refresh token đã hết hạn", HttpStatus.UNAUTHORIZED);

  private final String code;
  private final String message;
  private final HttpStatus httpStatus;

  AppErrorCode(String code, String message, HttpStatus httpStatus) {
    this.code = code;
    this.message = message;
    this.httpStatus = httpStatus;
  }
}

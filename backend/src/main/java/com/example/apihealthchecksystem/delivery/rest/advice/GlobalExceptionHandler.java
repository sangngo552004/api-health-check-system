package com.example.apihealthchecksystem.delivery.rest.advice;

import com.example.apihealthchecksystem.application.exception.AppErrorCode;
import com.example.apihealthchecksystem.application.exception.AppException;
import com.example.apihealthchecksystem.application.exception.ResourceNotFoundException;
import com.example.apihealthchecksystem.delivery.rest.common.ApiResponse;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(
      ResourceNotFoundException ex) {
    log.warn("Resource not found: {}", ex.getMessage());
    AppErrorCode errorCode = ex.getErrorCode();
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiResponse.error(errorCode.getCode(), ex.getMessage()));
  }

  @ExceptionHandler(AppException.class)
  public ResponseEntity<ApiResponse<Object>> handleAppException(AppException ex) {
    log.warn("Application exception occurred: {}", ex.getMessage());
    AppErrorCode errorCode = ex.getErrorCode();
    return ResponseEntity.status(resolveHttpStatus(errorCode))
        .body(ApiResponse.error(errorCode.getCode(), ex.getMessage()));
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ApiResponse<Object>> handleBadCredentialsException(
      BadCredentialsException ex) {
    log.warn("Authentication failed: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(
            ApiResponse.error(AppErrorCode.UNAUTHORIZED.getCode(), "Sai tai khoan hoac mat khau"));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            (error) -> {
              String fieldName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();
              errors.put(fieldName, errorMessage);
            });

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            ApiResponse.error(
                AppErrorCode.VALIDATION_ERROR.getCode(),
                AppErrorCode.VALIDATION_ERROR.getMessage(),
                errors));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
    log.error("Unexpected exception occurred", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            ApiResponse.error(
                AppErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                AppErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
  }

  private HttpStatus resolveHttpStatus(AppErrorCode errorCode) {
    return switch (errorCode) {
      case ACCESS_DENIED, WORKSPACE_RESOURCE_ACCESS_DENIED -> HttpStatus.FORBIDDEN;
      case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
      case RESOURCE_NOT_FOUND,
              ENDPOINT_NOT_FOUND,
              CHECK_POLICY_NOT_FOUND,
              ALERT_RULE_NOT_FOUND,
              CONTACT_GROUP_NOT_FOUND,
              WORKSPACE_NOT_FOUND ->
          HttpStatus.NOT_FOUND;
      case INTERNAL_SERVER_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
      default -> HttpStatus.BAD_REQUEST;
    };
  }
}

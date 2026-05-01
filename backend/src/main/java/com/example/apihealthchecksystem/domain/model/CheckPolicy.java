package com.example.apihealthchecksystem.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckPolicy {
  private Long id;
  private String name;
  private Integer intervalSeconds;
  private Integer timeoutMillis;
  private Integer retryCount;
  private Integer failureThreshold;
  private Integer latencyThresholdMillis;

  /** HTTP Status Code mong đợi (ví dụ: 200, 201). Chuyển từ Endpoint sang Template. */
  private Integer expectedStatusCode;

  /** Nội dung phản hồi mong đợi (chuỗi cố định). */
  private String expectedResponseBody;

  /** Biểu thức chính quy để kiểm tra nội dung phản hồi. */
  private String responseRegex;

  /** Người quản trị tạo ra template này. */
  private Long createdBy;
}

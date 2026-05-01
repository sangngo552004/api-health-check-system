package com.example.apihealthchecksystem.domain.valueobject;

public enum IncidentSeverity {
  /** Ảnh hưởng nghiêm trọng đến kinh doanh, cần xử lý ngay lập tức. */
  CRITICAL,

  /** Ảnh hưởng đến một phần tính năng, cần xử lý sớm. */
  WARNING,

  /** Các vấn đề nhỏ hoặc thông tin giám sát. */
  INFO
}

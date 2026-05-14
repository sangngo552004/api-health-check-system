package com.example.apihealthchecksystem.application.port.out;

import com.example.apihealthchecksystem.domain.model.CheckPolicy;
import com.example.apihealthchecksystem.domain.model.HealthCheckResult;
import com.example.apihealthchecksystem.domain.model.MonitoredEndpoint;

/**
 * Port out — Strategy interface cho các loại Health Check. Mỗi implementation sẽ xử lý một
 * CheckType khác nhau (HTTP, TCP, ...).
 */
public interface HealthCheckExecutor {

  /**
   * Kiểm tra xem executor này có hỗ trợ CheckType tương ứng không.
   *
   * @param checkType loại kiểm tra
   * @return true nếu hỗ trợ
   */
  boolean supports(com.example.apihealthchecksystem.domain.valueobject.CheckType checkType);

  /**
   * Thực thi health check cho một endpoint với policy cụ thể.
   *
   * @param endpoint endpoint cần kiểm tra
   * @param policy cấu hình kiểm tra (timeout, retry, expected status, ...)
   * @return HealthCheckResult kết quả sau khi kiểm tra
   */
  HealthCheckResult execute(MonitoredEndpoint endpoint, CheckPolicy policy);
}

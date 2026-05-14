package com.example.apihealthchecksystem.application.port.in;

/**
 * Use case điều phối toàn bộ quá trình kiểm tra sức khỏe API. Được gọi bởi Scheduler theo lịch định
 * kỳ.
 */
public interface MonitorEndpointUseCase {

  /** Chạy health check cho tất cả endpoint đang active của toàn hệ thống. */
  void runHealthCheckForAll();

  /** Chạy health check cho một endpoint cụ thể theo yêu cầu thủ công. */
  void runHealthCheckForEndpoint(Long endpointId);
}

package com.example.apihealthchecksystem.infrastructure.scheduler;

import com.example.apihealthchecksystem.application.port.in.MonitorEndpointUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class HealthCheckScheduler {

  private final MonitorEndpointUseCase monitorEndpointUseCase;

  /**
   * Định kỳ quét tất cả các endpoint active và kiểm tra sức khỏe. Chạy mỗi 60 giây (hoặc cấu hình
   * qua application.properties).
   */
  @Scheduled(fixedDelayString = "${app.scheduler.interval-ms:60000}")
  public void runPeriodicHealthCheck() {
    log.info("Scheduler: Bắt đầu chu kỳ health check...");
    monitorEndpointUseCase.runHealthCheckForAll();
    log.info("Scheduler: Đã hoàn tất chu kỳ health check.");
  }
}

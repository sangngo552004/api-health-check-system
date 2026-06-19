package com.example.apihealthchecksystem.application.monitoring;

import com.example.apihealthchecksystem.application.port.in.MonitorEndpointUseCase;
import com.example.apihealthchecksystem.application.port.in.RunEndpointHealthCheckUseCase;
import com.example.apihealthchecksystem.application.port.out.EndpointRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class MonitorEndpointBatchService implements MonitorEndpointUseCase {

  private final EndpointRepository endpointRepository;
  private final RunEndpointHealthCheckUseCase runEndpointHealthCheckUseCase;

  @Override
  public void runHealthCheckForAll() {
    var endpoints = endpointRepository.findAllActiveDueForCheck(LocalDateTime.now());
    LocalDateTime now = LocalDateTime.now();
    log.info(
        "Bắt đầu quét {} endpoint đến hạn để kiểm tra sức khỏe trên thread {}...",
        endpoints.size(),
        Thread.currentThread().getName());

    for (var endpoint : endpoints) {
      try {
        runEndpointHealthCheckUseCase.runHealthCheckForEndpoint(endpoint.getId());
      } catch (Exception ex) {
        log.error("Lỗi khi kiểm tra endpoint ID {}: {}", endpoint.getId(), ex.getMessage(), ex);
      }
    }

    log.info(
        "Hoàn tất quét health check lúc {}. {} endpoint đã được đưa vào kiểm tra.",
        now,
        endpoints.size());
  }

  @Override
  public void runHealthCheckForEndpoint(Long endpointId) {
    runEndpointHealthCheckUseCase.runHealthCheckForEndpoint(endpointId);
  }
}

package com.example.apihealthchecksystem.application.port.in;

public interface RunEndpointHealthCheckUseCase {
  void runHealthCheckForEndpoint(Long endpointId);
}

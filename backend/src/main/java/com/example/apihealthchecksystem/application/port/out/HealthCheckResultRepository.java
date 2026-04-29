package com.example.apihealthchecksystem.application.port.out;

import com.example.apihealthchecksystem.domain.model.HealthCheckResult;
import java.util.List;

public interface HealthCheckResultRepository {
  HealthCheckResult save(HealthCheckResult result);

  List<HealthCheckResult> findByEndpointId(Long endpointId);
}

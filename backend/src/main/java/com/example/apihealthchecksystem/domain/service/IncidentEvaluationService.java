package com.example.apihealthchecksystem.domain.service;

import com.example.apihealthchecksystem.domain.model.CheckPolicy;
import com.example.apihealthchecksystem.domain.model.HealthCheckResult;
import com.example.apihealthchecksystem.domain.model.Incident;
import com.example.apihealthchecksystem.domain.valueobject.CheckStatus;
import java.util.List;

public class IncidentEvaluationService {

  public boolean shouldOpenIncident(List<HealthCheckResult> recentResults, CheckPolicy policy) {
    return false;
  }

  public boolean shouldResolveIncident(HealthCheckResult latestResult, Incident openIncident) {
    if (latestResult == null || openIncident == null) {
      return false;
    }
    return openIncident.isOpen() && CheckStatus.UP.equals(latestResult.getStatus());
  }
}

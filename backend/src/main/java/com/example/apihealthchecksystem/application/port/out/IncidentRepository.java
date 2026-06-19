package com.example.apihealthchecksystem.application.port.out;

import com.example.apihealthchecksystem.domain.model.Incident;
import com.example.apihealthchecksystem.domain.valueobject.IncidentSeverity;
import com.example.apihealthchecksystem.domain.valueobject.IncidentStatus;
import java.util.List;
import java.util.Optional;

public interface IncidentRepository {
  Incident save(Incident incident);

  Optional<Incident> findById(Long id);

  List<Incident> findByEndpointId(Long endpointId);

  Optional<Incident> findOpenIncidentByEndpointId(Long endpointId);

  List<Incident> findOpenIncidentsByWorkspaceId(Long workspaceId);

  List<DashboardIncidentView> findDashboardOpenIncidentsByWorkspaceId(Long workspaceId);

  List<Incident> findByWorkspaceId(Long workspaceId);

  List<IncidentListView> findListByWorkspace(
      Long workspaceId,
      IncidentStatus status,
      IncidentSeverity severity,
      Long endpointId,
      String search);
}

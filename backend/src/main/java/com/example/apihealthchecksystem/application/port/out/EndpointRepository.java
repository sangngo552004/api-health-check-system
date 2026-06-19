package com.example.apihealthchecksystem.application.port.out;

import com.example.apihealthchecksystem.application.dto.response.PageResult;
import com.example.apihealthchecksystem.domain.model.MonitoredEndpoint;
import com.example.apihealthchecksystem.domain.valueobject.CheckType;
import com.example.apihealthchecksystem.domain.valueobject.EndpointStatus;
import com.example.apihealthchecksystem.domain.valueobject.HttpMethod;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EndpointRepository {
  MonitoredEndpoint save(MonitoredEndpoint endpoint);

  Optional<MonitoredEndpoint> findById(Long id);

  List<MonitoredEndpoint> findAll();

  List<MonitoredEndpoint> findAllActive();

  List<MonitoredEndpoint> findAllActiveDueForCheck(LocalDateTime now);

  PageResult<MonitoredEndpoint> searchByWorkspace(
      Long workspaceId,
      String search,
      String environment,
      EndpointStatus status,
      HttpMethod method,
      CheckType checkType,
      Boolean isActive,
      int page,
      int size,
      String sortBy,
      String sortDir);

  List<MonitoredEndpoint> findByWorkspaceId(Long workspaceId);

  List<DashboardEndpointView> findDashboardByWorkspaceId(Long workspaceId);

  void deleteById(Long id);

  boolean existsById(Long id);
}

package com.example.apihealthchecksystem.infrastructure.persistence.repository;

import com.example.apihealthchecksystem.application.port.out.DashboardIncidentView;
import com.example.apihealthchecksystem.application.port.out.IncidentListView;
import com.example.apihealthchecksystem.domain.valueobject.IncidentSeverity;
import com.example.apihealthchecksystem.domain.valueobject.IncidentStatus;
import com.example.apihealthchecksystem.infrastructure.persistence.entity.IncidentJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidentJpaRepository extends JpaRepository<IncidentJpaEntity, Long> {
  List<IncidentJpaEntity> findByEndpointId(Long endpointId);

  List<IncidentJpaEntity> findByWorkspaceId(Long workspaceId);

  Optional<IncidentJpaEntity> findFirstByEndpointIdAndStatusOrderByIdDesc(
      Long endpointId, IncidentStatus status);

  List<IncidentJpaEntity> findByWorkspaceIdAndStatus(Long workspaceId, IncidentStatus status);

  @org.springframework.data.jpa.repository.Query(
      """
      SELECT new com.example.apihealthchecksystem.application.port.out.IncidentListView(
          i.id,
          i.endpointId,
          e.name,
          i.workspaceId,
          i.startedAt,
          i.resolvedAt,
          i.status,
          i.reason,
          i.failureCount,
          i.severity,
          i.rootCause)
      FROM IncidentJpaEntity i
      JOIN MonitoredEndpointJpaEntity e ON e.id = i.endpointId
      WHERE i.workspaceId = :workspaceId
        AND (:status IS NULL OR i.status = :status)
        AND (:severity IS NULL OR i.severity = :severity)
        AND (:endpointId IS NULL OR i.endpointId = :endpointId)
        AND (:search IS NULL OR i.id IS NOT NULL)
      """)
  List<IncidentListView> findListByWorkspace(
      @org.springframework.data.repository.query.Param("workspaceId") Long workspaceId,
      @org.springframework.data.repository.query.Param("status") IncidentStatus status,
      @org.springframework.data.repository.query.Param("severity") IncidentSeverity severity,
      @org.springframework.data.repository.query.Param("endpointId") Long endpointId,
      @org.springframework.data.repository.query.Param("search") String search);

  @org.springframework.data.jpa.repository.Query(
      """
      SELECT new com.example.apihealthchecksystem.application.port.out.DashboardIncidentView(
          i.id,
          i.endpointId,
          e.name,
          i.startedAt,
          i.reason,
          i.severity)
      FROM IncidentJpaEntity i
      JOIN MonitoredEndpointJpaEntity e ON e.id = i.endpointId
      WHERE i.workspaceId = :workspaceId AND i.status = :status
      """)
  List<DashboardIncidentView> findDashboardByWorkspaceIdAndStatus(
      @org.springframework.data.repository.query.Param("workspaceId") Long workspaceId,
      @org.springframework.data.repository.query.Param("status") IncidentStatus status);
}

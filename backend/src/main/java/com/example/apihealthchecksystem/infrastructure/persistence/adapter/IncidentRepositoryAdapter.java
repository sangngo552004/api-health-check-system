package com.example.apihealthchecksystem.infrastructure.persistence.adapter;

import com.example.apihealthchecksystem.application.port.out.DashboardIncidentView;
import com.example.apihealthchecksystem.application.port.out.IncidentRepository;
import com.example.apihealthchecksystem.application.port.out.IncidentListView;
import com.example.apihealthchecksystem.domain.model.Incident;
import com.example.apihealthchecksystem.domain.valueobject.IncidentSeverity;
import com.example.apihealthchecksystem.domain.valueobject.IncidentStatus;
import com.example.apihealthchecksystem.infrastructure.persistence.mapper.IncidentMapper;
import com.example.apihealthchecksystem.infrastructure.persistence.repository.IncidentJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IncidentRepositoryAdapter implements IncidentRepository {

  private final IncidentJpaRepository jpaRepository;
  private final IncidentMapper mapper;

  @Override
  @Transactional
  public Incident save(Incident incident) {
    return mapper.toDomain(jpaRepository.save(mapper.toEntity(incident)));
  }

  @Override
  public Optional<Incident> findById(Long id) {
    return jpaRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public List<Incident> findByEndpointId(Long endpointId) {
    return jpaRepository.findByEndpointId(endpointId).stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<Incident> findOpenIncidentByEndpointId(Long endpointId) {
    return jpaRepository
        .findFirstByEndpointIdAndStatusOrderByIdDesc(endpointId, IncidentStatus.OPEN)
        .map(mapper::toDomain);
  }

  @Override
  public List<Incident> findOpenIncidentsByWorkspaceId(Long workspaceId) {
    return jpaRepository.findByWorkspaceIdAndStatus(workspaceId, IncidentStatus.OPEN).stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<DashboardIncidentView> findDashboardOpenIncidentsByWorkspaceId(Long workspaceId) {
    return jpaRepository.findDashboardByWorkspaceIdAndStatus(workspaceId, IncidentStatus.OPEN);
  }

  @Override
  public List<Incident> findByWorkspaceId(Long workspaceId) {
    return jpaRepository.findByWorkspaceId(workspaceId).stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<IncidentListView> findListByWorkspace(
      Long workspaceId,
      IncidentStatus status,
      IncidentSeverity severity,
      Long endpointId,
      String search) {
    return jpaRepository.findListByWorkspace(workspaceId, status, severity, endpointId, search);
  }
}

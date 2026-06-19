package com.example.apihealthchecksystem.infrastructure.persistence.adapter;

import com.example.apihealthchecksystem.application.port.out.DashboardEndpointView;
import com.example.apihealthchecksystem.application.dto.response.PageResult;
import com.example.apihealthchecksystem.application.port.out.EndpointRepository;
import com.example.apihealthchecksystem.application.port.out.EndpointListView;
import com.example.apihealthchecksystem.domain.model.MonitoredEndpoint;
import com.example.apihealthchecksystem.domain.valueobject.CheckType;
import com.example.apihealthchecksystem.domain.valueobject.EndpointStatus;
import com.example.apihealthchecksystem.domain.valueobject.HttpMethod;
import java.time.LocalDateTime;
import com.example.apihealthchecksystem.infrastructure.persistence.mapper.EndpointMapper;
import com.example.apihealthchecksystem.infrastructure.persistence.repository.MonitoredEndpointJpaRepository;
import com.example.apihealthchecksystem.infrastructure.persistence.support.RepositoryQuerySupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class EndpointRepositoryAdapter implements EndpointRepository {
  private static final Set<String> ALLOWED_SORT_FIELDS =
      Set.of(
          "id",
          "name",
          "url",
          "environment",
          "method",
          "checkType",
          "isActive",
          "status",
          "createdAt",
          "updatedAt",
          "lastCheckedAt",
          "nextRunAt");

  private final MonitoredEndpointJpaRepository jpaRepository;
  private final EndpointMapper mapper;

  @Override
  public MonitoredEndpoint save(MonitoredEndpoint endpoint) {
    return mapper.toDomain(jpaRepository.save(mapper.toEntity(endpoint)));
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<MonitoredEndpoint> findById(Long id) {
    return jpaRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  @Transactional(readOnly = true)
  public List<MonitoredEndpoint> findAll() {
    return jpaRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<MonitoredEndpoint> findAllActive() {
    return jpaRepository.findByIsActiveTrue().stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<MonitoredEndpoint> findAllActiveDueForCheck(LocalDateTime now) {
    return jpaRepository.findAllActiveDueForCheck(now).stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public PageResult<MonitoredEndpoint> searchByWorkspace(
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
      String sortDir) {
    var pageable =
        PageRequest.of(
            page,
            size,
            RepositoryQuerySupport.buildSort(
                sortBy, sortDir, ALLOWED_SORT_FIELDS, "createdAt"));
    var result =
        jpaRepository.searchList(
            workspaceId,
            RepositoryQuerySupport.normalizeSearch(search),
            RepositoryQuerySupport.normalizeSearch(environment),
            status != null ? status.name() : null,
            method,
            checkType,
            isActive,
            pageable);
    List<Long> endpointIds = result.getContent().stream().map(EndpointListView::id).toList();
    Map<Long, List<String>> tagsByEndpointId = loadTagsByEndpointId(endpointIds);
    Map<Long, List<Long>> alertRuleIdsByEndpointId = loadAlertRuleIdsByEndpointId(endpointIds);
    Map<Long, Map<String, String>> headersByEndpointId = loadHeadersByEndpointId(endpointIds);

    return new PageResult<>(
        result.getContent().stream()
            .map(
                view ->
                    toDomain(
                        view,
                        tagsByEndpointId.getOrDefault(view.id(), List.of()),
                        alertRuleIdsByEndpointId.getOrDefault(view.id(), List.of()),
                        headersByEndpointId.getOrDefault(view.id(), Map.of())))
            .toList(),
        result.getTotalElements());
  }

  @Override
  @Transactional(readOnly = true)
  public List<MonitoredEndpoint> findByWorkspaceId(Long workspaceId) {
    return jpaRepository.findByWorkspaceId(workspaceId).stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<DashboardEndpointView> findDashboardByWorkspaceId(Long workspaceId) {
    return jpaRepository.findDashboardByWorkspaceId(workspaceId);
  }

  @Override
  public void deleteById(Long id) {
    jpaRepository.deleteById(id);
  }

  @Override
  public boolean existsById(Long id) {
    return jpaRepository.existsById(id);
  }

  private MonitoredEndpoint toDomain(
      EndpointListView view,
      List<String> tags,
      List<Long> alertRuleIds,
      Map<String, String> headers) {
    return MonitoredEndpoint.builder()
        .id(view.id())
        .name(view.name())
        .url(view.url())
        .method(view.method())
        .environment(view.environment())
        .checkType(view.checkType())
        .workspaceId(view.workspaceId())
        .policyId(view.policyId())
        .isActive(view.isActive())
        .status(parseStatus(view.status()))
        .createdAt(view.createdAt())
        .updatedAt(view.updatedAt())
        .lastCheckedAt(view.lastCheckedAt())
        .nextRunAt(view.nextRunAt())
        .requestBody(view.requestBody())
        .tags(tags)
        .alertRuleIds(alertRuleIds)
        .headers(headers)
        .build();
  }

  private Map<Long, List<String>> loadTagsByEndpointId(List<Long> endpointIds) {
    if (endpointIds.isEmpty()) {
      return Map.of();
    }

    Map<Long, List<String>> tagsByEndpointId = new HashMap<>();
    for (Object[] row : jpaRepository.findTagsByEndpointIds(endpointIds)) {
      Long endpointId = ((Number) row[0]).longValue();
      String tag = (String) row[1];
      tagsByEndpointId.computeIfAbsent(endpointId, ignored -> new ArrayList<>()).add(tag);
    }
    return tagsByEndpointId;
  }

  private Map<Long, List<Long>> loadAlertRuleIdsByEndpointId(List<Long> endpointIds) {
    if (endpointIds.isEmpty()) {
      return Map.of();
    }

    Map<Long, List<Long>> alertRuleIdsByEndpointId = new HashMap<>();
    for (Object[] row : jpaRepository.findAlertRuleIdsByEndpointIds(endpointIds)) {
      Long endpointId = ((Number) row[0]).longValue();
      Long alertRuleId = ((Number) row[1]).longValue();
      alertRuleIdsByEndpointId
          .computeIfAbsent(endpointId, ignored -> new ArrayList<>())
          .add(alertRuleId);
    }
    return alertRuleIdsByEndpointId;
  }

  private Map<Long, Map<String, String>> loadHeadersByEndpointId(List<Long> endpointIds) {
    if (endpointIds.isEmpty()) {
      return Map.of();
    }

    Map<Long, Map<String, String>> headersByEndpointId = new HashMap<>();
    for (Object[] row : jpaRepository.findHeadersByEndpointIds(endpointIds)) {
      Long endpointId = ((Number) row[0]).longValue();
      String headerKey = (String) row[1];
      String headerValue = (String) row[2];
      headersByEndpointId
          .computeIfAbsent(endpointId, ignored -> new HashMap<>())
          .put(headerKey, headerValue);
    }
    return headersByEndpointId;
  }

  private EndpointStatus parseStatus(String status) {
    if (status == null || status.isBlank()) {
      return null;
    }
    try {
      return EndpointStatus.valueOf(status);
    } catch (IllegalArgumentException ex) {
      return null;
    }
  }
}

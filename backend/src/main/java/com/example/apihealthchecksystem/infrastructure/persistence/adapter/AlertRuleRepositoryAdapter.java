package com.example.apihealthchecksystem.infrastructure.persistence.adapter;

import com.example.apihealthchecksystem.application.dto.response.PageResult;
import com.example.apihealthchecksystem.application.port.out.AlertRuleRepository;
import com.example.apihealthchecksystem.domain.model.AlertRule;
import com.example.apihealthchecksystem.domain.valueobject.AlertRuleType;
import com.example.apihealthchecksystem.domain.valueobject.ComparisonOperator;
import com.example.apihealthchecksystem.infrastructure.persistence.mapper.AlertRuleMapper;
import com.example.apihealthchecksystem.infrastructure.persistence.repository.AlertRuleJpaRepository;
import com.example.apihealthchecksystem.infrastructure.persistence.support.RepositoryQuerySupport;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlertRuleRepositoryAdapter implements AlertRuleRepository {
  private static final Set<String> ALLOWED_SORT_FIELDS =
      Set.of(
          "id", "name", "ruleType", "operator", "thresholdValue", "isActive", "createdAt",
          "updatedAt");

  private final AlertRuleJpaRepository jpaRepository;
  private final AlertRuleMapper mapper;

  @Override
  @Transactional
  public AlertRule save(AlertRule alertRule) {
    return mapper.toDomain(jpaRepository.save(mapper.toEntity(alertRule)));
  }

  @Override
  public Optional<AlertRule> findById(Long id) {
    return jpaRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public List<AlertRule> findAllByIds(List<Long> ids) {
    return jpaRepository.findAllById(ids).stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<AlertRule> findAll() {
    return jpaRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public PageResult<AlertRule> searchByWorkspace(
      Long workspaceId,
      String search,
      AlertRuleType ruleType,
      ComparisonOperator operator,
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
        jpaRepository.search(
            workspaceId,
            RepositoryQuerySupport.normalizeSearch(search),
            ruleType,
            operator,
            isActive,
            pageable);
    return new PageResult<>(
        result.getContent().stream().map(mapper::toDomain).toList(), result.getTotalElements());
  }

  @Override
  public void deleteById(Long id) {
    jpaRepository.deleteById(id);
  }
}

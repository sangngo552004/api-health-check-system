package com.example.apihealthchecksystem.infrastructure.persistence.adapter;

import com.example.apihealthchecksystem.application.port.out.AlertRuleRepository;
import com.example.apihealthchecksystem.domain.model.AlertRule;
import com.example.apihealthchecksystem.infrastructure.persistence.mapper.AlertRuleMapper;
import com.example.apihealthchecksystem.infrastructure.persistence.repository.AlertRuleJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlertRuleRepositoryAdapter implements AlertRuleRepository {

  private final AlertRuleJpaRepository jpaRepository;
  private final AlertRuleMapper mapper;

  @Override
  public AlertRule save(AlertRule alertRule) {
    return mapper.toDomain(jpaRepository.save(mapper.toEntity(alertRule)));
  }

  @Override
  public Optional<AlertRule> findById(Long id) {
    return jpaRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public List<AlertRule> findAll() {
    return jpaRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<AlertRule> findByWorkspaceId(Long workspaceId, int page, int size) {
    org.springframework.data.domain.Pageable pageable =
        org.springframework.data.domain.PageRequest.of(page, size);
    return jpaRepository.findByWorkspaceId(workspaceId, pageable).stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public long countByWorkspaceId(Long workspaceId) {
    return jpaRepository.countByWorkspaceId(workspaceId);
  }

  @Override
  public void deleteById(Long id) {
    jpaRepository.deleteById(id);
  }
}

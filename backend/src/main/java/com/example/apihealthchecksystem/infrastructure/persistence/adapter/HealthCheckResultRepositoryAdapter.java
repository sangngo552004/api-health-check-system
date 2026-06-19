package com.example.apihealthchecksystem.infrastructure.persistence.adapter;

import com.example.apihealthchecksystem.application.port.out.HealthCheckResultRepository;
import com.example.apihealthchecksystem.domain.model.HealthCheckResult;
import com.example.apihealthchecksystem.infrastructure.persistence.mapper.HealthCheckResultMapper;
import com.example.apihealthchecksystem.infrastructure.persistence.repository.HealthCheckResultJpaRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HealthCheckResultRepositoryAdapter implements HealthCheckResultRepository {

  private final HealthCheckResultJpaRepository jpaRepository;
  private final HealthCheckResultMapper mapper;

  @Override
  public HealthCheckResult save(HealthCheckResult result) {
    return mapper.toDomain(jpaRepository.save(mapper.toEntity(result)));
  }

  @Override
  public List<HealthCheckResult> findByEndpointId(Long endpointId) {
    return jpaRepository.findByEndpointId(endpointId).stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<HealthCheckResult> findTop10ByEndpointIdOrderByCheckedAtDesc(Long endpointId) {
    return jpaRepository.findTop10ByEndpointIdOrderByCheckedAtDesc(endpointId).stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<HealthCheckResult> findByEndpointIdsOrderByCheckedAtDesc(List<Long> endpointIds) {
    return jpaRepository.findByEndpointIdInOrderByCheckedAtDesc(endpointIds).stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<HealthCheckResult> findAllByIds(List<Long> resultIds) {
    return jpaRepository.findByIdIn(resultIds).stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }
}

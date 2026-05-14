package com.example.apihealthchecksystem.infrastructure.persistence.adapter;

import com.example.apihealthchecksystem.application.port.out.EndpointRepository;
import com.example.apihealthchecksystem.domain.model.MonitoredEndpoint;
import com.example.apihealthchecksystem.infrastructure.persistence.mapper.EndpointMapper;
import com.example.apihealthchecksystem.infrastructure.persistence.repository.MonitoredEndpointJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EndpointRepositoryAdapter implements EndpointRepository {

  private final MonitoredEndpointJpaRepository jpaRepository;
  private final EndpointMapper mapper;

  @Override
  public MonitoredEndpoint save(MonitoredEndpoint endpoint) {
    return mapper.toDomain(jpaRepository.save(mapper.toEntity(endpoint)));
  }

  @Override
  public Optional<MonitoredEndpoint> findById(Long id) {
    return jpaRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public List<MonitoredEndpoint> findAll() {
    return jpaRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<MonitoredEndpoint> findAllActive() {
    return jpaRepository.findByIsActiveTrue().stream()
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public List<MonitoredEndpoint> findByWorkspaceId(Long workspaceId, int page, int size) {
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

  @Override
  public boolean existsById(Long id) {
    return jpaRepository.existsById(id);
  }
}

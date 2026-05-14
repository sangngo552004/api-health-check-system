package com.example.apihealthchecksystem.infrastructure.persistence.adapter;

import com.example.apihealthchecksystem.application.port.out.IncidentRepository;
import com.example.apihealthchecksystem.domain.model.Incident;
import com.example.apihealthchecksystem.domain.valueobject.IncidentStatus;
import com.example.apihealthchecksystem.infrastructure.persistence.mapper.IncidentMapper;
import com.example.apihealthchecksystem.infrastructure.persistence.repository.IncidentJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IncidentRepositoryAdapter implements IncidentRepository {

  private final IncidentJpaRepository jpaRepository;
  private final IncidentMapper mapper;

  @Override
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
}

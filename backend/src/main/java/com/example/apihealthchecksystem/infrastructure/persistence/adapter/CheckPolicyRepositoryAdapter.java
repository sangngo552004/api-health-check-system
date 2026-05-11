package com.example.apihealthchecksystem.infrastructure.persistence.adapter;

import com.example.apihealthchecksystem.application.port.out.CheckPolicyRepository;
import com.example.apihealthchecksystem.domain.model.CheckPolicy;
import com.example.apihealthchecksystem.infrastructure.persistence.mapper.CheckPolicyMapper;
import com.example.apihealthchecksystem.infrastructure.persistence.repository.CheckPolicyJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheckPolicyRepositoryAdapter implements CheckPolicyRepository {

  private final CheckPolicyJpaRepository jpaRepository;
  private final CheckPolicyMapper mapper;

  @Override
  public CheckPolicy save(CheckPolicy policy) {
    return mapper.toDomain(jpaRepository.save(mapper.toEntity(policy)));
  }

  @Override
  public Optional<CheckPolicy> findById(Long id) {
    return jpaRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public List<CheckPolicy> findAll() {
    return jpaRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<CheckPolicy> findByWorkspaceId(Long workspaceId, int page, int size) {
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

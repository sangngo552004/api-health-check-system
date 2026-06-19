package com.example.apihealthchecksystem.infrastructure.persistence.adapter;

import com.example.apihealthchecksystem.application.dto.response.PageResult;
import com.example.apihealthchecksystem.application.port.out.CheckPolicyRepository;
import com.example.apihealthchecksystem.domain.model.CheckPolicy;
import com.example.apihealthchecksystem.infrastructure.persistence.mapper.CheckPolicyMapper;
import com.example.apihealthchecksystem.infrastructure.persistence.repository.CheckPolicyJpaRepository;
import com.example.apihealthchecksystem.infrastructure.persistence.support.RepositoryQuerySupport;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheckPolicyRepositoryAdapter implements CheckPolicyRepository {
  private static final Set<String> ALLOWED_SORT_FIELDS =
      Set.of(
          "id",
          "name",
          "intervalSeconds",
          "timeoutMillis",
          "retryCount",
          "degradedResponseTimeMillis",
          "expectedStatusCode",
          "createdAt",
          "updatedAt");

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
  public List<CheckPolicy> findAllByIds(List<Long> ids) {
    return jpaRepository.findAllById(ids).stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<CheckPolicy> findAll() {
    return jpaRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public PageResult<CheckPolicy> searchByWorkspace(
      Long workspaceId,
      String search,
      Integer expectedStatusCode,
      Boolean hasDegradedResponseTimeThreshold,
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
            expectedStatusCode,
            hasDegradedResponseTimeThreshold,
            pageable);
    return new PageResult<>(
        result.getContent().stream().map(mapper::toDomain).toList(), result.getTotalElements());
  }

  @Override
  public void deleteById(Long id) {
    jpaRepository.deleteById(id);
  }
}

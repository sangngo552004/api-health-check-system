package com.example.apihealthchecksystem.infrastructure.persistence.adapter;

import com.example.apihealthchecksystem.application.dto.response.PageResult;
import com.example.apihealthchecksystem.application.port.out.ContactGroupRepository;
import com.example.apihealthchecksystem.domain.model.ContactGroup;
import com.example.apihealthchecksystem.infrastructure.persistence.mapper.ContactGroupMapper;
import com.example.apihealthchecksystem.infrastructure.persistence.repository.ContactGroupJpaRepository;
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
public class ContactGroupRepositoryAdapter implements ContactGroupRepository {
  private static final Set<String> ALLOWED_SORT_FIELDS =
      Set.of("id", "name", "isActive", "createdAt", "updatedAt");

  private final ContactGroupJpaRepository jpaRepository;
  private final ContactGroupMapper mapper;

  @Override
  @Transactional
  public ContactGroup save(ContactGroup group) {
    return mapper.toDomain(jpaRepository.save(mapper.toEntity(group)));
  }

  @Override
  public Optional<ContactGroup> findById(Long id) {
    return jpaRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public List<ContactGroup> findAllByIds(List<Long> ids) {
    return jpaRepository.findAllById(ids).stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<ContactGroup> findAll() {
    return jpaRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public PageResult<ContactGroup> searchByWorkspace(
      Long workspaceId,
      String search,
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

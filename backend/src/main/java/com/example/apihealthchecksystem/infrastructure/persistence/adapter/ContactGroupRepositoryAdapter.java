package com.example.apihealthchecksystem.infrastructure.persistence.adapter;

import com.example.apihealthchecksystem.application.port.out.ContactGroupRepository;
import com.example.apihealthchecksystem.domain.model.ContactGroup;
import com.example.apihealthchecksystem.infrastructure.persistence.mapper.ContactGroupMapper;
import com.example.apihealthchecksystem.infrastructure.persistence.repository.ContactGroupJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContactGroupRepositoryAdapter implements ContactGroupRepository {

  private final ContactGroupJpaRepository jpaRepository;
  private final ContactGroupMapper mapper;

  @Override
  public ContactGroup save(ContactGroup group) {
    return mapper.toDomain(jpaRepository.save(mapper.toEntity(group)));
  }

  @Override
  public Optional<ContactGroup> findById(Long id) {
    return jpaRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public List<ContactGroup> findAll() {
    return jpaRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<ContactGroup> findByWorkspaceId(Long workspaceId, int page, int size) {
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

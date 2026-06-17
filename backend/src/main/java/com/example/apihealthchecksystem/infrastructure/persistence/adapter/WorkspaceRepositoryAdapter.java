package com.example.apihealthchecksystem.infrastructure.persistence.adapter;

import com.example.apihealthchecksystem.application.dto.response.PageResult;
import com.example.apihealthchecksystem.application.port.out.WorkspaceRepository;
import com.example.apihealthchecksystem.domain.model.Workspace;
import com.example.apihealthchecksystem.infrastructure.persistence.entity.WorkspaceMemberId;
import com.example.apihealthchecksystem.infrastructure.persistence.entity.WorkspaceMemberJpaEntity;
import com.example.apihealthchecksystem.infrastructure.persistence.mapper.WorkspaceMapper;
import com.example.apihealthchecksystem.infrastructure.persistence.repository.WorkspaceJpaRepository;
import com.example.apihealthchecksystem.infrastructure.persistence.repository.WorkspaceMemberJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WorkspaceRepositoryAdapter implements WorkspaceRepository {
  private static final Set<String> ALLOWED_SORT_FIELDS =
      Set.of("id", "name", "slug", "ownerId", "isActive", "createdAt", "updatedAt");

  private final WorkspaceJpaRepository jpaRepository;
  private final WorkspaceMemberJpaRepository memberJpaRepository;
  private final WorkspaceMapper mapper;

  @Override
  public List<Workspace> findAll() {
    return jpaRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public PageResult<Workspace> search(
      String search,
      Boolean isActive,
      Long ownerId,
      int page,
      int size,
      String sortBy,
      String sortDir) {
    var pageable = PageRequest.of(page, size, buildSort(sortBy, sortDir));
    var result = jpaRepository.search(normalizeSearch(search), isActive, ownerId, pageable);
    return new PageResult<>(
        result.getContent().stream().map(mapper::toDomain).toList(), result.getTotalElements());
  }

  @Override
  public Workspace save(Workspace workspace) {
    return mapper.toDomain(jpaRepository.save(mapper.toEntity(workspace)));
  }

  @Override
  public Optional<Workspace> findById(Long id) {
    return jpaRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public Optional<Workspace> findBySlug(String slug) {
    return jpaRepository.findBySlug(slug).map(mapper::toDomain);
  }

  @Override
  public List<Workspace> findByUserId(Long userId) {
    return memberJpaRepository.findByIdUserId(userId).stream()
        .map(member -> jpaRepository.findById(member.getId().getWorkspaceId()))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(mapper::toDomain)
        .collect(Collectors.toList());
  }

  @Override
  public boolean existsBySlug(String slug) {
    return jpaRepository.existsBySlug(slug);
  }

  @Override
  public boolean existsByOwnerId(Long ownerId) {
    return jpaRepository.existsByOwnerId(ownerId);
  }

  @Override
  public void deleteById(Long id) {
    jpaRepository.deleteById(id);
  }

  @Override
  public void addMember(Long workspaceId, Long userId) {
    if (memberJpaRepository.existsByIdWorkspaceIdAndIdUserId(workspaceId, userId)) {
      return;
    }
    WorkspaceMemberJpaEntity member = new WorkspaceMemberJpaEntity();
    member.setId(new WorkspaceMemberId(workspaceId, userId));
    memberJpaRepository.save(member);
  }

  @Override
  public void removeMember(Long workspaceId, Long userId) {
    memberJpaRepository.deleteById(new WorkspaceMemberId(workspaceId, userId));
  }

  @Override
  public List<com.example.apihealthchecksystem.domain.model.WorkspaceMember> getMembers(
      Long workspaceId) {
    return memberJpaRepository.findByIdWorkspaceId(workspaceId).stream()
        .map(
            member ->
                com.example.apihealthchecksystem.domain.model.WorkspaceMember.builder()
                    .workspaceId(workspaceId)
                    .userId(member.getId().getUserId())
                    .joinedAt(member.getJoinedAt())
                    .build())
        .collect(Collectors.toList());
  }

  private Sort buildSort(String sortBy, String sortDir) {
    String normalizedSortBy = ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : "createdAt";
    Sort.Direction direction =
        "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
    return Sort.by(direction, normalizedSortBy);
  }

  private String normalizeSearch(String search) {
    if (search == null || search.isBlank()) {
      return null;
    }
    return search.trim();
  }
}

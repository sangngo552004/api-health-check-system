package com.example.apihealthchecksystem.infrastructure.persistence.adapter;

import com.example.apihealthchecksystem.application.port.out.WorkspaceRepository;
import com.example.apihealthchecksystem.domain.model.Workspace;
import com.example.apihealthchecksystem.domain.valueobject.WorkspaceRole;
import com.example.apihealthchecksystem.infrastructure.persistence.entity.WorkspaceMemberId;
import com.example.apihealthchecksystem.infrastructure.persistence.entity.WorkspaceMemberJpaEntity;
import com.example.apihealthchecksystem.infrastructure.persistence.mapper.WorkspaceMapper;
import com.example.apihealthchecksystem.infrastructure.persistence.repository.WorkspaceJpaRepository;
import com.example.apihealthchecksystem.infrastructure.persistence.repository.WorkspaceMemberJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WorkspaceRepositoryAdapter implements WorkspaceRepository {

  private final WorkspaceJpaRepository jpaRepository;
  private final WorkspaceMemberJpaRepository memberJpaRepository;
  private final com.example.apihealthchecksystem.infrastructure.persistence.repository
          .UserJpaRepository
      userJpaRepository;
  private final WorkspaceMapper mapper;

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
  public void deleteById(Long id) {
    jpaRepository.deleteById(id);
  }

  @Override
  public void addMember(Long workspaceId, Long userId, WorkspaceRole role) {
    WorkspaceMemberJpaEntity member = new WorkspaceMemberJpaEntity();
    member.setId(new WorkspaceMemberId(workspaceId, userId));
    member.setRole(role.name());
    memberJpaRepository.save(member);
  }

  @Override
  public void removeMember(Long workspaceId, Long userId) {
    memberJpaRepository.deleteById(new WorkspaceMemberId(workspaceId, userId));
  }

  @Override
  public Optional<WorkspaceRole> getMemberRole(Long workspaceId, Long userId) {
    return memberJpaRepository
        .findById(new WorkspaceMemberId(workspaceId, userId))
        .map(member -> WorkspaceRole.valueOf(member.getRole()));
  }

  @Override
  public List<com.example.apihealthchecksystem.domain.model.WorkspaceMember> getMembers(
      Long workspaceId) {
    return memberJpaRepository.findByIdWorkspaceId(workspaceId).stream()
        .map(
            member -> {
              var user = userJpaRepository.findById(member.getId().getUserId()).orElseThrow();
              return com.example.apihealthchecksystem.domain.model.WorkspaceMember.builder()
                  .workspaceId(workspaceId)
                  .userId(user.getId())
                  .username(user.getUsername())
                  .email(user.getEmail())
                  .role(WorkspaceRole.valueOf(member.getRole()))
                  .joinedAt(member.getJoinedAt())
                  .build();
            })
        .collect(Collectors.toList());
  }
}

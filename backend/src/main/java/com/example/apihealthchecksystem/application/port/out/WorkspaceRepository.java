package com.example.apihealthchecksystem.application.port.out;

import com.example.apihealthchecksystem.domain.model.Workspace;
import com.example.apihealthchecksystem.domain.valueobject.WorkspaceRole;
import java.util.List;
import java.util.Optional;

public interface WorkspaceRepository {
  Workspace save(Workspace workspace);

  Optional<Workspace> findById(Long id);

  Optional<Workspace> findBySlug(String slug);

  List<Workspace> findByUserId(Long userId);

  void deleteById(Long id);

  void addMember(Long workspaceId, Long userId, WorkspaceRole role);

  void removeMember(Long workspaceId, Long userId);

  Optional<WorkspaceRole> getMemberRole(Long workspaceId, Long userId);

  List<com.example.apihealthchecksystem.domain.model.WorkspaceMember> getMembers(Long workspaceId);
}

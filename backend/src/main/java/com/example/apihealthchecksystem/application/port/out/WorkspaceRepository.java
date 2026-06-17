package com.example.apihealthchecksystem.application.port.out;

import com.example.apihealthchecksystem.application.dto.response.PageResult;
import com.example.apihealthchecksystem.domain.model.Workspace;
import java.util.List;
import java.util.Optional;

public interface WorkspaceRepository {
  List<Workspace> findAll();

  PageResult<Workspace> search(
      String search,
      Boolean isActive,
      Long ownerId,
      int page,
      int size,
      String sortBy,
      String sortDir);

  Workspace save(Workspace workspace);

  Optional<Workspace> findById(Long id);

  Optional<Workspace> findBySlug(String slug);

  List<Workspace> findByUserId(Long userId);

  boolean existsBySlug(String slug);

  boolean existsByOwnerId(Long ownerId);

  void deleteById(Long id);

  void addMember(Long workspaceId, Long userId);

  void removeMember(Long workspaceId, Long userId);

  List<com.example.apihealthchecksystem.domain.model.WorkspaceMember> getMembers(Long workspaceId);
}

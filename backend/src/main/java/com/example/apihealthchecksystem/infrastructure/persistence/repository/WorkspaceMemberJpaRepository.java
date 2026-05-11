package com.example.apihealthchecksystem.infrastructure.persistence.repository;

import com.example.apihealthchecksystem.infrastructure.persistence.entity.WorkspaceMemberId;
import com.example.apihealthchecksystem.infrastructure.persistence.entity.WorkspaceMemberJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkspaceMemberJpaRepository
    extends JpaRepository<WorkspaceMemberJpaEntity, WorkspaceMemberId> {
  boolean existsByIdWorkspaceIdAndIdUserId(Long workspaceId, Long userId);

  List<WorkspaceMemberJpaEntity> findByIdUserId(Long userId);

  List<WorkspaceMemberJpaEntity> findByIdWorkspaceId(Long workspaceId);
}

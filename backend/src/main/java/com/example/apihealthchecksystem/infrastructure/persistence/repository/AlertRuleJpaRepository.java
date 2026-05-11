package com.example.apihealthchecksystem.infrastructure.persistence.repository;

import com.example.apihealthchecksystem.infrastructure.persistence.entity.AlertRuleJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertRuleJpaRepository extends JpaRepository<AlertRuleJpaEntity, Long> {
  Page<AlertRuleJpaEntity> findByWorkspaceId(Long workspaceId, Pageable pageable);

  long countByWorkspaceId(Long workspaceId);
}

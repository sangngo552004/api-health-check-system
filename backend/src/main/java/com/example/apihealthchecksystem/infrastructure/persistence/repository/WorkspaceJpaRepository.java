package com.example.apihealthchecksystem.infrastructure.persistence.repository;

import com.example.apihealthchecksystem.infrastructure.persistence.entity.WorkspaceJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkspaceJpaRepository extends JpaRepository<WorkspaceJpaEntity, Long> {
  Optional<WorkspaceJpaEntity> findBySlug(String slug);
}

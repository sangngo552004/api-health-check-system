package com.example.apihealthchecksystem.infrastructure.persistence.repository;

import com.example.apihealthchecksystem.infrastructure.persistence.entity.WorkspaceJpaEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkspaceJpaRepository extends JpaRepository<WorkspaceJpaEntity, Long> {
  @Query(
      """
      SELECT w
      FROM WorkspaceJpaEntity w
      WHERE (:search IS NULL
              OR LOWER(w.name) LIKE LOWER(CONCAT('%', :search, '%'))
              OR LOWER(w.slug) LIKE LOWER(CONCAT('%', :search, '%'))
              OR LOWER(COALESCE(w.description, '')) LIKE LOWER(CONCAT('%', :search, '%')))
          AND (:isActive IS NULL OR w.isActive = :isActive)
          AND (:ownerId IS NULL OR w.ownerId = :ownerId)
      """)
  Page<WorkspaceJpaEntity> search(
      @Param("search") String search,
      @Param("isActive") Boolean isActive,
      @Param("ownerId") Long ownerId,
      Pageable pageable);

  Optional<WorkspaceJpaEntity> findBySlug(String slug);

  boolean existsBySlug(String slug);

  boolean existsByOwnerId(Long ownerId);
}

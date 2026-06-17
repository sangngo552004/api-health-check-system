package com.example.apihealthchecksystem.infrastructure.persistence.repository;

import com.example.apihealthchecksystem.domain.valueobject.UserRole;
import com.example.apihealthchecksystem.infrastructure.persistence.entity.UserJpaEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
  @Query(
      """
      SELECT u
      FROM UserJpaEntity u
      WHERE (:search IS NULL
              OR LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%'))
              OR LOWER(COALESCE(u.email, '')) LIKE LOWER(CONCAT('%', :search, '%'))
              OR LOWER(COALESCE(u.phoneNumber, '')) LIKE LOWER(CONCAT('%', :search, '%')))
          AND (:role IS NULL OR u.role = :role)
          AND (:isActive IS NULL OR u.isActive = :isActive)
      """)
  Page<UserJpaEntity> search(
      @Param("search") String search,
      @Param("role") UserRole role,
      @Param("isActive") Boolean isActive,
      Pageable pageable);

  Optional<UserJpaEntity> findByUsername(String username);

  Optional<UserJpaEntity> findByEmail(String email);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);
}

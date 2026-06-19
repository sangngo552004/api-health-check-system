package com.example.apihealthchecksystem.infrastructure.persistence.repository;

import com.example.apihealthchecksystem.infrastructure.persistence.entity.CheckPolicyJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckPolicyJpaRepository extends JpaRepository<CheckPolicyJpaEntity, Long> {
  @Query(
      """
      SELECT p
      FROM CheckPolicyJpaEntity p
      WHERE p.workspaceId = :workspaceId
        AND (:search = ''
            OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(COALESCE(p.expectedResponseBody, ''))
                LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(COALESCE(p.responseRegex, ''))
                LIKE LOWER(CONCAT('%', :search, '%')))
        AND (:expectedStatusCode IS NULL OR p.expectedStatusCode = :expectedStatusCode)
        AND (:hasDegradedResponseTimeThreshold IS NULL
            OR (:hasDegradedResponseTimeThreshold = TRUE
                AND p.degradedResponseTimeMillis IS NOT NULL)
            OR (:hasDegradedResponseTimeThreshold = FALSE
                AND p.degradedResponseTimeMillis IS NULL))
      """)
  Page<CheckPolicyJpaEntity> search(
      @Param("workspaceId") Long workspaceId,
      @Param("search") String search,
      @Param("expectedStatusCode") Integer expectedStatusCode,
      @Param("hasDegradedResponseTimeThreshold") Boolean hasDegradedResponseTimeThreshold,
      Pageable pageable);
}

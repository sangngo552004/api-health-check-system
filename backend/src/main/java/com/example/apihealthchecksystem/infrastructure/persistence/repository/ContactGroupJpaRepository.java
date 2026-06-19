package com.example.apihealthchecksystem.infrastructure.persistence.repository;

import com.example.apihealthchecksystem.infrastructure.persistence.entity.ContactGroupJpaEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactGroupJpaRepository extends JpaRepository<ContactGroupJpaEntity, Long> {
  @Query(
      """
      SELECT DISTINCT email
      FROM ContactGroupJpaEntity c
      JOIN c.emailAddresses email
      WHERE c.id IN :ids
        AND c.isActive = true
        AND TRIM(email) <> ''
      """)
  List<String> findDistinctActiveEmailAddressesByIdIn(@Param("ids") List<Long> ids);

  @Query(
      value =
          """
          SELECT DISTINCT c
          FROM ContactGroupJpaEntity c
          LEFT JOIN c.emailAddresses email
          WHERE c.workspaceId = :workspaceId
            AND (:search = ''
                OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(COALESCE(c.description, ''))
                    LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(COALESCE(email, ''))
                    LIKE LOWER(CONCAT('%', :search, '%')))
            AND (:isActive IS NULL OR c.isActive = :isActive)
          """,
      countQuery =
          """
          SELECT COUNT(DISTINCT c)
          FROM ContactGroupJpaEntity c
          LEFT JOIN c.emailAddresses email
          WHERE c.workspaceId = :workspaceId
            AND (:search = ''
                OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(COALESCE(c.description, ''))
                    LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(COALESCE(email, ''))
                    LIKE LOWER(CONCAT('%', :search, '%')))
            AND (:isActive IS NULL OR c.isActive = :isActive)
          """)
  Page<ContactGroupJpaEntity> search(
      @Param("workspaceId") Long workspaceId,
      @Param("search") String search,
      @Param("isActive") Boolean isActive,
      Pageable pageable);
}

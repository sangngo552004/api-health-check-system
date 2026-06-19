package com.example.apihealthchecksystem.infrastructure.persistence.repository;

import com.example.apihealthchecksystem.domain.valueobject.AlertRuleType;
import com.example.apihealthchecksystem.domain.valueobject.ComparisonOperator;
import com.example.apihealthchecksystem.infrastructure.persistence.entity.AlertRuleJpaEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertRuleJpaRepository extends JpaRepository<AlertRuleJpaEntity, Long> {
  @Query(
      """
      SELECT DISTINCT contactGroupId
      FROM AlertRuleJpaEntity a
      JOIN a.contactGroupIds contactGroupId
      WHERE a.id IN :ids
        AND a.isActive = true
      """)
  List<Long> findDistinctActiveContactGroupIdsByIdIn(@Param("ids") List<Long> ids);

  @Query(
      """
      SELECT a
      FROM AlertRuleJpaEntity a
      WHERE a.workspaceId = :workspaceId
        AND (:search = ''
            OR LOWER(a.name) LIKE LOWER(CONCAT('%', :search, '%')))
        AND (:ruleType IS NULL OR a.ruleType = :ruleType)
        AND (:operator IS NULL OR a.operator = :operator)
        AND (:isActive IS NULL OR a.isActive = :isActive)
      """)
  Page<AlertRuleJpaEntity> search(
      @Param("workspaceId") Long workspaceId,
      @Param("search") String search,
      @Param("ruleType") AlertRuleType ruleType,
      @Param("operator") ComparisonOperator operator,
      @Param("isActive") Boolean isActive,
      Pageable pageable);
}

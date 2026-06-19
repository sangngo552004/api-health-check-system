package com.example.apihealthchecksystem.infrastructure.persistence.repository;

import com.example.apihealthchecksystem.domain.valueobject.CheckType;
import com.example.apihealthchecksystem.domain.valueobject.HttpMethod;
import com.example.apihealthchecksystem.application.port.out.DashboardEndpointView;
import com.example.apihealthchecksystem.application.port.out.EndpointListView;
import com.example.apihealthchecksystem.infrastructure.persistence.entity.MonitoredEndpointJpaEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MonitoredEndpointJpaRepository
    extends JpaRepository<MonitoredEndpointJpaEntity, Long> {
  @Query(
      """
      SELECT e
      FROM MonitoredEndpointJpaEntity e
      WHERE e.workspaceId = :workspaceId
        AND (:search = ''
            OR LOWER(e.name) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(e.url) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(COALESCE(e.environment, ''))
                LIKE LOWER(CONCAT('%', :search, '%')))
        AND (:environment = ''
            OR LOWER(COALESCE(e.environment, '')) = LOWER(:environment))
        AND (:status IS NULL OR e.status = :status)
        AND (:method IS NULL OR e.method = :method)
        AND (:checkType IS NULL OR e.checkType = :checkType)
        AND (:isActive IS NULL OR e.isActive = :isActive)
      """)
  Page<MonitoredEndpointJpaEntity> search(
      @Param("workspaceId") Long workspaceId,
      @Param("search") String search,
      @Param("environment") String environment,
      @Param("status") String status,
      @Param("method") HttpMethod method,
      @Param("checkType") CheckType checkType,
      @Param("isActive") Boolean isActive,
      Pageable pageable);

  @Query(
      value =
          """
          SELECT new com.example.apihealthchecksystem.application.port.out.EndpointListView(
              e.id,
              e.name,
              e.url,
              e.method,
              e.environment,
              e.checkType,
              e.workspaceId,
              e.policyId,
              e.isActive,
              e.status,
          e.createdAt,
          e.updatedAt,
          e.lastCheckedAt,
          e.nextRunAt,
          e.requestBody)
          FROM MonitoredEndpointJpaEntity e
          WHERE e.workspaceId = :workspaceId
            AND (:search = ''
                OR LOWER(e.name) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(e.url) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(COALESCE(e.environment, ''))
                    LIKE LOWER(CONCAT('%', :search, '%')))
            AND (:environment = ''
                OR LOWER(COALESCE(e.environment, '')) = LOWER(:environment))
            AND (:status IS NULL OR e.status = :status)
            AND (:method IS NULL OR e.method = :method)
            AND (:checkType IS NULL OR e.checkType = :checkType)
            AND (:isActive IS NULL OR e.isActive = :isActive)
          """,
      countQuery =
          """
          SELECT COUNT(e)
          FROM MonitoredEndpointJpaEntity e
          WHERE e.workspaceId = :workspaceId
            AND (:search = ''
                OR LOWER(e.name) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(e.url) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(COALESCE(e.environment, ''))
                    LIKE LOWER(CONCAT('%', :search, '%')))
            AND (:environment = ''
                OR LOWER(COALESCE(e.environment, '')) = LOWER(:environment))
            AND (:status IS NULL OR e.status = :status)
            AND (:method IS NULL OR e.method = :method)
            AND (:checkType IS NULL OR e.checkType = :checkType)
            AND (:isActive IS NULL OR e.isActive = :isActive)
          """)
  Page<EndpointListView> searchList(
      @Param("workspaceId") Long workspaceId,
      @Param("search") String search,
      @Param("environment") String environment,
      @Param("status") String status,
      @Param("method") HttpMethod method,
      @Param("checkType") CheckType checkType,
      @Param("isActive") Boolean isActive,
      Pageable pageable);

  @Query(
      value =
          """
          SELECT endpoint_id, tag
          FROM endpoint_tags
          WHERE endpoint_id IN (:endpointIds)
          """,
      nativeQuery = true)
  List<Object[]> findTagsByEndpointIds(@Param("endpointIds") List<Long> endpointIds);

  @Query(
      value =
          """
          SELECT endpoint_id, alert_rule_id
          FROM endpoint_alert_rules
          WHERE endpoint_id IN (:endpointIds)
          """,
      nativeQuery = true)
  List<Object[]> findAlertRuleIdsByEndpointIds(@Param("endpointIds") List<Long> endpointIds);

  @Query(
      value =
          """
          SELECT endpoint_id, header_key, header_value
          FROM endpoint_headers
          WHERE endpoint_id IN (:endpointIds)
          """,
      nativeQuery = true)
  List<Object[]> findHeadersByEndpointIds(@Param("endpointIds") List<Long> endpointIds);

  java.util.List<MonitoredEndpointJpaEntity> findByWorkspaceId(Long workspaceId);

  @Query(
      """
      SELECT new com.example.apihealthchecksystem.application.port.out.DashboardEndpointView(
          e.id,
          e.name,
          e.status,
          e.workspaceId)
      FROM MonitoredEndpointJpaEntity e
      WHERE e.workspaceId = :workspaceId
      """)
  java.util.List<DashboardEndpointView> findDashboardByWorkspaceId(
      @Param("workspaceId") Long workspaceId);

  java.util.List<MonitoredEndpointJpaEntity> findByIsActiveTrue();

  @Query(
      """
      SELECT e
      FROM MonitoredEndpointJpaEntity e
      WHERE e.isActive = TRUE
        AND e.policyId IS NOT NULL
        AND (e.nextRunAt IS NULL OR e.nextRunAt <= :now)
      """)
  java.util.List<MonitoredEndpointJpaEntity> findAllActiveDueForCheck(
      @Param("now") java.time.LocalDateTime now);
}

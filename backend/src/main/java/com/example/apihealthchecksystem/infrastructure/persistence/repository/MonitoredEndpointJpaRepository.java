package com.example.apihealthchecksystem.infrastructure.persistence.repository;

import com.example.apihealthchecksystem.infrastructure.persistence.entity.MonitoredEndpointJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonitoredEndpointJpaRepository
    extends JpaRepository<MonitoredEndpointJpaEntity, Long> {
  Page<MonitoredEndpointJpaEntity> findByWorkspaceId(Long workspaceId, Pageable pageable);

  java.util.List<MonitoredEndpointJpaEntity> findByIsActiveTrue();

  long countByWorkspaceId(Long workspaceId);
}

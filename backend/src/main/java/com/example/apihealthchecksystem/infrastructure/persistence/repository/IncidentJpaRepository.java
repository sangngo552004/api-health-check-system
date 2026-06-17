package com.example.apihealthchecksystem.infrastructure.persistence.repository;

import com.example.apihealthchecksystem.domain.valueobject.IncidentStatus;
import com.example.apihealthchecksystem.infrastructure.persistence.entity.IncidentJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncidentJpaRepository extends JpaRepository<IncidentJpaEntity, Long> {
  List<IncidentJpaEntity> findByEndpointId(Long endpointId);

  List<IncidentJpaEntity> findByWorkspaceId(Long workspaceId);

  Optional<IncidentJpaEntity> findFirstByEndpointIdAndStatusOrderByIdDesc(
      Long endpointId, IncidentStatus status);

  List<IncidentJpaEntity> findByWorkspaceIdAndStatus(Long workspaceId, IncidentStatus status);
}

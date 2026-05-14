package com.example.apihealthchecksystem.infrastructure.persistence.repository;

import com.example.apihealthchecksystem.infrastructure.persistence.entity.HealthCheckResultJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthCheckResultJpaRepository
    extends JpaRepository<HealthCheckResultJpaEntity, Long> {
  List<HealthCheckResultJpaEntity> findByEndpointId(Long endpointId);

  List<HealthCheckResultJpaEntity> findTop10ByEndpointIdOrderByCheckedAtDesc(Long endpointId);
}

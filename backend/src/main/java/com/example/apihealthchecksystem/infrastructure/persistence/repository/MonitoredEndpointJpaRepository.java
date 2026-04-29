package com.example.apihealthchecksystem.infrastructure.persistence.repository;

import com.example.apihealthchecksystem.infrastructure.persistence.entity.MonitoredEndpointJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonitoredEndpointJpaRepository
    extends JpaRepository<MonitoredEndpointJpaEntity, Long> {}

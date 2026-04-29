package com.example.apihealthchecksystem.application.port.out;

import com.example.apihealthchecksystem.domain.model.Incident;
import java.util.List;
import java.util.Optional;

public interface IncidentRepository {
  Incident save(Incident incident);

  Optional<Incident> findById(Long id);

  List<Incident> findByEndpointId(Long endpointId);

  Optional<Incident> findOpenIncidentByEndpointId(Long endpointId);
}

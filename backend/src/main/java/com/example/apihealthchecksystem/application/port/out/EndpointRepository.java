package com.example.apihealthchecksystem.application.port.out;

import com.example.apihealthchecksystem.domain.model.MonitoredEndpoint;
import java.util.List;
import java.util.Optional;

public interface EndpointRepository {
  MonitoredEndpoint save(MonitoredEndpoint endpoint);

  Optional<MonitoredEndpoint> findById(Long id);

  List<MonitoredEndpoint> findAll();

  void deleteById(Long id);
}

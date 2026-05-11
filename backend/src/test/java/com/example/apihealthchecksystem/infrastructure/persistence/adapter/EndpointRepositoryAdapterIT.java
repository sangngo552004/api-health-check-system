package com.example.apihealthchecksystem.infrastructure.persistence.adapter;

import static org.junit.jupiter.api.Assertions.*;

import com.example.apihealthchecksystem.domain.model.MonitoredEndpoint;
import com.example.apihealthchecksystem.domain.valueobject.CheckType;
import com.example.apihealthchecksystem.domain.valueobject.HttpMethod;
import com.example.apihealthchecksystem.infrastructure.config.JpaAuditingConfig;
import com.example.apihealthchecksystem.infrastructure.persistence.mapper.EndpointMapperImpl;
import com.example.apihealthchecksystem.infrastructure.persistence.repository.MonitoredEndpointJpaRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({EndpointMapperImpl.class, JpaAuditingConfig.class})
class EndpointRepositoryAdapterIT {

  @Autowired private MonitoredEndpointJpaRepository jpaRepository;
  @Autowired private EndpointMapperImpl mapper;

  private EndpointRepositoryAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new EndpointRepositoryAdapter(jpaRepository, mapper);
  }

  @Test
  void save_shouldPersistEndpoint() {
    MonitoredEndpoint endpoint =
        MonitoredEndpoint.builder()
            .name("Test API")
            .url("http://test.com")
            .method(HttpMethod.GET)
            .workspaceId(1L)
            .checkType(CheckType.HTTP)
            .isActive(true)
            .environment("PROD")
            .build();

    MonitoredEndpoint saved = adapter.save(endpoint);

    assertNotNull(saved.getId());
    assertEquals("Test API", saved.getName());

    Optional<MonitoredEndpoint> found = adapter.findById(saved.getId());
    assertTrue(found.isPresent());
  }

  @Test
  void findByWorkspaceId_shouldReturnPagedResults() {
    Long wsId = 1L;
    adapter.save(
        MonitoredEndpoint.builder()
            .name("API 1")
            .url("U1")
            .workspaceId(wsId)
            .checkType(CheckType.HTTP)
            .method(HttpMethod.GET)
            .build());
    adapter.save(
        MonitoredEndpoint.builder()
            .name("API 2")
            .url("U2")
            .workspaceId(wsId)
            .checkType(CheckType.HTTP)
            .method(HttpMethod.GET)
            .build());
    adapter.save(
        MonitoredEndpoint.builder()
            .name("API 3")
            .url("U3")
            .workspaceId(2L)
            .checkType(CheckType.HTTP)
            .method(HttpMethod.GET)
            .build());

    List<MonitoredEndpoint> results = adapter.findByWorkspaceId(wsId, 0, 10);

    assertEquals(2, results.size());
    assertEquals(2, adapter.countByWorkspaceId(wsId));
  }
}

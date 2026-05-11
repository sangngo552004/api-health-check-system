package com.example.apihealthchecksystem.infrastructure.persistence.adapter;

import static org.junit.jupiter.api.Assertions.*;

import com.example.apihealthchecksystem.domain.model.CheckPolicy;
import com.example.apihealthchecksystem.infrastructure.config.JpaAuditingConfig;
import com.example.apihealthchecksystem.infrastructure.persistence.mapper.CheckPolicyMapperImpl;
import com.example.apihealthchecksystem.infrastructure.persistence.repository.CheckPolicyJpaRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({CheckPolicyMapperImpl.class, JpaAuditingConfig.class})
class CheckPolicyRepositoryAdapterIT {

  @Autowired private CheckPolicyJpaRepository jpaRepository;
  @Autowired private CheckPolicyMapperImpl mapper;

  private CheckPolicyRepositoryAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new CheckPolicyRepositoryAdapter(jpaRepository, mapper);
  }

  @Test
  void save_shouldPersistPolicy() {
    CheckPolicy policy =
        CheckPolicy.builder()
            .name("Strict Policy")
            .intervalSeconds(30)
            .timeoutMillis(5000)
            .retryCount(3)
            .failureThreshold(2)
            .workspaceId(1L)
            .build();

    CheckPolicy saved = adapter.save(policy);

    assertNotNull(saved.getId());

    Optional<CheckPolicy> found = adapter.findById(saved.getId());
    assertTrue(found.isPresent());
    assertEquals(30, found.get().getIntervalSeconds());
  }

  @Test
  void findByWorkspaceId_shouldReturnResults() {
    Long wsId = 1L;
    adapter.save(
        CheckPolicy.builder()
            .name("P1")
            .workspaceId(wsId)
            .intervalSeconds(60)
            .timeoutMillis(1000)
            .retryCount(1)
            .failureThreshold(1)
            .build());
    adapter.save(
        CheckPolicy.builder()
            .name("P2")
            .workspaceId(2L)
            .intervalSeconds(60)
            .timeoutMillis(1000)
            .retryCount(1)
            .failureThreshold(1)
            .build());

    List<CheckPolicy> results = adapter.findByWorkspaceId(wsId, 0, 10);
    assertEquals(1, results.size());
  }
}

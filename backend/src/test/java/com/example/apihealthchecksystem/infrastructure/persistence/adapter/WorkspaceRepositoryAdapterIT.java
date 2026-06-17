package com.example.apihealthchecksystem.infrastructure.persistence.adapter;

import static org.junit.jupiter.api.Assertions.*;

import com.example.apihealthchecksystem.domain.model.Workspace;
import com.example.apihealthchecksystem.infrastructure.config.JpaAuditingConfig;
import com.example.apihealthchecksystem.infrastructure.persistence.mapper.WorkspaceMapperImpl;
import com.example.apihealthchecksystem.infrastructure.persistence.repository.WorkspaceJpaRepository;
import com.example.apihealthchecksystem.infrastructure.persistence.repository.WorkspaceMemberJpaRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({WorkspaceMapperImpl.class, JpaAuditingConfig.class})
class WorkspaceRepositoryAdapterIT {

  @Autowired private WorkspaceJpaRepository jpaRepository;
  @Autowired private WorkspaceMemberJpaRepository memberJpaRepository;

  @Autowired private WorkspaceMapperImpl mapper;

  private WorkspaceRepositoryAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new WorkspaceRepositoryAdapter(jpaRepository, memberJpaRepository, mapper);
  }

  @Test
  void save_shouldPersistWorkspace() {
    Workspace workspace =
        Workspace.builder().name("Test Workspace").slug("test-ws").ownerId(1L).build();

    Workspace saved = adapter.save(workspace);

    assertNotNull(saved.getId());
    assertEquals("Test Workspace", saved.getName());

    Optional<Workspace> found = adapter.findById(saved.getId());
    assertTrue(found.isPresent());
    assertEquals("test-ws", found.get().getSlug());
  }

  @Test
  void addMember_shouldPersistMembership() {
    Workspace ws = adapter.save(Workspace.builder().name("WS").slug("ws").ownerId(1L).build());
    Long userId = 2L;

    adapter.addMember(ws.getId(), userId);

    assertEquals(1, adapter.getMembers(ws.getId()).size());
  }

  @Test
  void findByUserId_shouldReturnWorkspacesForUser() {
    Workspace ws1 = adapter.save(Workspace.builder().name("WS1").slug("ws1").ownerId(1L).build());
    Workspace ws2 = adapter.save(Workspace.builder().name("WS2").slug("ws2").ownerId(1L).build());
    Long userId = 10L;

    adapter.addMember(ws1.getId(), userId);
    adapter.addMember(ws2.getId(), userId);

    List<Workspace> workspaces = adapter.findByUserId(userId);

    assertEquals(2, workspaces.size());
  }
}

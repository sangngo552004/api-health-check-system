package com.example.apihealthchecksystem.infrastructure.persistence.adapter;

import static org.junit.jupiter.api.Assertions.*;

import com.example.apihealthchecksystem.domain.model.ContactGroup;
import com.example.apihealthchecksystem.infrastructure.config.JpaAuditingConfig;
import com.example.apihealthchecksystem.infrastructure.persistence.mapper.ContactGroupMapperImpl;
import com.example.apihealthchecksystem.infrastructure.persistence.repository.ContactGroupJpaRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({ContactGroupMapperImpl.class, JpaAuditingConfig.class})
class ContactGroupRepositoryAdapterIT {

  @Autowired private ContactGroupJpaRepository jpaRepository;
  @Autowired private ContactGroupMapperImpl mapper;

  private ContactGroupRepositoryAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new ContactGroupRepositoryAdapter(jpaRepository, mapper);
  }

  @Test
  void save_shouldPersistGroup() {
    ContactGroup group =
        ContactGroup.builder()
            .name("SRE Team")
            .emailAddresses(List.of("sre@company.com"))
            .workspaceId(1L)
            .build();

    ContactGroup saved = adapter.save(group);

    assertNotNull(saved.getId());

    Optional<ContactGroup> found = adapter.findById(saved.getId());
    assertTrue(found.isPresent());
    assertTrue(found.get().getEmailAddresses().contains("sre@company.com"));
  }

  @Test
  void findByWorkspaceId_shouldReturnResults() {
    Long wsId = 1L;
    adapter.save(
        ContactGroup.builder()
            .name("G1")
            .workspaceId(wsId)
            .emailAddresses(List.of("t1@t.com"))
            .build());

    List<ContactGroup> results = adapter.findByWorkspaceId(wsId, 0, 10);
    assertEquals(1, results.size());
  }
}

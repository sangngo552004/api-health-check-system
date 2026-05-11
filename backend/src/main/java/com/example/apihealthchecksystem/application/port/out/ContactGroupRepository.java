package com.example.apihealthchecksystem.application.port.out;

import com.example.apihealthchecksystem.domain.model.ContactGroup;
import java.util.List;
import java.util.Optional;

public interface ContactGroupRepository {
  ContactGroup save(ContactGroup group);

  Optional<ContactGroup> findById(Long id);

  List<ContactGroup> findAll();

  List<ContactGroup> findByWorkspaceId(Long workspaceId, int page, int size);

  long countByWorkspaceId(Long workspaceId);

  void deleteById(Long id);
}

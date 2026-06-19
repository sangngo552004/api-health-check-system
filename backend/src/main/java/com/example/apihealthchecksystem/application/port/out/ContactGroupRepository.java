package com.example.apihealthchecksystem.application.port.out;

import com.example.apihealthchecksystem.application.dto.response.PageResult;
import com.example.apihealthchecksystem.domain.model.ContactGroup;
import java.util.List;
import java.util.Optional;

public interface ContactGroupRepository {
  ContactGroup save(ContactGroup group);

  Optional<ContactGroup> findById(Long id);

  List<ContactGroup> findAllByIds(List<Long> ids);

  List<ContactGroup> findAll();

  PageResult<ContactGroup> searchByWorkspace(
      Long workspaceId,
      String search,
      Boolean isActive,
      int page,
      int size,
      String sortBy,
      String sortDir);

  void deleteById(Long id);
}

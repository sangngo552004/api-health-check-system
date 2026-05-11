package com.example.apihealthchecksystem.application.port.out;

import com.example.apihealthchecksystem.domain.model.CheckPolicy;
import java.util.List;
import java.util.Optional;

public interface CheckPolicyRepository {
  CheckPolicy save(CheckPolicy policy);

  Optional<CheckPolicy> findById(Long id);

  List<CheckPolicy> findAll();

  List<CheckPolicy> findByWorkspaceId(Long workspaceId, int page, int size);

  long countByWorkspaceId(Long workspaceId);

  void deleteById(Long id);
}

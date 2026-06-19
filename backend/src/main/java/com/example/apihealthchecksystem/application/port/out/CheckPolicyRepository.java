package com.example.apihealthchecksystem.application.port.out;

import com.example.apihealthchecksystem.application.dto.response.PageResult;
import com.example.apihealthchecksystem.domain.model.CheckPolicy;
import java.util.List;
import java.util.Optional;

public interface CheckPolicyRepository {
  CheckPolicy save(CheckPolicy policy);

  Optional<CheckPolicy> findById(Long id);

  List<CheckPolicy> findAllByIds(List<Long> ids);

  List<CheckPolicy> findAll();

  PageResult<CheckPolicy> searchByWorkspace(
      Long workspaceId,
      String search,
      Integer expectedStatusCode,
      Boolean hasDegradedResponseTimeThreshold,
      int page,
      int size,
      String sortBy,
      String sortDir);

  void deleteById(Long id);
}

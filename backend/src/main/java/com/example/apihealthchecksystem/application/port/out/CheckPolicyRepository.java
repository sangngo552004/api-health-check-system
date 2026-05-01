package com.example.apihealthchecksystem.application.port.out;

import com.example.apihealthchecksystem.domain.model.CheckPolicy;
import java.util.Optional;

public interface CheckPolicyRepository {
  CheckPolicy save(CheckPolicy policy);

  Optional<CheckPolicy> findById(Long id);

  void deleteById(Long id);
}

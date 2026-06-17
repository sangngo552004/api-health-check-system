package com.example.apihealthchecksystem.application.port.out;

import com.example.apihealthchecksystem.application.dto.response.PageResult;
import com.example.apihealthchecksystem.domain.model.User;
import com.example.apihealthchecksystem.domain.valueobject.UserRole;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
  List<User> findAll();

  PageResult<User> search(
      String search,
      UserRole role,
      Boolean isActive,
      int page,
      int size,
      String sortBy,
      String sortDir);

  Optional<User> findById(Long id);

  Optional<User> findByUsername(String username);

  Optional<User> findByEmail(String email);

  List<User> findAllByIds(List<Long> ids);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  User save(User user);

  void deleteById(Long id);
}

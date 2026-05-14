package com.example.apihealthchecksystem.application.port.out;

import com.example.apihealthchecksystem.domain.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
  Optional<User> findById(Long id);

  Optional<User> findByUsername(String username);

  List<User> findAllByIds(List<Long> ids);

  User save(User user);
}

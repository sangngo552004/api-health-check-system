package com.example.apihealthchecksystem.infrastructure.security;

import com.example.apihealthchecksystem.infrastructure.persistence.entity.UserJpaEntity;
import com.example.apihealthchecksystem.infrastructure.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserJpaRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserJpaEntity user =
        userRepository
            .findByUsername(username)
            .orElseThrow(
                () -> new UsernameNotFoundException("User not found with username: " + username));

    if (user.getIsActive() != null && !user.getIsActive()) {
      throw new UsernameNotFoundException("User is deactivated: " + username);
    }

    return new CustomUserDetails(user);
  }

  public UserDetails loadUserById(Long id) {
    UserJpaEntity user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

    if (user.getIsActive() != null && !user.getIsActive()) {
      throw new UsernameNotFoundException("User is deactivated with id: " + id);
    }

    return new CustomUserDetails(user);
  }
}

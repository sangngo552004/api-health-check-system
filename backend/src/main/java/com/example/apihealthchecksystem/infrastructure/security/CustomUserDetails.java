package com.example.apihealthchecksystem.infrastructure.security;

import com.example.apihealthchecksystem.infrastructure.persistence.entity.UserJpaEntity;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class CustomUserDetails implements UserDetails {

  private final Long id;
  private final String username;
  private final String password;
  private final boolean requiresPasswordChange;
  private final Collection<? extends GrantedAuthority> authorities;

  public CustomUserDetails(UserJpaEntity user) {
    this.id = user.getId();
    this.username = user.getUsername();
    this.password = user.getPasswordHash();
    this.requiresPasswordChange = user.getRequiresPasswordChange();
    // Prefix "ROLE_" is standard for Spring Security role checks
    this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}

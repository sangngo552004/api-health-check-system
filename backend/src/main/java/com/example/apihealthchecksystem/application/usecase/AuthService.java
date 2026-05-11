package com.example.apihealthchecksystem.application.usecase;

import com.example.apihealthchecksystem.application.dto.request.LoginRequest;
import com.example.apihealthchecksystem.application.dto.request.TokenRefreshRequest;
import com.example.apihealthchecksystem.application.dto.response.LoginResponse;
import com.example.apihealthchecksystem.application.port.in.AuthUseCase;
import com.example.apihealthchecksystem.application.port.out.AuthenticationPort;

public class AuthService implements AuthUseCase {

  private final AuthenticationPort authenticationPort;

  public AuthService(AuthenticationPort authenticationPort) {
    this.authenticationPort = authenticationPort;
  }

  @Override
  public LoginResponse login(LoginRequest request) {
    return authenticationPort.authenticate(request.username(), request.password());
  }

  @Override
  public LoginResponse refreshToken(TokenRefreshRequest request) {
    return authenticationPort.refresh(request.refreshToken());
  }
}

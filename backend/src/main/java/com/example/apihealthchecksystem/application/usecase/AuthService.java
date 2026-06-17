package com.example.apihealthchecksystem.application.usecase;

import com.example.apihealthchecksystem.application.dto.request.LoginRequest;
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
  public LoginResponse refreshToken(String refreshToken) {
    return authenticationPort.refresh(refreshToken);
  }

  @Override
  public void logout(String refreshToken) {
    authenticationPort.logout(refreshToken);
  }
}

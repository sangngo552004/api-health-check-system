package com.example.apihealthchecksystem.application.port.in;

import com.example.apihealthchecksystem.application.dto.request.LoginRequest;
import com.example.apihealthchecksystem.application.dto.request.TokenRefreshRequest;
import com.example.apihealthchecksystem.application.dto.response.LoginResponse;

public interface AuthUseCase {
  LoginResponse login(LoginRequest request);

  LoginResponse refreshToken(TokenRefreshRequest request);
}

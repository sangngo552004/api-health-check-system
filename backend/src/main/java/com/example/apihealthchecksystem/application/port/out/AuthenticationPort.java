package com.example.apihealthchecksystem.application.port.out;

import com.example.apihealthchecksystem.application.dto.response.LoginResponse;

public interface AuthenticationPort {
  LoginResponse authenticate(String username, String password);

  LoginResponse refresh(String refreshToken);
}

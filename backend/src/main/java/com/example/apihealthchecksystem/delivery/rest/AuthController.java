package com.example.apihealthchecksystem.delivery.rest;

import com.example.apihealthchecksystem.application.dto.request.LoginRequest;
import com.example.apihealthchecksystem.application.dto.request.TokenRefreshRequest;
import com.example.apihealthchecksystem.application.dto.response.LoginResponse;
import com.example.apihealthchecksystem.application.port.in.AuthUseCase;
import com.example.apihealthchecksystem.delivery.rest.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthUseCase authUseCase;

  @PostMapping("/login")
  public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
    return ApiResponse.success(authUseCase.login(request));
  }

  @PostMapping("/refresh")
  public ApiResponse<LoginResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
    return ApiResponse.success(authUseCase.refreshToken(request));
  }
}

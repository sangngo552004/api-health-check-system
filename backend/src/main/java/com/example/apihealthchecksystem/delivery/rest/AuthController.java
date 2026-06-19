package com.example.apihealthchecksystem.delivery.rest;

import com.example.apihealthchecksystem.application.dto.request.LoginRequest;
import com.example.apihealthchecksystem.application.dto.response.LoginResponse;
import com.example.apihealthchecksystem.application.exception.AppErrorCode;
import com.example.apihealthchecksystem.application.exception.UnauthorizedException;
import com.example.apihealthchecksystem.application.port.in.AuthUseCase;
import com.example.apihealthchecksystem.delivery.rest.common.ApiResponse;
import com.example.apihealthchecksystem.delivery.rest.common.security.RefreshTokenCookieService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthUseCase authUseCase;
  private final RefreshTokenCookieService refreshTokenCookieService;

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<LoginResponse>> login(
      @Valid @RequestBody LoginRequest request) {
    LoginResponse response = authUseCase.login(request);
    return ResponseEntity.ok()
        .header(
            HttpHeaders.SET_COOKIE,
            refreshTokenCookieService.buildSetCookieHeader(response.refreshToken()))
        .body(
            ApiResponse.success(
                new LoginResponse(
                    response.accessToken(),
                    null,
                    response.role(),
                    response.requiresPasswordChange())));
  }

  @PostMapping("/refresh")
  public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(HttpServletRequest request) {
    String refreshToken = refreshTokenCookieService.extractRefreshToken(request);
    if (refreshToken == null || refreshToken.isBlank()) {
      throw new UnauthorizedException(AppErrorCode.REFRESH_TOKEN_MISSING);
    }

    LoginResponse response = authUseCase.refreshToken(refreshToken);

    return ResponseEntity.ok()
        .header(
            HttpHeaders.SET_COOKIE,
            refreshTokenCookieService.buildSetCookieHeader(response.refreshToken()))
        .body(
            ApiResponse.success(
                new LoginResponse(
                    response.accessToken(),
                    null,
                    response.role(),
                    response.requiresPasswordChange())));
  }

  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
    authUseCase.logout(refreshTokenCookieService.extractRefreshToken(request));
    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, refreshTokenCookieService.buildClearCookieHeader())
        .body(ApiResponse.success(null));
  }
}

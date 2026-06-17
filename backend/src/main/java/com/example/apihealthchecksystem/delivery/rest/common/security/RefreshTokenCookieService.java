package com.example.apihealthchecksystem.delivery.rest.common.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenCookieService {

  @Value("${app.jwt.refresh-cookie.name:refresh_token}")
  private String cookieName;

  @Value("${app.jwt.refresh-cookie.path:/api/v1/auth}")
  private String cookiePath;

  @Value("${app.jwt.refresh-cookie.secure:false}")
  private boolean secureCookie;

  @Value("${app.jwt.refresh-cookie.same-site:Lax}")
  private String sameSite;

  @Value("${app.jwt.refresh-expiration-ms:86400000}")
  private long refreshExpirationMs;

  public String buildSetCookieHeader(String refreshToken) {
    return ResponseCookie.from(cookieName, refreshToken)
        .httpOnly(true)
        .secure(secureCookie)
        .sameSite(sameSite)
        .path(cookiePath)
        .maxAge(refreshExpirationMs / 1000)
        .build()
        .toString();
  }

  public String buildClearCookieHeader() {
    return ResponseCookie.from(cookieName, "")
        .httpOnly(true)
        .secure(secureCookie)
        .sameSite(sameSite)
        .path(cookiePath)
        .maxAge(0)
        .build()
        .toString();
  }

  public String extractRefreshToken(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies == null) {
      return null;
    }

    for (Cookie cookie : cookies) {
      if (cookieName.equals(cookie.getName())) {
        return cookie.getValue();
      }
    }

    return null;
  }
}

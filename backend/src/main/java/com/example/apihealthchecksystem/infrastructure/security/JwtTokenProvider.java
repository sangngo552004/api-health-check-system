package com.example.apihealthchecksystem.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  @Value(
      "${app.jwt.secret:ZGV2LWFwaS1oZWFsdGgtY2hlY2stc3lzdGVtLWp3dC1zZWNyZXQta2V5LTEyMzQ1Njc4OTA=}")
  private String jwtSecret;

  @Value("${app.jwt.expiration-ms:3600000}") // 1 hour
  private int jwtExpirationMs;

  @Value("${app.jwt.refresh-expiration-ms:86400000}") // 24 hours
  private int refreshExpirationMs;

  private SecretKey getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String generateAccessToken(Authentication authentication) {
    CustomUserDetails userPrincipal = (CustomUserDetails) authentication.getPrincipal();

    return Jwts.builder()
        .subject(Long.toString(userPrincipal.getId()))
        .claim("username", userPrincipal.getUsername())
        .claim("requiresPasswordChange", userPrincipal.isRequiresPasswordChange())
        .claim("role", userPrincipal.getRole())
        .issuedAt(new Date())
        .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
        .signWith(getSigningKey())
        .compact();
  }

  public String generateRefreshToken(Long userId) {
    return Jwts.builder()
        .id(UUID.randomUUID().toString())
        .subject(Long.toString(userId))
        .issuedAt(new Date())
        .expiration(new Date((new Date()).getTime() + refreshExpirationMs))
        .signWith(getSigningKey())
        .compact();
  }

  public Long getUserIdFromJwt(String token) {
    Claims claims =
        Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();

    return Long.parseLong(claims.getSubject());
  }

  public boolean validateToken(String authToken) {
    try {
      Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(authToken);
      return true;
    } catch (SignatureException ex) {
      // Invalid JWT signature
    } catch (MalformedJwtException ex) {
      // Invalid JWT token
    } catch (ExpiredJwtException ex) {
      // Expired JWT token
    } catch (UnsupportedJwtException ex) {
      // Unsupported JWT token
    } catch (IllegalArgumentException ex) {
      // JWT claims string is empty
    }
    return false;
  }
}

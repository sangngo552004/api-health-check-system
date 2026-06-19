package com.example.apihealthchecksystem.infrastructure.security;

import com.example.apihealthchecksystem.application.dto.response.LoginResponse;
import com.example.apihealthchecksystem.application.exception.AppErrorCode;
import com.example.apihealthchecksystem.application.exception.UnauthorizedException;
import com.example.apihealthchecksystem.application.port.out.AuthenticationPort;
import com.example.apihealthchecksystem.infrastructure.persistence.entity.RefreshTokenJpaEntity;
import com.example.apihealthchecksystem.infrastructure.persistence.entity.UserJpaEntity;
import com.example.apihealthchecksystem.infrastructure.persistence.repository.RefreshTokenJpaRepository;
import com.example.apihealthchecksystem.infrastructure.persistence.repository.UserJpaRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AuthenticationAdapter implements AuthenticationPort {

  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider tokenProvider;
  private final RefreshTokenJpaRepository refreshTokenRepository;
  private final UserJpaRepository userRepository;

  @Override
  @Transactional
  public LoginResponse authenticate(String username, String password) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password));

    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    Long userId = userDetails.getId();

    String accessToken = tokenProvider.generateAccessToken(authentication);
    String refreshToken = tokenProvider.generateRefreshToken(userId);

    // Xóa các refresh token cũ của user này để mỗi thiết bị có 1 phiên duy nhất (hoặc giữ lại tùy
    // business)
    // Để đơn giản và bảo mật, khi login mới, ta xóa token cũ
    refreshTokenRepository.deleteByUserId(userId);

    UserJpaEntity user = userRepository.findById(userId).orElseThrow();

    // Lưu refresh token mới
    RefreshTokenJpaEntity refreshTokenEntity =
        RefreshTokenJpaEntity.builder()
            .user(user)
            .token(refreshToken)
            .expiryDate(LocalDateTime.now().plusDays(1)) // 24 hours
            .build();
    refreshTokenRepository.save(refreshTokenEntity);

    return new LoginResponse(
        accessToken, refreshToken, user.getRole().name(), user.getRequiresPasswordChange());
  }

  @Override
  @Transactional
  public LoginResponse refresh(String refreshToken) {
    if (!tokenProvider.validateToken(refreshToken)) {
      throw new UnauthorizedException(AppErrorCode.REFRESH_TOKEN_INVALID);
    }

    RefreshTokenJpaEntity refreshTokenEntity =
        refreshTokenRepository
            .findByToken(refreshToken)
            .orElseThrow(
                () ->
                    new UnauthorizedException(AppErrorCode.REFRESH_TOKEN_NOT_FOUND));

    if (refreshTokenEntity.getExpiryDate().isBefore(LocalDateTime.now())) {
      refreshTokenRepository.delete(refreshTokenEntity);
      throw new UnauthorizedException(AppErrorCode.REFRESH_TOKEN_EXPIRED);
    }

    UserJpaEntity user = refreshTokenEntity.getUser();
    Long userId = user.getId();

    // Sinh accessToken giả lập Authentication
    CustomUserDetails userDetails = new CustomUserDetails(user);
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

    String newAccessToken = tokenProvider.generateAccessToken(authentication);
    String newRefreshToken = tokenProvider.generateRefreshToken(userId);

    // Refresh Token Rotation: Xóa token cũ, tạo token mới
    refreshTokenRepository.delete(refreshTokenEntity);

    RefreshTokenJpaEntity newRefreshTokenEntity =
        RefreshTokenJpaEntity.builder()
            .user(user)
            .token(newRefreshToken)
            .expiryDate(LocalDateTime.now().plusDays(1)) // 24 hours
            .build();
    refreshTokenRepository.save(newRefreshTokenEntity);

    return new LoginResponse(
        newAccessToken, newRefreshToken, user.getRole().name(), user.getRequiresPasswordChange());
  }

  @Override
  @Transactional
  public void logout(String refreshToken) {
    if (refreshToken == null || refreshToken.isBlank()) {
      return;
    }

    refreshTokenRepository.deleteByToken(refreshToken);
  }
}

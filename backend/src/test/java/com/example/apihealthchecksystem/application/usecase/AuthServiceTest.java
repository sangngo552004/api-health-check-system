package com.example.apihealthchecksystem.application.usecase;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.apihealthchecksystem.application.dto.request.LoginRequest;
import com.example.apihealthchecksystem.application.dto.request.TokenRefreshRequest;
import com.example.apihealthchecksystem.application.dto.response.LoginResponse;
import com.example.apihealthchecksystem.application.port.out.AuthenticationPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock private AuthenticationPort authenticationPort;

  @InjectMocks private AuthService authService;

  @Test
  void login_shouldDelegateToAuthenticationPort() {
    LoginRequest request = new LoginRequest("alice", "secret");
    LoginResponse expected = new LoginResponse("access", "refresh", "ADMIN", false);
    when(authenticationPort.authenticate("alice", "secret")).thenReturn(expected);

    LoginResponse actual = authService.login(request);

    assertSame(expected, actual);
    verify(authenticationPort).authenticate("alice", "secret");
  }

  @Test
  void refreshToken_shouldDelegateToAuthenticationPort() {
    TokenRefreshRequest request = new TokenRefreshRequest("refresh-token");
    LoginResponse expected = new LoginResponse("new-access", "new-refresh", "ADMIN", false);
    when(authenticationPort.refresh("refresh-token")).thenReturn(expected);

    LoginResponse actual = authService.refreshToken(request);

    assertSame(expected, actual);
    verify(authenticationPort).refresh("refresh-token");
  }
}

package com.example.apihealthchecksystem.domain.service;

import static org.junit.jupiter.api.Assertions.*;

import com.example.apihealthchecksystem.domain.model.CheckPolicy;
import com.example.apihealthchecksystem.domain.model.HealthCheckResult;
import com.example.apihealthchecksystem.domain.model.Incident;
import com.example.apihealthchecksystem.domain.valueobject.CheckStatus;
import com.example.apihealthchecksystem.domain.valueobject.IncidentStatus;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("IncidentEvaluationService")
class IncidentEvaluationServiceTest {

  private IncidentEvaluationService service;
  private CheckPolicy policy;

  @BeforeEach
  void setUp() {
    service = new IncidentEvaluationService();
    policy = CheckPolicy.builder().failureThreshold(3).build();
  }

  // ─── shouldOpenIncident ───────────────────────────────────────────────────

  @Nested
  @DisplayName("shouldOpenIncident")
  class ShouldOpenIncident {

    @Test
    @DisplayName("Trả về false khi list rỗng")
    void returnsFalse_whenResultsEmpty() {
      assertFalse(service.shouldOpenIncident(Collections.emptyList(), policy));
    }

    @Test
    @DisplayName("Trả về false khi list null")
    void returnsFalse_whenResultsNull() {
      assertFalse(service.shouldOpenIncident(null, policy));
    }

    @Test
    @DisplayName("Trả về false khi số kết quả ít hơn threshold")
    void returnsFalse_whenNotEnoughResults() {
      List<HealthCheckResult> results =
          List.of(buildResult(CheckStatus.DOWN), buildResult(CheckStatus.DOWN));
      // threshold = 3, chỉ có 2 kết quả
      assertFalse(service.shouldOpenIncident(results, policy));
    }

    @Test
    @DisplayName("Trả về true khi đủ lần DOWN liên tiếp bằng threshold")
    void returnsTrue_whenConsecutiveDownEqualsThreshold() {
      List<HealthCheckResult> results =
          List.of(
              buildResult(CheckStatus.DOWN),
              buildResult(CheckStatus.DOWN),
              buildResult(CheckStatus.DOWN));
      assertTrue(service.shouldOpenIncident(results, policy));
    }

    @Test
    @DisplayName("Trả về true khi lẫn cả DEGRADED và DOWN liên tiếp")
    void returnsTrue_whenMixedDegradedAndDown() {
      List<HealthCheckResult> results =
          List.of(
              buildResult(CheckStatus.DEGRADED),
              buildResult(CheckStatus.DOWN),
              buildResult(CheckStatus.DEGRADED),
              buildResult(CheckStatus.UP) // kết quả cũ hơn, không ảnh hưởng
              );
      assertTrue(service.shouldOpenIncident(results, policy));
    }

    @Test
    @DisplayName("Trả về false khi có 1 UP xen vào đầu danh sách")
    void returnsFalse_whenLatestIsUp() {
      List<HealthCheckResult> results =
          List.of(
              buildResult(CheckStatus.UP), // mới nhất
              buildResult(CheckStatus.DOWN),
              buildResult(CheckStatus.DOWN));
      assertFalse(service.shouldOpenIncident(results, policy));
    }

    @Test
    @DisplayName("Dùng failureThreshold = 1 từ policy")
    void usesThresholdFromPolicy() {
      CheckPolicy strictPolicy = CheckPolicy.builder().failureThreshold(1).build();
      List<HealthCheckResult> results = List.of(buildResult(CheckStatus.DOWN));
      assertTrue(service.shouldOpenIncident(results, strictPolicy));
    }

    @Test
    @DisplayName("Dùng threshold mặc định = 3 khi policy.failureThreshold là null")
    void usesDefaultThreshold_whenPolicyThresholdIsNull() {
      CheckPolicy noThresholdPolicy = CheckPolicy.builder().build(); // failureThreshold = null
      List<HealthCheckResult> twoDowns =
          List.of(buildResult(CheckStatus.DOWN), buildResult(CheckStatus.DOWN));
      // default threshold = 3, chỉ có 2 → false
      assertFalse(service.shouldOpenIncident(twoDowns, noThresholdPolicy));
    }
  }

  // ─── shouldResolveIncident ────────────────────────────────────────────────

  @Nested
  @DisplayName("shouldResolveIncident")
  class ShouldResolveIncident {

    @Test
    @DisplayName("Trả về true khi latestResult là UP và incident đang OPEN")
    void returnsTrue_whenUpAndIncidentOpen() {
      HealthCheckResult upResult = buildResult(CheckStatus.UP);
      Incident openIncident = buildIncident(IncidentStatus.OPEN);
      assertTrue(service.shouldResolveIncident(upResult, openIncident));
    }

    @Test
    @DisplayName("Trả về false khi latestResult là DOWN")
    void returnsFalse_whenDown() {
      HealthCheckResult downResult = buildResult(CheckStatus.DOWN);
      Incident openIncident = buildIncident(IncidentStatus.OPEN);
      assertFalse(service.shouldResolveIncident(downResult, openIncident));
    }

    @Test
    @DisplayName("Trả về false khi incident đã RESOLVED")
    void returnsFalse_whenIncidentAlreadyResolved() {
      HealthCheckResult upResult = buildResult(CheckStatus.UP);
      Incident resolvedIncident = buildIncident(IncidentStatus.RESOLVED);
      assertFalse(service.shouldResolveIncident(upResult, resolvedIncident));
    }

    @Test
    @DisplayName("Trả về false khi latestResult là null")
    void returnsFalse_whenResultNull() {
      Incident openIncident = buildIncident(IncidentStatus.OPEN);
      assertFalse(service.shouldResolveIncident(null, openIncident));
    }

    @Test
    @DisplayName("Trả về false khi incident là null")
    void returnsFalse_whenIncidentNull() {
      HealthCheckResult upResult = buildResult(CheckStatus.UP);
      assertFalse(service.shouldResolveIncident(upResult, null));
    }
  }

  // ─── Helpers ─────────────────────────────────────────────────────────────

  private HealthCheckResult buildResult(CheckStatus status) {
    return HealthCheckResult.builder()
        .endpointId(1L)
        .checkedAt(LocalDateTime.now())
        .status(status)
        .success(CheckStatus.UP.equals(status))
        .build();
  }

  private Incident buildIncident(IncidentStatus status) {
    return Incident.builder()
        .id(1L)
        .endpointId(1L)
        .status(status)
        .startedAt(LocalDateTime.now().minusMinutes(10))
        .build();
  }
}

package com.example.apihealthchecksystem.application.usecase;

import com.example.apihealthchecksystem.application.port.in.MonitorEndpointUseCase;
import com.example.apihealthchecksystem.application.port.out.CheckPolicyRepository;
import com.example.apihealthchecksystem.application.port.out.EndpointRepository;
import com.example.apihealthchecksystem.application.port.out.HealthCheckExecutor;
import com.example.apihealthchecksystem.application.port.out.HealthCheckResultRepository;
import com.example.apihealthchecksystem.application.port.out.IncidentRepository;
import com.example.apihealthchecksystem.domain.event.EndpointCheckedEvent;
import com.example.apihealthchecksystem.domain.event.IncidentOpenedEvent;
import com.example.apihealthchecksystem.domain.event.IncidentResolvedEvent;
import com.example.apihealthchecksystem.domain.model.CheckPolicy;
import com.example.apihealthchecksystem.domain.model.HealthCheckResult;
import com.example.apihealthchecksystem.domain.model.Incident;
import com.example.apihealthchecksystem.domain.model.MonitoredEndpoint;
import com.example.apihealthchecksystem.domain.service.IncidentAnalyzer;
import com.example.apihealthchecksystem.domain.valueobject.CheckStatus;
import com.example.apihealthchecksystem.domain.valueobject.EndpointStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

@RequiredArgsConstructor
@Slf4j
public class MonitorEndpointService implements MonitorEndpointUseCase {

  private final EndpointRepository endpointRepository;
  private final CheckPolicyRepository checkPolicyRepository;
  private final List<HealthCheckExecutor> executors;
  private final HealthCheckResultRepository resultRepository;
  private final IncidentRepository incidentRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final IncidentAnalyzer incidentAnalyzer = new IncidentAnalyzer();

  @Override
  public void runHealthCheckForAll() {
    log.info("Bắt đầu quét tất cả active endpoints để kiểm tra sức khỏe...");
    List<MonitoredEndpoint> endpoints = endpointRepository.findAllActive();
    log.info("Tìm thấy {} active endpoints.", endpoints.size());

    for (MonitoredEndpoint endpoint : endpoints) {
      try {
        runHealthCheckForEndpointInternal(endpoint);
      } catch (Exception e) {
        log.error("Lỗi khi kiểm tra endpoint ID {}: {}", endpoint.getId(), e.getMessage(), e);
      }
    }
    log.info("Hoàn tất quét health check.");
  }

  @Override
  public void runHealthCheckForEndpoint(Long endpointId) {
    endpointRepository
        .findById(endpointId)
        .ifPresentOrElse(
            endpoint -> {
              if (Boolean.TRUE.equals(endpoint.getIsActive())) {
                runHealthCheckForEndpointInternal(endpoint);
              } else {
                log.info("Endpoint {} đang không active, bỏ qua.", endpointId);
              }
            },
            () -> log.warn("Không tìm thấy endpoint {}", endpointId));
  }

  private void runHealthCheckForEndpointInternal(MonitoredEndpoint endpoint) {
    Long policyId = endpoint.getPolicyId();
    if (policyId == null) {
      log.warn("Endpoint {} không có policy, bỏ qua.", endpoint.getId());
      return;
    }

    CheckPolicy policy = checkPolicyRepository.findById(policyId).orElse(null);
    if (policy == null) {
      log.warn("Không tìm thấy policy {} cho endpoint {}, bỏ qua.", policyId, endpoint.getId());
      return;
    }

    HealthCheckExecutor executor =
        executors.stream()
            .filter(e -> e.supports(endpoint.getCheckType()))
            .findFirst()
            .orElse(null);

    if (executor == null) {
      log.error("Không có executor nào hỗ trợ CheckType {}", endpoint.getCheckType());
      return;
    }

    log.debug(
        "Thực thi kiểm tra cho endpoint {} bằng {}",
        endpoint.getId(),
        executor.getClass().getSimpleName());
    HealthCheckResult result = executor.execute(endpoint, policy);
    result.setWorkspaceId(endpoint.getWorkspaceId());

    // Lưu kết quả
    HealthCheckResult savedResult = resultRepository.save(result);

    // Cập nhật trạng thái mới nhất cho endpoint
    EndpointStatus newStatus = mapToEndpointStatus(savedResult.getStatus());
    endpoint.setStatus(newStatus);
    endpoint.setLastCheckedAt(savedResult.getCheckedAt());
    endpointRepository.save(endpoint);

    // Phát sự kiện vừa check xong (để ghi log hoặc dashboard realtime)
    eventPublisher.publishEvent(EndpointCheckedEvent.of(endpoint.getId(), savedResult));

    // Phân tích kết quả để mở/đóng Incident
    analyzeIncident(endpoint, policy);
  }

  private void analyzeIncident(MonitoredEndpoint endpoint, CheckPolicy policy) {
    // Lấy danh sách 10 kết quả gần nhất để phân tích
    List<HealthCheckResult> recentResults =
        resultRepository.findTop10ByEndpointIdOrderByCheckedAtDesc(endpoint.getId());

    Optional<Incident> openIncident =
        incidentRepository.findOpenIncidentByEndpointId(endpoint.getId());

    IncidentAnalyzer.AnalysisResult analysis =
        incidentAnalyzer.analyze(recentResults, policy, openIncident);

    switch (analysis.decision()) {
      case OPEN_INCIDENT -> {
        log.warn(
            "Phát hiện lỗi liên tục trên endpoint {}: {}", endpoint.getId(), analysis.reason());
        Incident newIncident =
            incidentAnalyzer.buildNewIncident(
                endpoint.getId(),
                endpoint.getWorkspaceId(),
                analysis.reason(),
                recentResults, // Đơn giản hóa, lấy list hiện tại
                policy.getFailureThreshold() != null ? policy.getFailureThreshold() : 3);

        Incident savedIncident = incidentRepository.save(newIncident);
        eventPublisher.publishEvent(
            IncidentOpenedEvent.of(savedIncident.getId(), endpoint.getId(), analysis.reason()));
      }
      case CLOSE_INCIDENT -> {
        log.info("Endpoint {} đã phục hồi, đóng incident hiện tại.", endpoint.getId());
        Incident incident = openIncident.get();
        incident.resolve(LocalDateTime.now());
        Incident savedIncident = incidentRepository.save(incident);
        eventPublisher.publishEvent(
            IncidentResolvedEvent.of(savedIncident.getId(), endpoint.getId()));
      }
      case NO_ACTION -> log.debug("Phân tích endpoint {}: {}", endpoint.getId(), analysis.reason());
    }
  }

  private EndpointStatus mapToEndpointStatus(CheckStatus checkStatus) {
    if (checkStatus == CheckStatus.DOWN) {
      return EndpointStatus.DOWN;
    }
    if (checkStatus == CheckStatus.DEGRADED) {
      return EndpointStatus.DEGRADED;
    }
    return EndpointStatus.UP;
  }
}

package com.example.apihealthchecksystem.application.usecase;

import com.example.apihealthchecksystem.application.port.in.RunEndpointHealthCheckUseCase;
import com.example.apihealthchecksystem.application.port.out.AlertRuleRepository;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
public class MonitorEndpointWorkerService implements RunEndpointHealthCheckUseCase {

  private final EndpointRepository endpointRepository;
  private final CheckPolicyRepository checkPolicyRepository;
  private final AlertRuleRepository alertRuleRepository;
  private final List<HealthCheckExecutor> executors;
  private final HealthCheckResultRepository resultRepository;
  private final IncidentRepository incidentRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final IncidentAnalyzer incidentAnalyzer = new IncidentAnalyzer();

  @Override
  @Transactional
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

    log.info(
        "Thực thi health check cho endpoint {} [{} {}] bằng {} trên thread {}",
        endpoint.getId(),
        endpoint.getMethod(),
        endpoint.getUrl(),
        executor.getClass().getSimpleName(),
        Thread.currentThread().getName());
    HealthCheckResult result = executor.execute(endpoint, policy);
    result.setWorkspaceId(endpoint.getWorkspaceId());

    // Lưu kết quả
    HealthCheckResult savedResult = resultRepository.save(result);

    // Cập nhật trạng thái mới nhất cho endpoint
    endpoint.markChecked(savedResult);
    endpoint.scheduleNextRun(savedResult.getCheckedAt().plusSeconds(policy.effectiveIntervalSeconds()));
    endpointRepository.save(endpoint);

    // Phát sự kiện vừa check xong (để ghi log hoặc dashboard realtime)
    eventPublisher.publishEvent(EndpointCheckedEvent.of(endpoint.getId(), savedResult));

    // Phân tích kết quả để mở/đóng Incident
    analyzeIncident(endpoint, policy);
  }

  private void analyzeIncident(MonitoredEndpoint endpoint, CheckPolicy policy) {
    List<HealthCheckResult> recentResults =
        resultRepository.findTop10ByEndpointIdOrderByCheckedAtDesc(endpoint.getId());
    if (recentResults.isEmpty()) {
      return;
    }

    Optional<Incident> openIncident =
        incidentRepository.findOpenIncidentByEndpointId(endpoint.getId());
    var alertRules =
        endpoint.getAlertRuleIds() == null || endpoint.getAlertRuleIds().isEmpty()
            ? List.<com.example.apihealthchecksystem.domain.model.AlertRule>of()
            : alertRuleRepository.findAllByIds(endpoint.getAlertRuleIds()).stream()
                .filter(rule -> endpoint.getWorkspaceId().equals(rule.getWorkspaceId()))
                .filter(rule -> !Boolean.FALSE.equals(rule.getIsActive()))
                .toList();

    IncidentAnalyzer.AnalysisResult analysis =
        incidentAnalyzer.analyze(recentResults, policy, alertRules, openIncident);

    log.info(
        "Kết quả phân tích incident cho endpoint {}: decision={}, openIncidentPresent={}, triggeredRuleIds={}, triggeringResultIds={}, severity={}, reason={}",
        endpoint.getId(),
        analysis.decision(),
        openIncident.isPresent(),
        analysis.triggeredRuleIds(),
        analysis.triggeringResultIds(),
        analysis.severity(),
        analysis.reason());

    switch (analysis.decision()) {
      case OPEN_INCIDENT -> {
        log.warn(
            "Phát hiện lỗi liên tục trên endpoint {}: {}", endpoint.getId(), analysis.reason());
        Incident newIncident =
            incidentAnalyzer.buildNewIncident(
                endpoint.getId(),
                endpoint.getWorkspaceId(),
                analysis.reason(),
                recentResults,
                analysis.triggeringResultIds(),
                analysis.triggeredRuleIds(),
                analysis.severity());

        Incident savedIncident = incidentRepository.save(newIncident);
        log.info(
            "Đã tạo incident {} cho endpoint {}. Publish IncidentOpenedEvent với triggeredRuleIds={}",
            savedIncident.getId(),
            endpoint.getId(),
            savedIncident.getTriggeredAlertRuleIds());
        eventPublisher.publishEvent(
            IncidentOpenedEvent.of(savedIncident.getId(), endpoint.getId(), analysis.reason()));
      }
      case CLOSE_INCIDENT -> {
        log.info("Endpoint {} đã phục hồi, đóng incident hiện tại.", endpoint.getId());
        Incident incident = openIncident.get();
        incident.resolve(LocalDateTime.now());
        Incident savedIncident = incidentRepository.save(incident);
        log.info(
            "Đã đóng incident {} cho endpoint {}. Publish IncidentResolvedEvent.",
            savedIncident.getId(),
            endpoint.getId());
        eventPublisher.publishEvent(
            IncidentResolvedEvent.of(savedIncident.getId(), endpoint.getId()));
      }
      case NO_ACTION -> log.debug("Phân tích endpoint {}: {}", endpoint.getId(), analysis.reason());
    }
  }
}

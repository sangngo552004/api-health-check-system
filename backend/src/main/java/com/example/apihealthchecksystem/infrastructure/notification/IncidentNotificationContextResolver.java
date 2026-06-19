package com.example.apihealthchecksystem.infrastructure.notification;

import com.example.apihealthchecksystem.application.port.out.EndpointRepository;
import com.example.apihealthchecksystem.application.port.out.IncidentRepository;
import com.example.apihealthchecksystem.domain.model.Incident;
import com.example.apihealthchecksystem.domain.model.MonitoredEndpoint;
import com.example.apihealthchecksystem.infrastructure.persistence.repository.AlertRuleJpaRepository;
import com.example.apihealthchecksystem.infrastructure.persistence.repository.ContactGroupJpaRepository;
import com.example.apihealthchecksystem.infrastructure.persistence.repository.UserJpaRepository;
import com.example.apihealthchecksystem.infrastructure.persistence.repository.WorkspaceMemberJpaRepository;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class IncidentNotificationContextResolver {

  private final IncidentRepository incidentRepository;
  private final EndpointRepository endpointRepository;
  private final WorkspaceMemberJpaRepository workspaceMemberJpaRepository;
  private final UserJpaRepository userJpaRepository;
  private final AlertRuleJpaRepository alertRuleJpaRepository;
  private final ContactGroupJpaRepository contactGroupJpaRepository;

  @Transactional(readOnly = true)
  public Optional<ResolvedIncidentNotification> resolve(Long incidentId, Long endpointId) {
    Incident incident = incidentRepository.findById(incidentId).orElse(null);
    MonitoredEndpoint endpoint = endpointRepository.findById(endpointId).orElse(null);
    if (incident == null || endpoint == null) {
      return Optional.empty();
    }

    Set<String> recipientEmails = new LinkedHashSet<>();

    List<Long> memberIds =
        workspaceMemberJpaRepository.findByIdWorkspaceId(incident.getWorkspaceId()).stream()
            .map(member -> member.getId().getUserId())
            .distinct()
            .toList();
    if (!memberIds.isEmpty()) {
      recipientEmails.addAll(userJpaRepository.findDistinctEmailsByIdIn(memberIds));
    }

    List<Long> alertRuleIds = incident.getTriggeredAlertRuleIds();
    if (alertRuleIds != null && !alertRuleIds.isEmpty()) {
      List<Long> contactGroupIds =
          alertRuleJpaRepository.findDistinctActiveContactGroupIdsByIdIn(alertRuleIds);
      if (!contactGroupIds.isEmpty()) {
        recipientEmails.addAll(
            contactGroupJpaRepository.findDistinctActiveEmailAddressesByIdIn(contactGroupIds));
      }
    }

    log.info(
        "Resolved notification context cho incident {} endpoint {} với {} recipient(s): {}",
        incidentId,
        endpointId,
        recipientEmails.size(),
        recipientEmails);

    return Optional.of(new ResolvedIncidentNotification(incident, endpoint, recipientEmails));
  }

  public record ResolvedIncidentNotification(
      Incident incident, MonitoredEndpoint endpoint, Set<String> recipientEmails) {}
}

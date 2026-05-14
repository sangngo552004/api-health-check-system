package com.example.apihealthchecksystem.infrastructure.event;

import com.example.apihealthchecksystem.application.port.out.ContactGroupRepository;
import com.example.apihealthchecksystem.application.port.out.EndpointRepository;
import com.example.apihealthchecksystem.application.port.out.IncidentRepository;
import com.example.apihealthchecksystem.application.port.out.NotificationPort;
import com.example.apihealthchecksystem.domain.event.IncidentOpenedEvent;
import com.example.apihealthchecksystem.domain.event.IncidentResolvedEvent;
import com.example.apihealthchecksystem.domain.model.ContactGroup;
import com.example.apihealthchecksystem.domain.model.Incident;
import com.example.apihealthchecksystem.domain.model.MonitoredEndpoint;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class IncidentEventListener {

  private final IncidentRepository incidentRepository;
  private final EndpointRepository endpointRepository;
  private final ContactGroupRepository contactGroupRepository;
  private final NotificationPort notificationPort;

  @Async
  @EventListener
  public void handleIncidentOpened(IncidentOpenedEvent event) {
    log.info("Nhận sự kiện IncidentOpenedEvent cho incident {}", event.incidentId());

    Incident incident = incidentRepository.findById(event.incidentId()).orElse(null);
    MonitoredEndpoint endpoint = endpointRepository.findById(event.endpointId()).orElse(null);

    if (incident != null && endpoint != null) {
      // Lấy danh sách contact group của workspace (hoặc riêng của endpoint nếu có cấu hình
      // override)
      // Hiện tại đơn giản hóa: lấy tất cả contact group của workspace
      List<ContactGroup> contactGroups =
          contactGroupRepository.findByWorkspaceId(endpoint.getWorkspaceId(), 0, 100);
      notificationPort.sendIncidentAlert(incident, endpoint, contactGroups);
    }
  }

  @Async
  @EventListener
  public void handleIncidentResolved(IncidentResolvedEvent event) {
    log.info("Nhận sự kiện IncidentResolvedEvent cho incident {}", event.incidentId());

    Incident incident = incidentRepository.findById(event.incidentId()).orElse(null);
    MonitoredEndpoint endpoint = endpointRepository.findById(event.endpointId()).orElse(null);

    if (incident != null && endpoint != null) {
      List<ContactGroup> contactGroups =
          contactGroupRepository.findByWorkspaceId(endpoint.getWorkspaceId(), 0, 100);
      notificationPort.sendRecoveryAlert(incident, endpoint, contactGroups);
    }
  }
}

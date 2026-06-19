package com.example.apihealthchecksystem.infrastructure.config;

import com.example.apihealthchecksystem.application.mapper.AlertRuleDtoMapper;
import com.example.apihealthchecksystem.application.mapper.CheckPolicyDtoMapper;
import com.example.apihealthchecksystem.application.mapper.ContactGroupDtoMapper;
import com.example.apihealthchecksystem.application.mapper.EndpointDtoMapper;
import com.example.apihealthchecksystem.application.monitoring.MonitorEndpointBatchService;
import com.example.apihealthchecksystem.application.port.in.AuthUseCase;
import com.example.apihealthchecksystem.application.port.in.GetAdminDataUseCase;
import com.example.apihealthchecksystem.application.port.in.GetIncidentUseCase;
import com.example.apihealthchecksystem.application.port.in.ManageAdminUseCase;
import com.example.apihealthchecksystem.application.port.in.ManageAlertRuleUseCase;
import com.example.apihealthchecksystem.application.port.in.ManageCheckPolicyUseCase;
import com.example.apihealthchecksystem.application.port.in.ManageContactGroupUseCase;
import com.example.apihealthchecksystem.application.port.in.ManageEndpointUseCase;
import com.example.apihealthchecksystem.application.port.in.ManageIncidentUseCase;
import com.example.apihealthchecksystem.application.port.in.ManageWorkspaceUseCase;
import com.example.apihealthchecksystem.application.port.in.MonitorEndpointUseCase;
import com.example.apihealthchecksystem.application.port.in.RunEndpointHealthCheckUseCase;
import com.example.apihealthchecksystem.application.port.out.AlertRuleRepository;
import com.example.apihealthchecksystem.application.port.out.AuthenticationPort;
import com.example.apihealthchecksystem.application.port.out.CheckPolicyRepository;
import com.example.apihealthchecksystem.application.port.out.ContactGroupRepository;
import com.example.apihealthchecksystem.application.port.out.EndpointRepository;
import com.example.apihealthchecksystem.application.port.out.HealthCheckExecutor;
import com.example.apihealthchecksystem.application.port.out.HealthCheckResultRepository;
import com.example.apihealthchecksystem.application.port.out.IncidentRepository;
import com.example.apihealthchecksystem.application.port.out.UserRepository;
import com.example.apihealthchecksystem.application.port.out.WorkspaceRepository;
import com.example.apihealthchecksystem.application.usecase.AuthService;
import com.example.apihealthchecksystem.application.usecase.GetAdminDataService;
import com.example.apihealthchecksystem.application.usecase.GetIncidentService;
import com.example.apihealthchecksystem.application.usecase.ManageAdminService;
import com.example.apihealthchecksystem.application.usecase.ManageAlertRuleService;
import com.example.apihealthchecksystem.application.usecase.ManageCheckPolicyService;
import com.example.apihealthchecksystem.application.usecase.ManageContactGroupService;
import com.example.apihealthchecksystem.application.usecase.ManageEndpointService;
import com.example.apihealthchecksystem.application.usecase.ManageIncidentService;
import com.example.apihealthchecksystem.application.usecase.ManageWorkspaceService;
import com.example.apihealthchecksystem.application.usecase.MonitorEndpointWorkerService;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UseCaseConfig {

  @Bean
  public AuthUseCase authUseCase(AuthenticationPort authenticationPort) {
    return new AuthService(authenticationPort);
  }

  @Bean
  public GetAdminDataUseCase getAdminDataUseCase(
      UserRepository userRepository, WorkspaceRepository workspaceRepository) {
    return new GetAdminDataService(userRepository, workspaceRepository);
  }

  @Bean
  public ManageAdminUseCase manageAdminUseCase(
      UserRepository userRepository,
      WorkspaceRepository workspaceRepository,
      PasswordEncoder passwordEncoder) {
    return new ManageAdminService(userRepository, workspaceRepository, passwordEncoder);
  }

  @Bean
  public ManageEndpointUseCase manageEndpointUseCase(
      EndpointRepository endpointRepository,
      CheckPolicyRepository checkPolicyRepository,
      AlertRuleRepository alertRuleRepository,
      EndpointDtoMapper mapper) {
    return new ManageEndpointService(
        endpointRepository, checkPolicyRepository, alertRuleRepository, mapper);
  }

  @Bean
  public ManageCheckPolicyUseCase manageCheckPolicyUseCase(
      CheckPolicyRepository repository, CheckPolicyDtoMapper mapper) {
    return new ManageCheckPolicyService(repository, mapper);
  }

  @Bean
  public ManageAlertRuleUseCase manageAlertRuleUseCase(
      AlertRuleRepository repository,
      ContactGroupRepository contactGroupRepository,
      AlertRuleDtoMapper mapper) {
    return new ManageAlertRuleService(repository, contactGroupRepository, mapper);
  }

  @Bean
  public ManageContactGroupUseCase manageContactGroupUseCase(
      ContactGroupRepository repository, ContactGroupDtoMapper mapper) {
    return new ManageContactGroupService(repository, mapper);
  }

  @Bean
  public ManageWorkspaceUseCase manageWorkspaceUseCase(
      WorkspaceRepository workspaceRepository, UserRepository userRepository) {
    return new ManageWorkspaceService(workspaceRepository, userRepository);
  }

  @Bean
  public MonitorEndpointUseCase monitorEndpointUseCase(
      EndpointRepository endpointRepository,
      RunEndpointHealthCheckUseCase runEndpointHealthCheckUseCase) {
    return new MonitorEndpointBatchService(endpointRepository, runEndpointHealthCheckUseCase);
  }

  @Bean
  public RunEndpointHealthCheckUseCase runEndpointHealthCheckUseCase(
      EndpointRepository endpointRepository,
      CheckPolicyRepository checkPolicyRepository,
      AlertRuleRepository alertRuleRepository,
      List<HealthCheckExecutor> executors,
      HealthCheckResultRepository resultRepository,
      IncidentRepository incidentRepository,
      ApplicationEventPublisher eventPublisher) {
    return new MonitorEndpointWorkerService(
        endpointRepository,
        checkPolicyRepository,
        alertRuleRepository,
        executors,
        resultRepository,
        incidentRepository,
        eventPublisher);
  }

  @Bean
  public com.example.apihealthchecksystem.application.port.in.GetDashboardStatsUseCase
      getDashboardStatsUseCase(
          WorkspaceRepository workspaceRepository,
          EndpointRepository endpointRepository,
          IncidentRepository incidentRepository,
          HealthCheckResultRepository resultRepository) {
    return new com.example.apihealthchecksystem.application.usecase.GetDashboardStatsService(
        workspaceRepository, endpointRepository, incidentRepository, resultRepository);
  }

  @Bean
  public GetIncidentUseCase getIncidentUseCase(
      WorkspaceRepository workspaceRepository,
      IncidentRepository incidentRepository,
      EndpointRepository endpointRepository,
      HealthCheckResultRepository healthCheckResultRepository) {
    return new GetIncidentService(
        workspaceRepository,
        incidentRepository,
        endpointRepository,
        healthCheckResultRepository);
  }

  @Bean
  public ManageIncidentUseCase manageIncidentUseCase(
      IncidentRepository incidentRepository, EndpointRepository endpointRepository) {
    return new ManageIncidentService(incidentRepository, endpointRepository);
  }
}

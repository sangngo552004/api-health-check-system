package com.example.apihealthchecksystem.application.usecase;

import com.example.apihealthchecksystem.application.dto.request.ContactGroupCreateCommand;
import com.example.apihealthchecksystem.application.dto.request.ContactGroupUpdateCommand;
import com.example.apihealthchecksystem.application.dto.response.ContactGroupDto;
import com.example.apihealthchecksystem.application.dto.response.PagedResponseDto;
import com.example.apihealthchecksystem.application.exception.AccessDeniedException;
import com.example.apihealthchecksystem.application.exception.AppErrorCode;
import com.example.apihealthchecksystem.application.exception.ResourceNotFoundException;
import com.example.apihealthchecksystem.application.mapper.ContactGroupDtoMapper;
import com.example.apihealthchecksystem.application.port.in.ManageContactGroupUseCase;
import com.example.apihealthchecksystem.application.port.out.ContactGroupRepository;
import com.example.apihealthchecksystem.domain.model.ContactGroup;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ManageContactGroupService implements ManageContactGroupUseCase {

  private final ContactGroupRepository repository;
  private final ContactGroupDtoMapper mapper;

  @Override
  public ContactGroupDto createContactGroup(Long workspaceId, ContactGroupCreateCommand command) {
    ContactGroup group = mapper.toDomain(command);
    group.setWorkspaceId(workspaceId);
    return mapper.toDto(repository.save(group));
  }

  @Override
  public ContactGroupDto updateContactGroup(Long workspaceId, ContactGroupUpdateCommand command) {
    ContactGroup existing = getContactGroupById(command.id());
    validateWorkspaceAccess(existing.getWorkspaceId(), workspaceId);

    existing.setName(command.name());
    existing.setDescription(command.description());
    existing.setUserIds(command.userIds());
    existing.setEmailAddresses(command.emailAddresses());
    existing.setWebhookUrls(command.webhookUrls());
    if (command.isActive() != null) {
      existing.setIsActive(command.isActive());
    }

    return mapper.toDto(repository.save(existing));
  }

  @Override
  public ContactGroupDto getContactGroup(Long workspaceId, Long id) {
    ContactGroup group = getContactGroupById(id);
    validateWorkspaceAccess(group.getWorkspaceId(), workspaceId);

    return mapper.toDto(group);
  }

  @Override
  public PagedResponseDto<ContactGroupDto> getContactGroupsByWorkspace(
      Long workspaceId, int page, int size) {
    List<ContactGroup> groups = repository.findByWorkspaceId(workspaceId, page, size);
    long total = repository.countByWorkspaceId(workspaceId);

    List<ContactGroupDto> dtos = groups.stream().map(mapper::toDto).collect(Collectors.toList());

    return PagedResponseDto.of(dtos, page, size, total);
  }

  @Override
  public void deleteContactGroup(Long workspaceId, Long id) {
    ContactGroup group = getContactGroupById(id);
    validateWorkspaceAccess(group.getWorkspaceId(), workspaceId);
    repository.deleteById(id);
  }

  private ContactGroup getContactGroupById(Long contactGroupId) {
    return repository
        .findById(contactGroupId)
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    AppErrorCode.CONTACT_GROUP_NOT_FOUND, contactGroupId));
  }

  private void validateWorkspaceAccess(Long resourceWorkspaceId, Long requestedWorkspaceId) {
    if (!resourceWorkspaceId.equals(requestedWorkspaceId)) {
      throw new AccessDeniedException();
    }
  }
}

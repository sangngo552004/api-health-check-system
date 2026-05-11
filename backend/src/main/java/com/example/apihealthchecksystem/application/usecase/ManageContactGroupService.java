package com.example.apihealthchecksystem.application.usecase;

import com.example.apihealthchecksystem.application.dto.request.ContactGroupCreateCommand;
import com.example.apihealthchecksystem.application.dto.request.ContactGroupUpdateCommand;
import com.example.apihealthchecksystem.application.dto.response.ContactGroupDto;
import com.example.apihealthchecksystem.application.dto.response.PagedResponseDto;
import com.example.apihealthchecksystem.application.exception.AccessDeniedException;
import com.example.apihealthchecksystem.application.exception.ResourceNotFoundException;
import com.example.apihealthchecksystem.application.mapper.ContactGroupDtoMapper;
import com.example.apihealthchecksystem.application.port.in.ManageContactGroupUseCase;
import com.example.apihealthchecksystem.application.port.out.ContactGroupRepository;
import com.example.apihealthchecksystem.domain.model.ContactGroup;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManageContactGroupService implements ManageContactGroupUseCase {

  private final ContactGroupRepository repository;
  private final ContactGroupDtoMapper mapper;

  @Override
  @Transactional
  public ContactGroupDto createContactGroup(Long workspaceId, ContactGroupCreateCommand command) {
    ContactGroup group = mapper.toDomain(command);
    group.setWorkspaceId(workspaceId);
    return mapper.toDto(repository.save(group));
  }

  @Override
  @Transactional
  public ContactGroupDto updateContactGroup(Long workspaceId, ContactGroupUpdateCommand command) {
    ContactGroup existing =
        repository
            .findById(command.id())
            .orElseThrow(() -> new ResourceNotFoundException("ContactGroup", command.id()));

    if (!existing.getWorkspaceId().equals(workspaceId)) {
      throw new AccessDeniedException("ContactGroup không thuộc về Workspace này.");
    }

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
    ContactGroup group =
        repository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("ContactGroup", id));

    if (!group.getWorkspaceId().equals(workspaceId)) {
      throw new AccessDeniedException("ContactGroup không thuộc về Workspace này.");
    }

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
  @Transactional
  public void deleteContactGroup(Long workspaceId, Long id) {
    ContactGroup group =
        repository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("ContactGroup", id));

    if (!group.getWorkspaceId().equals(workspaceId)) {
      throw new AccessDeniedException("ContactGroup không thuộc về Workspace này.");
    }
    repository.deleteById(id);
  }
}

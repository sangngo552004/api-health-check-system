package com.example.apihealthchecksystem.application.port.in;

import com.example.apihealthchecksystem.application.dto.request.ContactGroupCreateCommand;
import com.example.apihealthchecksystem.application.dto.request.ContactGroupUpdateCommand;
import com.example.apihealthchecksystem.application.dto.response.ContactGroupDto;
import com.example.apihealthchecksystem.application.dto.response.PagedResponseDto;

public interface ManageContactGroupUseCase {
  ContactGroupDto createContactGroup(Long workspaceId, ContactGroupCreateCommand command);

  ContactGroupDto updateContactGroup(Long workspaceId, ContactGroupUpdateCommand command);

  ContactGroupDto getContactGroup(Long workspaceId, Long id);

  PagedResponseDto<ContactGroupDto> getContactGroupsByWorkspace(
      Long workspaceId, int page, int size);

  void deleteContactGroup(Long workspaceId, Long id);
}

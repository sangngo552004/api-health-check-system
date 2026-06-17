package com.example.apihealthchecksystem.application.port.in;

import com.example.apihealthchecksystem.application.dto.response.AdminUserDto;
import com.example.apihealthchecksystem.application.dto.response.WorkspaceDto;
import java.util.List;

public interface GetAdminDataUseCase {
  List<AdminUserDto> getUsers();

  List<WorkspaceDto> getWorkspaces();
}

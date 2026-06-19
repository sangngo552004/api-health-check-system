package com.example.apihealthchecksystem.delivery.rest;

import com.example.apihealthchecksystem.application.dto.response.WorkspaceDto;
import com.example.apihealthchecksystem.application.port.in.ManageWorkspaceUseCase;
import com.example.apihealthchecksystem.delivery.rest.common.ApiResponse;
import com.example.apihealthchecksystem.delivery.rest.common.security.CurrentUserId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

  private final ManageWorkspaceUseCase workspaceUseCase;

  @GetMapping("/my")
  public ApiResponse<List<WorkspaceDto>> getMyWorkspaces(@CurrentUserId Long userId) {
    return ApiResponse.success(workspaceUseCase.getMyWorkspaces(userId));
  }
}

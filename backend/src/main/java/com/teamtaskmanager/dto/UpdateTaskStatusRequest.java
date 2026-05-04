package com.teamtaskmanager.dto;

import com.teamtaskmanager.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateTaskStatusRequest(
    @NotNull(message = "Status is required")
    TaskStatus status
) {
}

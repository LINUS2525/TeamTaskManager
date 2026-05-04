package com.teamtaskmanager.dto;

import com.teamtaskmanager.enums.TaskPriority;
import com.teamtaskmanager.enums.TaskStatus;
import java.time.Instant;
import java.time.LocalDate;

public record TaskResponse(
    Long id,
    String title,
    String description,
    TaskStatus status,
    TaskPriority priority,
    LocalDate dueDate,
    UserSummaryResponse assignee,
    Long projectId,
    String projectName,
    Instant createdAt,
    Instant updatedAt
) {
}

package com.teamtaskmanager.dto;

import com.teamtaskmanager.enums.TaskPriority;
import com.teamtaskmanager.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record CreateTaskRequest(
    @NotBlank(message = "Task title is required")
    @Size(max = 180, message = "Task title must be at most 180 characters")
    String title,

    @Size(max = 4000, message = "Description must be at most 4000 characters")
    String description,

    TaskStatus status,

    TaskPriority priority,

    LocalDate dueDate,

    Long assigneeId
) {
}

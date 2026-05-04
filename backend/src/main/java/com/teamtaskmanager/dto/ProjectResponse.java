package com.teamtaskmanager.dto;

import com.teamtaskmanager.enums.ProjectRole;
import java.time.Instant;

public record ProjectResponse(
    Long id,
    String name,
    String description,
    ProjectRole myRole,
    long memberCount,
    long taskCount,
    Instant createdAt,
    Instant updatedAt
) {
}

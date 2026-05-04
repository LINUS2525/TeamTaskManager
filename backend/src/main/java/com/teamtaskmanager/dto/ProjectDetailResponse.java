package com.teamtaskmanager.dto;

import com.teamtaskmanager.enums.ProjectRole;
import java.time.Instant;
import java.util.List;

public record ProjectDetailResponse(
    Long id,
    String name,
    String description,
    ProjectRole myRole,
    List<ProjectMemberResponse> members,
    List<TaskResponse> tasks,
    Instant createdAt,
    Instant updatedAt
) {
}

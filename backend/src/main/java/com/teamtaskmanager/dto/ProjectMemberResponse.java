package com.teamtaskmanager.dto;

import com.teamtaskmanager.enums.ProjectRole;
import java.time.Instant;

public record ProjectMemberResponse(
    Long id,
    UserSummaryResponse user,
    ProjectRole role,
    Instant joinedAt
) {
}

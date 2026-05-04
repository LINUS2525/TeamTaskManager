package com.teamtaskmanager.dto;

import com.teamtaskmanager.enums.ProjectRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AddProjectMemberRequest(
    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    String email,

    ProjectRole role
) {
}

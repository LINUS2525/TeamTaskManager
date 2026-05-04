package com.teamtaskmanager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(
    @NotBlank(message = "Name is required")
    @Size(max = 120, message = "Name must be at most 120 characters")
    String name,

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    @Size(max = 160, message = "Email must be at most 160 characters")
    String email,

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 120, message = "Password must be between 8 and 120 characters")
    String password
) {
}

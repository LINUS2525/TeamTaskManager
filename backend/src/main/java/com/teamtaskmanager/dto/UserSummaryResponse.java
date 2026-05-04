package com.teamtaskmanager.dto;

public record UserSummaryResponse(
    Long id,
    String name,
    String email
) {
}

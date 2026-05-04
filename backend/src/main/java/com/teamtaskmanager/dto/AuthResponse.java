package com.teamtaskmanager.dto;

public record AuthResponse(
    String token,
    String tokenType,
    long expiresInMs,
    UserSummaryResponse user
) {
}

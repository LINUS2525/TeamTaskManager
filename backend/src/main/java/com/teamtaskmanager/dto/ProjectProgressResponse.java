package com.teamtaskmanager.dto;

public record ProjectProgressResponse(
    Long projectId,
    String projectName,
    long totalTasks,
    long todoCount,
    long inProgressCount,
    long doneCount,
    long overdueCount,
    double completionPercent
) {
}

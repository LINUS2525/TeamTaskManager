package com.teamtaskmanager.dto;

import java.util.List;

public record DashboardResponse(
    long totalTasks,
    long todoCount,
    long inProgressCount,
    long doneCount,
    long overdueCount,
    List<TaskResponse> myAssignedTasks,
    List<ProjectProgressResponse> projectProgress
) {
}

package com.teamtaskmanager.controller;

import com.teamtaskmanager.dto.DashboardResponse;
import com.teamtaskmanager.dto.ProjectProgressResponse;
import com.teamtaskmanager.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getMyDashboard() {
        return ResponseEntity.ok(dashboardService.getMyDashboard());
    }

    @GetMapping("/projects/{projectId}/dashboard")
    public ResponseEntity<ProjectProgressResponse> getProjectDashboard(@PathVariable Long projectId) {
        return ResponseEntity.ok(dashboardService.getProjectDashboard(projectId));
    }
}

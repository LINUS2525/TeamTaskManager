package com.teamtaskmanager.service;

import com.teamtaskmanager.dto.DashboardResponse;
import com.teamtaskmanager.dto.ProjectProgressResponse;
import com.teamtaskmanager.dto.TaskResponse;
import com.teamtaskmanager.entity.Project;
import com.teamtaskmanager.entity.ProjectMember;
import com.teamtaskmanager.entity.Task;
import com.teamtaskmanager.entity.User;
import com.teamtaskmanager.enums.ProjectRole;
import com.teamtaskmanager.enums.TaskStatus;
import com.teamtaskmanager.repository.ProjectRepository;
import com.teamtaskmanager.repository.TaskRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final CurrentUserService currentUserService;
    private final ProjectService projectService;
    private final TaskService taskService;

    public DashboardService(
        ProjectRepository projectRepository,
        TaskRepository taskRepository,
        CurrentUserService currentUserService,
        ProjectService projectService,
        TaskService taskService
    ) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.currentUserService = currentUserService;
        this.projectService = projectService;
        this.taskService = taskService;
    }

    @Transactional(readOnly = true)
    public DashboardResponse getMyDashboard() {
        User currentUser = currentUserService.getCurrentUser();
        List<Project> projects = projectRepository.findAllByMemberUserId(currentUser.getId());
        List<Task> visibleTasks = new ArrayList<>();
        List<ProjectProgressResponse> projectProgress = new ArrayList<>();

        for (Project project : projects) {
            ProjectMember membership = projectService.requireMember(project.getId(), currentUser.getId());
            List<Task> projectTasks = membership.getRole() == ProjectRole.ADMIN
                ? taskRepository.findByProjectId(project.getId())
                : taskRepository.findByProjectIdAndAssigneeId(project.getId(), currentUser.getId());

            visibleTasks.addAll(projectTasks);
            projectProgress.add(buildProjectProgress(project, projectTasks));
        }

        List<TaskResponse> myAssignedTasks = taskRepository.findByAssigneeId(currentUser.getId()).stream()
            .map(taskService::toTaskResponse)
            .toList();

        return buildDashboardResponse(visibleTasks, myAssignedTasks, projectProgress);
    }

    @Transactional(readOnly = true)
    public ProjectProgressResponse getProjectDashboard(Long projectId) {
        User currentUser = currentUserService.getCurrentUser();
        Project project = projectService.getProjectOrThrow(projectId);
        ProjectMember membership = projectService.requireMember(projectId, currentUser.getId());

        List<Task> visibleTasks = membership.getRole() == ProjectRole.ADMIN
            ? taskRepository.findByProjectId(projectId)
            : taskRepository.findByProjectIdAndAssigneeId(projectId, currentUser.getId());

        return buildProjectProgress(project, visibleTasks);
    }

    private DashboardResponse buildDashboardResponse(
        List<Task> tasks,
        List<TaskResponse> myAssignedTasks,
        List<ProjectProgressResponse> projectProgress
    ) {
        return new DashboardResponse(
            tasks.size(),
            countByStatus(tasks, TaskStatus.TODO),
            countByStatus(tasks, TaskStatus.IN_PROGRESS),
            countByStatus(tasks, TaskStatus.DONE),
            countOverdue(tasks),
            myAssignedTasks,
            projectProgress
        );
    }

    private ProjectProgressResponse buildProjectProgress(Project project, List<Task> tasks) {
        long total = tasks.size();
        long done = countByStatus(tasks, TaskStatus.DONE);
        double completionPercent = total == 0 ? 0 : Math.round((done * 10000.0) / total) / 100.0;

        return new ProjectProgressResponse(
            project.getId(),
            project.getName(),
            total,
            countByStatus(tasks, TaskStatus.TODO),
            countByStatus(tasks, TaskStatus.IN_PROGRESS),
            done,
            countOverdue(tasks),
            completionPercent
        );
    }

    private long countByStatus(List<Task> tasks, TaskStatus status) {
        return tasks.stream()
            .filter(task -> task.getStatus() == status)
            .count();
    }

    private long countOverdue(List<Task> tasks) {
        LocalDate today = LocalDate.now();
        return tasks.stream()
            .filter(task -> task.getDueDate() != null)
            .filter(task -> task.getDueDate().isBefore(today))
            .filter(task -> task.getStatus() != TaskStatus.DONE)
            .count();
    }
}

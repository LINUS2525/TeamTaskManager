package com.teamtaskmanager.service;

import com.teamtaskmanager.dto.CreateTaskRequest;
import com.teamtaskmanager.dto.TaskResponse;
import com.teamtaskmanager.dto.UpdateTaskRequest;
import com.teamtaskmanager.dto.UpdateTaskStatusRequest;
import com.teamtaskmanager.dto.UserSummaryResponse;
import com.teamtaskmanager.entity.Project;
import com.teamtaskmanager.entity.Task;
import com.teamtaskmanager.entity.User;
import com.teamtaskmanager.enums.ProjectRole;
import com.teamtaskmanager.enums.TaskPriority;
import com.teamtaskmanager.enums.TaskStatus;
import com.teamtaskmanager.exception.ForbiddenActionException;
import com.teamtaskmanager.exception.ResourceNotFoundException;
import com.teamtaskmanager.repository.ProjectMemberRepository;
import com.teamtaskmanager.repository.TaskRepository;
import com.teamtaskmanager.repository.UserRepository;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectService projectService;
    private final CurrentUserService currentUserService;

    public TaskService(
        TaskRepository taskRepository,
        UserRepository userRepository,
        ProjectMemberRepository projectMemberRepository,
        ProjectService projectService,
        CurrentUserService currentUserService
    ) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.projectService = projectService;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public TaskResponse createTask(Long projectId, CreateTaskRequest request) {
        User currentUser = currentUserService.getCurrentUser();
        projectService.requireAdmin(projectId, currentUser.getId());
        Project project = projectService.getProjectOrThrow(projectId);

        Task task = new Task();
        task.setProject(project);
        task.setTitle(request.title().trim());
        task.setDescription(request.description());
        task.setStatus(request.status() == null ? TaskStatus.TODO : request.status());
        task.setPriority(request.priority() == null ? TaskPriority.MEDIUM : request.priority());
        task.setDueDate(request.dueDate());
        task.setAssignee(resolveAssignee(projectId, request.assigneeId()));

        return toTaskResponse(taskRepository.save(task));
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getProjectTasks(Long projectId) {
        User currentUser = currentUserService.getCurrentUser();
        projectService.requireMember(projectId, currentUser.getId());
        return taskRepository.findByProjectId(projectId).stream()
            .map(this::toTaskResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getMyTasks() {
        User currentUser = currentUserService.getCurrentUser();
        return taskRepository.findByAssigneeId(currentUser.getId()).stream()
            .map(this::toTaskResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse getTask(Long taskId) {
        User currentUser = currentUserService.getCurrentUser();
        Task task = getTaskOrThrow(taskId);
        projectService.requireMember(task.getProject().getId(), currentUser.getId());
        return toTaskResponse(task);
    }

    @Transactional
    public TaskResponse updateTask(Long taskId, UpdateTaskRequest request) {
        User currentUser = currentUserService.getCurrentUser();
        Task task = getTaskOrThrow(taskId);
        Long projectId = task.getProject().getId();
        projectService.requireAdmin(projectId, currentUser.getId());

        if (request.title() != null) {
            task.setTitle(request.title().trim());
        }
        if (request.description() != null) {
            task.setDescription(request.description());
        }
        if (request.status() != null) {
            task.setStatus(request.status());
        }
        if (request.priority() != null) {
            task.setPriority(request.priority());
        }
        if (request.dueDate() != null) {
            task.setDueDate(request.dueDate());
        }
        if (request.assigneeId() != null) {
            task.setAssignee(resolveAssignee(projectId, request.assigneeId()));
        }

        return toTaskResponse(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse updateTaskStatus(Long taskId, UpdateTaskStatusRequest request) {
        User currentUser = currentUserService.getCurrentUser();
        Task task = getTaskOrThrow(taskId);
        Long projectId = task.getProject().getId();

        boolean isAdmin = projectMemberRepository.existsByProjectIdAndUserIdAndRole(
            projectId,
            currentUser.getId(),
            ProjectRole.ADMIN
        );
        boolean isAssignee = task.getAssignee() != null && Objects.equals(task.getAssignee().getId(), currentUser.getId());

        if (!isAdmin && !isAssignee) {
            throw new ForbiddenActionException("Only project admins or the assignee can update task status");
        }

        task.setStatus(request.status());
        return toTaskResponse(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(Long taskId) {
        User currentUser = currentUserService.getCurrentUser();
        Task task = getTaskOrThrow(taskId);
        projectService.requireAdmin(task.getProject().getId(), currentUser.getId());
        taskRepository.delete(task);
    }

    public TaskResponse toTaskResponse(Task task) {
        User assignee = task.getAssignee();
        UserSummaryResponse assigneeResponse = assignee == null
            ? null
            : new UserSummaryResponse(assignee.getId(), assignee.getName(), assignee.getEmail());

        return new TaskResponse(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getStatus(),
            task.getPriority(),
            task.getDueDate(),
            assigneeResponse,
            task.getProject().getId(),
            task.getProject().getName(),
            task.getCreatedAt(),
            task.getUpdatedAt()
        );
    }

    private Task getTaskOrThrow(Long taskId) {
        return taskRepository.findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    }

    private User resolveAssignee(Long projectId, Long assigneeId) {
        if (assigneeId == null) {
            return null;
        }

        User assignee = userRepository.findById(assigneeId)
            .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));

        if (!projectMemberRepository.existsByProjectIdAndUserId(projectId, assigneeId)) {
            throw new ForbiddenActionException("Assignee must be a project member");
        }

        return assignee;
    }
}

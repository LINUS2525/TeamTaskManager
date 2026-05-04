package com.teamtaskmanager.service;

import com.teamtaskmanager.dto.AddProjectMemberRequest;
import com.teamtaskmanager.dto.CreateProjectRequest;
import com.teamtaskmanager.dto.ProjectDetailResponse;
import com.teamtaskmanager.dto.ProjectMemberResponse;
import com.teamtaskmanager.dto.ProjectResponse;
import com.teamtaskmanager.dto.TaskResponse;
import com.teamtaskmanager.dto.UserSummaryResponse;
import com.teamtaskmanager.entity.Project;
import com.teamtaskmanager.entity.ProjectMember;
import com.teamtaskmanager.entity.Task;
import com.teamtaskmanager.entity.User;
import com.teamtaskmanager.enums.ProjectRole;
import com.teamtaskmanager.exception.BadRequestException;
import com.teamtaskmanager.exception.DuplicateResourceException;
import com.teamtaskmanager.exception.ForbiddenActionException;
import com.teamtaskmanager.exception.ResourceNotFoundException;
import com.teamtaskmanager.repository.ProjectMemberRepository;
import com.teamtaskmanager.repository.ProjectRepository;
import com.teamtaskmanager.repository.TaskRepository;
import com.teamtaskmanager.repository.UserRepository;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    public ProjectService(
        ProjectRepository projectRepository,
        ProjectMemberRepository projectMemberRepository,
        TaskRepository taskRepository,
        UserRepository userRepository,
        CurrentUserService currentUserService
    ) {
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public ProjectDetailResponse createProject(CreateProjectRequest request) {
        User currentUser = currentUserService.getCurrentUser();

        Project project = new Project();
        project.setName(request.name().trim());
        project.setDescription(request.description());
        Project savedProject = projectRepository.save(project);

        ProjectMember owner = new ProjectMember();
        owner.setProject(savedProject);
        owner.setUser(currentUser);
        owner.setRole(ProjectRole.ADMIN);
        projectMemberRepository.save(owner);

        return getProjectDetails(savedProject.getId());
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getMyProjects() {
        User currentUser = currentUserService.getCurrentUser();
        return projectRepository.findAllByMemberUserId(currentUser.getId()).stream()
            .map(project -> toProjectResponse(project, currentUser.getId()))
            .toList();
    }

    @Transactional(readOnly = true)
    public ProjectDetailResponse getProjectDetails(Long projectId) {
        User currentUser = currentUserService.getCurrentUser();
        Project project = getProjectOrThrow(projectId);
        ProjectRole myRole = requireMember(projectId, currentUser.getId()).getRole();

        List<ProjectMemberResponse> members = projectMemberRepository.findByProjectId(projectId).stream()
            .sorted(Comparator.comparing(member -> member.getUser().getName()))
            .map(this::toProjectMemberResponse)
            .toList();

        List<TaskResponse> tasks = taskRepository.findByProjectId(projectId).stream()
            .map(this::toTaskResponse)
            .toList();

        return new ProjectDetailResponse(
            project.getId(),
            project.getName(),
            project.getDescription(),
            myRole,
            members,
            tasks,
            project.getCreatedAt(),
            project.getUpdatedAt()
        );
    }

    @Transactional
    public ProjectMemberResponse addMember(Long projectId, AddProjectMemberRequest request) {
        User currentUser = currentUserService.getCurrentUser();
        requireAdmin(projectId, currentUser.getId());
        Project project = getProjectOrThrow(projectId);
        User user = userRepository.findByEmail(request.email().toLowerCase())
            .orElseThrow(() -> new ResourceNotFoundException("User with this email was not found"));

        if (projectMemberRepository.existsByProjectIdAndUserId(projectId, user.getId())) {
            throw new DuplicateResourceException("User is already a member of this project");
        }

        ProjectMember member = new ProjectMember();
        member.setProject(project);
        member.setUser(user);
        member.setRole(request.role() == null ? ProjectRole.MEMBER : request.role());
        return toProjectMemberResponse(projectMemberRepository.save(member));
    }

    @Transactional
    public void removeMember(Long projectId, Long userId) {
        User currentUser = currentUserService.getCurrentUser();
        requireAdmin(projectId, currentUser.getId());
        ProjectMember member = requireMember(projectId, userId);

        if (member.getRole() == ProjectRole.ADMIN
            && projectMemberRepository.countByProjectIdAndRole(projectId, ProjectRole.ADMIN) <= 1) {
            throw new BadRequestException("Project must keep at least one admin");
        }

        taskRepository.findByProjectId(projectId).stream()
            .filter(task -> task.getAssignee() != null && task.getAssignee().getId().equals(userId))
            .forEach(task -> task.setAssignee(null));

        projectMemberRepository.delete(member);
    }

    @Transactional
    public void deleteProject(Long projectId) {
        User currentUser = currentUserService.getCurrentUser();
        requireAdmin(projectId, currentUser.getId());
        Project project = getProjectOrThrow(projectId);
        projectRepository.delete(project);
    }

    @Transactional(readOnly = true)
    public ProjectMember requireMember(Long projectId, Long userId) {
        return projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
            .orElseThrow(() -> new ForbiddenActionException("You are not a member of this project"));
    }

    @Transactional(readOnly = true)
    public void requireAdmin(Long projectId, Long userId) {
        if (!projectMemberRepository.existsByProjectIdAndUserIdAndRole(projectId, userId, ProjectRole.ADMIN)) {
            throw new ForbiddenActionException("Only project admins can perform this action");
        }
    }

    @Transactional(readOnly = true)
    public Project getProjectOrThrow(Long projectId) {
        return projectRepository.findById(projectId)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
    }

    private ProjectResponse toProjectResponse(Project project, Long currentUserId) {
        ProjectRole myRole = requireMember(project.getId(), currentUserId).getRole();
        return new ProjectResponse(
            project.getId(),
            project.getName(),
            project.getDescription(),
            myRole,
            project.getMembers().size(),
            taskRepository.countByProjectId(project.getId()),
            project.getCreatedAt(),
            project.getUpdatedAt()
        );
    }

    private ProjectMemberResponse toProjectMemberResponse(ProjectMember member) {
        User user = member.getUser();
        return new ProjectMemberResponse(
            member.getId(),
            new UserSummaryResponse(user.getId(), user.getName(), user.getEmail()),
            member.getRole(),
            member.getJoinedAt()
        );
    }

    private TaskResponse toTaskResponse(Task task) {
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
}

package com.teamtaskmanager.repository;

import com.teamtaskmanager.entity.Task;
import com.teamtaskmanager.enums.TaskStatus;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProjectId(Long projectId);

    List<Task> findByAssigneeId(Long assigneeId);

    List<Task> findByProjectIdAndAssigneeId(Long projectId, Long assigneeId);

    long countByProjectId(Long projectId);

    long countByProjectIdAndStatus(Long projectId, TaskStatus status);

    long countByProjectIdAndDueDateBeforeAndStatusNot(Long projectId, LocalDate date, TaskStatus status);
}

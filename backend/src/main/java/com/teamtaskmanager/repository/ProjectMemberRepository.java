package com.teamtaskmanager.repository;

import com.teamtaskmanager.entity.ProjectMember;
import com.teamtaskmanager.enums.ProjectRole;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    List<ProjectMember> findByProjectId(Long projectId);

    Optional<ProjectMember> findByProjectIdAndUserId(Long projectId, Long userId);

    boolean existsByProjectIdAndUserId(Long projectId, Long userId);

    boolean existsByProjectIdAndUserIdAndRole(Long projectId, Long userId, ProjectRole role);

    long countByProjectIdAndRole(Long projectId, ProjectRole role);

    void deleteByProjectIdAndUserId(Long projectId, Long userId);
}

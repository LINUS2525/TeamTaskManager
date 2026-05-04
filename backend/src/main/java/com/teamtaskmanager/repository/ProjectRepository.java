package com.teamtaskmanager.repository;

import com.teamtaskmanager.entity.Project;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("""
        select distinct p
        from Project p
        join p.members pm
        where pm.user.id = :userId
        order by p.updatedAt desc
        """)
    List<Project> findAllByMemberUserId(@Param("userId") Long userId);
}

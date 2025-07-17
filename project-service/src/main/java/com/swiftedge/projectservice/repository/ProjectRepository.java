package com.swiftedge.projectservice.repository;

import com.swiftedge.projectservice.dto.ProjectStatusDTO;
import com.swiftedge.projectservice.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    Optional<ProjectEntity> findByProjectName(String projectName);

    @Query("SELECT p.projectId FROM ProjectEntity p WHERE p.projectName = :projectName")
    Optional<Long> findProjectIdByProjectName(@Param("projectName") String projectName);

    @Query("SELECT new com.swiftedge.projectservice.dto.ProjectStatusDTO(s.id, s.status, COUNT(p)) " +
            "FROM ProjectEntity p JOIN p.status s GROUP BY s.id, s.status")
    List<ProjectStatusDTO> countProjectsGroupedByStatus();
}

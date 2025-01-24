package com.swiftedge.projectservice.repository;

import com.swiftedge.projectservice.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    Optional<ProjectEntity> findByProjectName(String projectName);
}

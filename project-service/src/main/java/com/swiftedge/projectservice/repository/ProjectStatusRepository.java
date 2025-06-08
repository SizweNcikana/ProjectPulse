package com.swiftedge.projectservice.repository;

import com.swiftedge.projectservice.entity.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectStatusRepository extends JpaRepository<ProjectStatus, Long> {

    boolean existsByStatus(String status);
}

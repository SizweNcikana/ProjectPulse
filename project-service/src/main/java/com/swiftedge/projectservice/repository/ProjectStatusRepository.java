package com.swiftedge.projectservice.repository;

import com.swiftedge.projectservice.entity.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectStatusRepository extends JpaRepository<ProjectStatus, Long> {

    boolean existsByStatus(String status);


    Optional<ProjectStatus> findByStatus(String statusName);
}

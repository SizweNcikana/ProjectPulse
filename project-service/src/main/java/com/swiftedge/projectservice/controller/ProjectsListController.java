package com.swiftedge.projectservice.controller;

import com.swiftedge.dtolibrary.dto.ProjectDTO;
import com.swiftedge.projectservice.dto.ProjectRequestDTO;
import com.swiftedge.projectservice.dto.ProjectResponseDTO;
import com.swiftedge.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v2/projects")
@RequiredArgsConstructor
@Slf4j

public class ProjectsListController {

    private final ProjectService projectService;

    @GetMapping("/list")
    public List<ProjectRequestDTO> getAllProjects() {
        return projectService.getProjects();
    }

    @GetMapping("/{projectId}")
    public Optional<ProjectDTO> getProjectById(@PathVariable("projectId") Long projectId) {
        log.info("Project Id: " + projectId);
        return projectService.getProjectById(projectId);
    }

}

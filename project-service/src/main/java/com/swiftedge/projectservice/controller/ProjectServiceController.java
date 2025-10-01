package com.swiftedge.projectservice.controller;

import com.swiftedge.dtolibrary.dto.ProjectDTO;
import com.swiftedge.dtolibrary.dto.ProjectResponseDTO;
import com.swiftedge.projectservice.dto.ProjectRequestDTO;
import com.swiftedge.projectservice.dto.ProjectStatusDTO;
import com.swiftedge.projectservice.entity.ProjectEntity;
import com.swiftedge.projectservice.entity.ProjectStatus;
import com.swiftedge.projectservice.service.ProjectService;
import com.swiftedge.projectservice.service.ProjectStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/v2/projects")
@RequiredArgsConstructor
@Slf4j
public class ProjectServiceController {

    private final ProjectService projectService;
    private final ProjectStatusService projectStatusService;
    List<ProjectStatusDTO> projectStatus;
    ProjectEntity projectEntity;
    ProjectDTO projectDTO;

    @GetMapping("/add-project")
    public ResponseEntity<Map<String, Object>> addProjectForm() {
        Map<String, Object> response = new HashMap<>();

        response.put("activeMenu", "projects");
        response.put("activePage", "projects");

        projectDTO = new ProjectDTO();
        response.put("project", projectDTO);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/save-project")
    public ResponseEntity<ProjectDTO> saveProject(@Valid @RequestBody ProjectRequestDTO projectRequestDTO) {

        String statusName = "Not Started";

        ProjectStatus status = projectStatusService.getStatusByName(statusName)
                .orElseThrow(() -> new RuntimeException("Project status not found"));

        projectDTO = projectService.saveProject(projectRequestDTO, status.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(projectDTO);

    }

    @GetMapping("/view-projects")
    public ResponseEntity<List<ProjectResponseDTO>> getAllProjects() {
        List<ProjectResponseDTO> projects = projectService.getAllProjects();

        return ResponseEntity.ok(projects);
    }

    @GetMapping("/view-project")

    public ResponseEntity<Map<String, Object>> viewProject() {
        Map<String, Object> response = new HashMap<>();

        response.put("activeMenu", "projects");
        response.put("activePage", "project-overview");

        ProjectDTO projectDTO = new ProjectDTO();
        response.put("project", projectDTO);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search-project")
    public ResponseEntity<ProjectResponseDTO> searchProject(@RequestParam String projectName) {
        ProjectResponseDTO projectResponseDTO = projectService.getProjectByName(projectName);

        return ResponseEntity.ok(projectResponseDTO);
    }

    @PutMapping("/update-project/{id}")
    public ResponseEntity<ProjectResponseDTO> updateProject(
            @PathVariable("id") Long id,
            @RequestParam("status") Long statusId,
            @RequestBody ProjectRequestDTO projectRequestDTO,
            BindingResult bindingResult) {

        System.out.println("Status Id --> " + statusId);

        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException("Validation error" + bindingResult.toString());
        }

        try {
            ProjectResponseDTO updatedProject = projectService.updateProject(id, projectRequestDTO, statusId);

            return ResponseEntity.ok(updatedProject);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/delete-project/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable Long id) {

        try {
            projectService.deleteProject(id);
            return ResponseEntity.ok("Project deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while deleting project. " + e.getMessage());
        }
    }

    @GetMapping("/status/counts")
    public ResponseEntity<List<ProjectStatusDTO>> getProjectStatusCounts() {
        return ResponseEntity.ok(projectService.getProjectsStatusCount());
    }

}

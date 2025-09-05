package com.swiftedge.projectservice.controller;

import com.swiftedge.dtolibrary.dto.ProjectDTO;
import com.swiftedge.projectservice.dto.ProjectRequestDTO;
import com.swiftedge.projectservice.dto.ProjectStatusDTO;
import com.swiftedge.projectservice.entity.ProjectEntity;
import com.swiftedge.projectservice.service.ProjectService;
import com.swiftedge.projectservice.service.ProjectStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/api/v2/projects")
@RequiredArgsConstructor
@Slf4j
public class ProjectServiceController {

    private final ProjectService projectService;
    private final ProjectStatusService projectStatusService;
    List<ProjectStatusDTO> projectStatus;

//    @GetMapping("/add-project")
//    public String addProject(Model model) {
//        model.addAttribute("activeMenu", "projects");
//        model.addAttribute("activePage", "projects");
//
//        ProjectRequestDTO project = new ProjectRequestDTO();
//        model.addAttribute("project", project);
//
//        return "add-project";
//    }

    @GetMapping("/add-project")
    public ResponseEntity<Map<String, Object>> addProjectForm() {
        Map<String, Object> response = new HashMap<>();

        response.put("activeMenu", "projects");
        response.put("activePage", "projects");

        ProjectDTO projectDTO = new ProjectDTO();
        response.put("project", projectDTO);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/save")
    public String saveProject(@Valid @ModelAttribute("project") ProjectRequestDTO projectRequestDTO,
                              BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Validation failed. Please correct the following errors in your request and try again.");
            return "redirect:/api/v2/projects/add";
        }

        try {

            projectStatus = projectStatusService.getAllProjectStatus();
            String statusName = "Not Started";

            projectStatus.stream()
                            .filter(projectStatus -> projectStatus.getStatusName().equals(statusName))
                                    .findFirst()
                                            .flatMap(status -> {
                                                Long statusId = status.getStatusId();
                                                return projectStatusService.getStatusById(statusId);
                                            })
                                                    .ifPresentOrElse(projectStatus -> {
                                                        log.info("Project status: {}", projectStatus.getId());
                                                        projectService.saveProject(projectRequestDTO, projectStatus.getId());
                                                        redirectAttributes.addFlashAttribute("successMessage", "Project saved successfully.");
                                                    }, () -> {
                                                        log.info("No matching project status found or status ID is invalid.");
                                                    });

        }
        catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error while saving project." + e.getMessage());
        }
        return "redirect:/api/v2/projects/add";
    }

//    @GetMapping("/view-all")
//    public String viewAllEmployees(Model model) {
//        model.addAttribute("activeMenu", "projects");
//        model.addAttribute("activePage", "all-projects");
//
//        List<ProjectResponseDTO> projects = projectService.getAllProjects();
//        model.addAttribute("projectsEntityList", projects);
//
//        return "projects-view-all";
//    }

//    @GetMapping("/all-projects")
//    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
//        System.out.println("getAllProjects: " + projectService.getAllProjects());
//        return ResponseEntity.ok(projectService.getAllProjects());
//    }

    @GetMapping("/all-projects")
    public ResponseEntity<Map<String, Object>> getAllProjects() {
        List<ProjectDTO> projects = projectService.getAllProjects();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", projects.size());
        response.put("projects", projects);

        System.out.println("Project response: \n" + response);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/edit")
    public String projectOverview(Model model) {
        model.addAttribute("activeMenu", "projects");
        model.addAttribute("activePage", "project-overview");

        return "project-overview";
    }

    @GetMapping("/search")
    public String searchProjects(@ModelAttribute("projectRequestDTO")ProjectRequestDTO projectRequestDTO, Model model) {

        log.info("Searching projects for project named {}", projectRequestDTO.getProjectName());
        Optional<ProjectEntity> projectEntity = projectService.searchProjectByName(projectRequestDTO);


        projectStatus = projectStatusService.getAllProjectStatus();
        log.info("Searching projects with status {}", projectStatus.listIterator().next().getStatusName());
        model.addAttribute("statusList", projectStatus);


        model.addAttribute("activeMenu", "projects");
        model.addAttribute("activePage", "project-overview");

        if (projectEntity.isPresent()) {
            projectEntity.ifPresent(project -> {
                model.addAttribute("projectId", project.getProjectId());
                model.addAttribute("projectName", project.getProjectName());
                model.addAttribute("startDate", project.getStartDate());
                model.addAttribute("duration", project.getDuration());
                model.addAttribute("description", project.getDescription());

                Long currentStatus = project.getStatus().getId();
                String statusName = project.getStatus().getStatus();
                log.info("\nStatus Id: {} \nStatus: {}", currentStatus, statusName);

                model.addAttribute("selectedStatus", currentStatus);
                model.addAttribute("projectStatus", statusName);

            });
        } else {
            model.addAttribute("errorMessage", "Project not found.");
        }

        return "project-overview";
    }

    @PostMapping("/update/{id}")
    public String updateProject(
            @PathVariable("id") Long id,
            @RequestParam("projectStatus") Long statusId,
            @ModelAttribute("projectRequestDTO") ProjectRequestDTO projectRequestDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        System.out.println("Status Id --> " + statusId);

        if (bindingResult.hasErrors() || projectRequestDTO.getProjectName() == null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Validation errors occurred. Please resolve and try again.");
            return "redirect:/api/v2/projects/edit";
        }

        try {
            boolean isUpdated = projectService.updateProject(id, projectRequestDTO, statusId);

            if (isUpdated) {
                redirectAttributes.addFlashAttribute("successMessage",
                        "Project updated successfully.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Error while updating project.");
            }
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", "Error updating project: " + e.getMessage());
        }
        return "redirect:/api/v2/projects/edit";
    }

    @PostMapping("/delete/{id}")
    public String deleteProject(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            projectService.deleteProject(id);
            redirectAttributes.addFlashAttribute("successMessage", "Project deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error while deleting project.");
        }
        return "redirect:/api/v2/projects/edit";
    }

    @GetMapping("/status/counts")
    public ResponseEntity<List<ProjectStatusDTO>> getProjectStatusCounts() {
        return ResponseEntity.ok(projectService.getProjectsStatusCount());
    }

}

package com.swiftedge.frontendservice.controller.project;

import com.swiftedge.dtolibrary.dto.ProjectDTO;
import com.swiftedge.dtolibrary.dto.ProjectResponseDTO;
import com.swiftedge.frontendservice.dto.project.ProjectFormDTO;
import com.swiftedge.frontendservice.service.project.ProjectClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectClient projectClient;

    @GetMapping("/add-project")
    public String showAddProject(Model model) {

        ProjectResponseDTO projectDTO = projectClient.projectForm("/add-project");

        model.addAttribute("activeMenu", projectDTO.getActiveMenu());
        model.addAttribute("activePage", projectDTO.getActivePage());
        model.addAttribute("project", new ProjectResponseDTO());

        return "add-project";
    }

    @PostMapping("/save-project")
    public String saveProject(
            @ModelAttribute("project") ProjectFormDTO projectFormDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Validation failed. Please correct the errors and try again.");
            redirectAttributes.addFlashAttribute("project", projectFormDTO); // retain form values
            return "redirect:/projects/add-project";
        }

        try {

            ProjectDTO savedProject = projectClient.saveProject("/save-project", projectFormDTO);

            if (savedProject != null) {
                // Success message
                redirectAttributes.addFlashAttribute("successMessage",
                        "Project '" + savedProject.getProjectName() + "' saved successfully.");

                // Retain saved project for the form (optional)
                redirectAttributes.addFlashAttribute("project", savedProject);
            } else {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Failed to save project. Please try again.");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error while saving project: " + e.getMessage());
            redirectAttributes.addFlashAttribute("project", projectFormDTO); // retain form values
        }

        return "redirect:/projects/add-project";
    }

    @GetMapping("/view-projects")
    public String allProjects(Model model) {
        List<ProjectResponseDTO> projects = projectClient.getAllProjects("/view-projects");

        model.addAttribute("activeMenu", "projects");
        model.addAttribute("activePage", "all-projects");

        model.addAttribute("projects", projects);

        return "projects-view-all";
    }

    @GetMapping("/view-project")
    public String viewProject(Model model) {

        ProjectResponseDTO projectDTO = projectClient.projectForm("/view-project");

        System.out.println("Active page: " + projectDTO.getActivePage());

        model.addAttribute("activeMenu", projectDTO.getActiveMenu());
        model.addAttribute("activePage", projectDTO.getActivePage());
        model.addAttribute("project", new ProjectResponseDTO());

        return "view-project";
    }

    @GetMapping("/search-project")
    public String searchProject(@RequestParam("projectName") String projectName,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            ProjectResponseDTO projectDTO = projectClient.searchProject("/search-project", projectName);

            if (projectDTO == null) {
                redirectAttributes.addFlashAttribute("infoMessage",
                        "Project with name '" + projectName + "' does not exist.");
                return "redirect:/projects/view-project";
            }

            Long currentStatus = projectDTO.getCurrentStatus() != null
                    ? projectDTO.getCurrentStatus().getStatusId()
                    : null;

            if (projectDTO.getStatuses() != null && !projectDTO.getStatuses().isEmpty()) {
                model.addAttribute("availableStatuses", projectDTO.getStatuses());
            }

            model.addAttribute("activeMenu", "projects");
            model.addAttribute("activePage", "project-overview");
            model.addAttribute("currentStatus", currentStatus);
            model.addAttribute("project", projectDTO);

            return "view-project";

        } catch (WebClientResponseException.NotFound e) {
            redirectAttributes.addFlashAttribute("infoMessage",
                    "Project with name '" + projectName + "' does not exist.");
            return "redirect:/projects/view-project";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error while searching project");
            return "redirect:/projects/view-project";
        }
    }


    @PostMapping("/update-project/{id}")
    public String updateProject(
            @PathVariable("id") Long id,
            @RequestParam("status") Long statusId,
            @ModelAttribute("project") ProjectDTO projectDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Validation failed. Please correct the errors and try again.");

            return "redirect:/projects/view-project";
        }

        try {
            ProjectResponseDTO updateProject = projectClient.updateProject("/update-project/" + id, statusId, projectDTO);

            if (updateProject != null) {
                redirectAttributes.addFlashAttribute("successMessage",
                        "Project '" + updateProject.getProjectName() + "' updated successfully.");
                redirectAttributes.addFlashAttribute("project", updateProject);
            } else {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Failed to update project. Please try again.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error while updating project: " + e.getMessage());
        }

        return "redirect:/projects/view-project";
    }

}
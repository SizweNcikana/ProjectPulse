package com.swiftedge.projectservice.controller;

import com.swiftedge.projectservice.dto.ProjectRequestDTO;
import com.swiftedge.projectservice.dto.ProjectResponseDTO;
import com.swiftedge.projectservice.entity.ProjectEntity;
import com.swiftedge.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api/v2/projects")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/add")
    public String addProject(Model model) {
        model.addAttribute("activeMenu", "basic");
        model.addAttribute("activePage", "projects");

        ProjectRequestDTO project = new ProjectRequestDTO();
        model.addAttribute("project", project);

        return "add-project";
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
            projectService.saveProject(projectRequestDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Project saved successfully.");
        }
        catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error while saving project." + e.getMessage());
        }
        return "redirect:/api/v2/projects/add";
    }

    @GetMapping("/edit")
    public String projectOverview(Model model) {
        model.addAttribute("activeMenu", "basic");
        model.addAttribute("activePage", "project-overview");

        return "project-overview";
    }

    @GetMapping("/search")
    public String searchProjects(@ModelAttribute("projectRequestDTO")ProjectRequestDTO projectRequestDTO, Model model) {

        log.info("Searching projects for project named {}", projectRequestDTO.getProjectName());
        List<ProjectEntity> projectEntity = projectService.searchProjectByName(projectRequestDTO);

        model.addAttribute("activeMenu", "basic");
        model.addAttribute("activePage", "project-overview");

        if (!projectEntity.isEmpty()) {
            model.addAttribute("successMessage", "Project found.");
        } else {
            model.addAttribute("errorMessage", "Project not found.");
        }

        for (ProjectEntity project : projectEntity) {
            model.addAttribute("projectId", project.getProjectId());
            model.addAttribute("projectName", project.getProjectName());
            model.addAttribute("startDate", project.getStartDate());
            model.addAttribute("duration", project.getDuration());
            model.addAttribute("description", project.getDescription());
        }

        return "project-overview";
    }

    @PostMapping("/update/{id}")
    public String updateProject(
            @PathVariable("id") Long id,
            @ModelAttribute("projectRequestDTO") ProjectRequestDTO projectRequestDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors() || projectRequestDTO.getProjectName() == null) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Validation errors occurred. Please resolve and try again.");
            return "redirect:/api/v2/projects/edit";
        }
        try {
            boolean isUpdated = projectService.updateProject(id, projectRequestDTO);

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
}

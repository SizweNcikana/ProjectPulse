package com.swiftedge.frontendservice.controller.project;

import com.swiftedge.dtolibrary.dto.ProjectDTO;
import com.swiftedge.frontendservice.dto.project.ProjectFormDTO;
import com.swiftedge.frontendservice.service.project.ProjectClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/projects")
public class ProjectController {

    ProjectFormDTO projectFormDTO;
    private final ProjectClient projectClient;

    @GetMapping("/add-project")
    public String showAddProject(Model model) {

        projectFormDTO = projectClient.projectForm("/add-project");

        model.addAttribute("activeMenu", projectFormDTO.getActiveMenu());
        model.addAttribute("activePage", projectFormDTO.getActivePage());
        model.addAttribute("project", projectFormDTO);

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
        List<ProjectDTO> projects = projectClient.getAllProjects("/view-projects");

        model.addAttribute("activeMenu", "projects");
        model.addAttribute("activePage", "all-projects");

        model.addAttribute("projects", projects);

        return "projects-view-all";
    }

    @GetMapping("/view-project")
    public String viewProject(Model model) {

        projectFormDTO = projectClient.projectForm("/view-project");

        System.out.println("Active page: " + projectFormDTO.getActivePage());

        model.addAttribute("activeMenu", projectFormDTO.getActiveMenu());
        model.addAttribute("activePage", projectFormDTO.getActivePage());
        model.addAttribute("project", projectFormDTO);

        return "view-project";
    }

    @GetMapping("/search-project")
    public String searchProject(@RequestParam("projectName") String projectName,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            System.out.println("Search value: " + projectName);
            ProjectDTO projectDTO = projectClient.searchProject("/search-project", projectName);

            if (projectDTO != null) {
                model.addAttribute("activeMenu", "projects");
                model.addAttribute("activePage", "project-overview");
                model.addAttribute("project", projectDTO);
                return "view-project";
            } else {
                redirectAttributes.addFlashAttribute("infoMessage",
                        "Project with name '" + projectName + "' not found.");
                return "redirect:/projects/view-project";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error while searching project: " + e.getMessage());
            return "redirect:/projects/view-project";
        }

    }

}
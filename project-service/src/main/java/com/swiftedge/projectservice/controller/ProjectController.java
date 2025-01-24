package com.swiftedge.projectservice.controller;

import com.swiftedge.projectservice.dto.ProjectRequestDTO;
import com.swiftedge.projectservice.dto.ProjectResponseDTO;
import com.swiftedge.projectservice.entity.ProjectEntity;
import com.swiftedge.projectservice.repository.ProjectRepository;
import com.swiftedge.projectservice.service.ProjectService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/v2/projects")
@RequiredArgsConstructor
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

}

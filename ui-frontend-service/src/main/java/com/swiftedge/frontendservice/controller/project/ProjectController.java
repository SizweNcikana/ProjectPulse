package com.swiftedge.frontendservice.controller.project;

import com.swiftedge.frontendservice.dto.project.ProjectFormDTO;
import com.swiftedge.frontendservice.service.project.ProjectClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

        return "add-project";
    }
}

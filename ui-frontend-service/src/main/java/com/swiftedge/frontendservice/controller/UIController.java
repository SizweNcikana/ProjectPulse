package com.swiftedge.frontendservice.controller;

import com.swiftedge.frontendservice.dto.EmployeeDTO;
import com.swiftedge.frontendservice.dto.ProjectDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
public class UIController {

    private final WebClient.Builder webClientBuilder;

    @GetMapping("/projects")
    public String getProjects(Model model) {
        List<ProjectDTO> projects = webClientBuilder.build()
                .get()
                .uri("http://API-GATEWAY/api/dashboard")
                .retrieve()
                .bodyToFlux(ProjectDTO.class)
                .collectList()
                .block();

        model.addAttribute("projects", projects);
        return "projects";
    }

    @GetMapping("/employees")
    public String getEmployees(Model model) {
        Map<String, Object> dashboardData = webClientBuilder.build()
                .get()
                .uri("http://API-GATEWAY/api/dashboard")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        assert dashboardData != null;
        model.addAttribute("employees", dashboardData.get("employees"));
        model.addAttribute("projects", dashboardData.get("projects"));
        return "employees";
    }
}

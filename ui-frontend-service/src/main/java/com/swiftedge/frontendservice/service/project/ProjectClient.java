package com.swiftedge.frontendservice.service.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swiftedge.dtolibrary.dto.ProjectDTO;
import com.swiftedge.frontendservice.dto.project.ProjectFormDTO;
import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ProjectClient {

    private final WebClient.Builder builder;
    private final String baseUrl = "http://api-gateway/api/v2/projects";

    public ProjectFormDTO projectForm(String uri) {
        return builder.baseUrl(baseUrl)
                .build()
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(ProjectFormDTO.class)
                .block();
    }

    public ProjectDTO saveProject(String uri, ProjectFormDTO projectForm) {
        return builder.baseUrl(baseUrl)
                .build()
                .post()
                .uri(uri)
                .bodyValue(projectForm)
                .retrieve()
                .bodyToMono(ProjectDTO.class)
                .block();
    }

    public List<ProjectDTO> getAllProjects(String uri) {
        return builder.baseUrl(baseUrl)
                .build()
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ProjectDTO>>() {})
                .block();
    }

    public ProjectDTO searchProject(String uri, String projectName) {
        return builder.baseUrl(baseUrl)
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(uri)
                        .queryParam("projectName", projectName)
                        .build())
                .retrieve()
                .bodyToMono(ProjectDTO.class)
                .block();
    }
}

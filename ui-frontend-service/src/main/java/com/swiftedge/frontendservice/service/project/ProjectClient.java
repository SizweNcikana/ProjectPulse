package com.swiftedge.frontendservice.service.project;

import com.swiftedge.dtolibrary.dto.ProjectDTO;
import com.swiftedge.dtolibrary.dto.ProjectResponseDTO;
import com.swiftedge.frontendservice.dto.project.ProjectFormDTO;
import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@AllArgsConstructor
public class ProjectClient {

    private final WebClient.Builder builder;
    private final String baseUrl = "http://api-gateway/api/v2/projects";

    public ProjectResponseDTO projectForm(String uri) {
        return builder.baseUrl(baseUrl)
                .build()
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(ProjectResponseDTO.class)
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

    public List<ProjectResponseDTO> getAllProjects(String uri) {
        return builder.baseUrl(baseUrl)
                .build()
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ProjectResponseDTO>>() {})
                .block();
    }

    public ProjectResponseDTO searchProject(String uri, String projectName) {
        return builder.baseUrl(baseUrl)
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(uri)
                        .queryParam("projectName", projectName)
                        .build())
                .retrieve()
                .bodyToMono(ProjectResponseDTO.class)
                .block();
    }

    public ProjectResponseDTO updateProject(String uri, Long statusId, ProjectDTO projectDTO) {
        return builder.baseUrl(baseUrl)
                .build()
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(uri)
                        .queryParam("status", statusId)
                        .build())
                .bodyValue(projectDTO)
                .retrieve()
                .bodyToMono(ProjectResponseDTO.class)
                .block();
    }
}

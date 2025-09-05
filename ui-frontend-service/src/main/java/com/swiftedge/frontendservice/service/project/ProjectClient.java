package com.swiftedge.frontendservice.service.project;

import com.swiftedge.frontendservice.dto.project.ProjectFormDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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
}

package com.swiftedge.frontendservice.dto.project;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProjectDTO {

    @NotNull
    private Long projectId;

    @NotNull
    private String projectName;
}

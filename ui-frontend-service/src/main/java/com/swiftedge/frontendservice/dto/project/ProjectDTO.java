package com.swiftedge.frontendservice.dto.project;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ProjectDTO {

    @NotNull
    private Long projectId;

    @NotNull
    private String projectName;

    @NotNull
    private String description;

    @NotNull
    private String dateCreated;

    @NotNull
    private ProjectStatusDTO projectStatus;
}

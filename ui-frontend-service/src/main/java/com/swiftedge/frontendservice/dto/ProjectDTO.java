package com.swiftedge.frontendservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {

    @NotNull
    private Long projectId;

    @NotNull
    private String projectName;
}

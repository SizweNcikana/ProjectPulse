package com.swiftedge.employeeservice.dto.project;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDTO {

    @NotNull
    private Long projectId;

    @NotNull
    private String projectName;

    @NotNull
    private String description;

}

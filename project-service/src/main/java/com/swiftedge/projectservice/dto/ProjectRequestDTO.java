package com.swiftedge.projectservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequestDTO {

    private Long projectId;

    @NotBlank(message = "Project name is required.")
    @Size(max = 100, message = "Project name must be at most 100 characters.")
    private String projectName;

    @NotNull(message = "Start date is required.")
    private LocalDate startDate;

    @Positive(message = "Duration must be a positive number.")
    private Integer duration;

    @Size(max = 500, message = "Description must be at most 500 characters.")
    private String description;
}

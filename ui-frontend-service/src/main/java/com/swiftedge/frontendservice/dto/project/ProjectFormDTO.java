package com.swiftedge.frontendservice.dto.project;

import com.swiftedge.dtolibrary.dto.ProjectDTO;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Data
public class ProjectFormDTO {

    private String activeMenu;
    private String activePage;

//    private ProjectDTO projectDTO;
    private Long projectId;
    @NotNull
    private String projectName;

    @NotNull(message = "Start date is required.")
    @FutureOrPresent(message = "Start date cannot be in the past.")
    private LocalDate startDate;
    private Integer duration;
    private String description;
}

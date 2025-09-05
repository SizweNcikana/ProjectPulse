package com.swiftedge.frontendservice.dto.project;

import com.swiftedge.dtolibrary.dto.ProjectDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ProjectFormDTO {

    private String activeMenu;
    private String activePage;

    private ProjectDTO projectDTO;
}

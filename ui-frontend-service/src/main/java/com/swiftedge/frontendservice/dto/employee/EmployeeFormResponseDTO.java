package com.swiftedge.frontendservice.dto.employee;

import com.swiftedge.frontendservice.dto.project.ProjectDTO;
import lombok.Data;

import java.util.List;

@Data
public class EmployeeFormResponseDTO {
    private String activeMenu;
    private String activePage;
    private List<ProjectDTO> projects;
    private EmployeeDTO employee;
}

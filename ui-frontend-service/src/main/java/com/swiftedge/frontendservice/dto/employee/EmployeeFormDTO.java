package com.swiftedge.frontendservice.dto.employee;

import com.swiftedge.dtolibrary.dto.EmployeeDTO;
import com.swiftedge.frontendservice.dto.project.ProjectDTO;
import lombok.Data;

import java.util.List;

@Data
public class EmployeeFormDTO {
    private String activeMenu;
    private String activePage;
    private List<ProjectDTO> projects;
    private EmployeeDTO employee;
}

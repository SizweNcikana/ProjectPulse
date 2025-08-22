package com.swiftedge.frontendservice.dto.employee;

import com.swiftedge.frontendservice.dto.project.ProjectDTO;
import com.swiftedge.frontendservice.dto.status.StatusDTO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class EmployeeResponseDTO {
    private EmployeeDTO employee;
    private StatusDTO status;
    private ProjectDTO project;
    private String successMessage;
    private String errorMessage;
    private Boolean success;

}

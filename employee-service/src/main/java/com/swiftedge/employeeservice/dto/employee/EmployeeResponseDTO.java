package com.swiftedge.employeeservice.dto.employee;

import com.swiftedge.employeeservice.dto.project.ProjectDTO;
import com.swiftedge.employeeservice.dto.status.StatusDTO;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class EmployeeResponseDTO {

    private EmployeeDTO employee;
    private StatusDTO status;
    private ProjectDTO project;

    private String successMessage;
    private String errorMessage;
    private boolean success;
}

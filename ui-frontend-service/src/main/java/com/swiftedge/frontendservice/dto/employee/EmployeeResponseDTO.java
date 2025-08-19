package com.swiftedge.frontendservice.dto.employee;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class EmployeeResponseDTO {
    private String successMessage;
    private String errorMessage;
    private boolean success;

}

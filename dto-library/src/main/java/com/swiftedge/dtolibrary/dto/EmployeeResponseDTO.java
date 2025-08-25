package com.swiftedge.dtolibrary.dto;

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

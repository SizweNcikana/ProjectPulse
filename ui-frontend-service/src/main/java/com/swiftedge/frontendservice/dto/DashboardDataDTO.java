package com.swiftedge.frontendservice.dto;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDataDTO {

    private EmployeeDTO employees;
    private ProjectDTO projects;
}

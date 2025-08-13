package com.swiftedge.frontendservice.dto.dashboard;

import com.swiftedge.frontendservice.dto.employee.EmployeeStatusDTO;
import com.swiftedge.frontendservice.dto.project.ProjectStatusDTO;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDataDTO {

    private String activeMenu;
    private String activePage;
    private EmployeeStatusDTO employees;
    private ProjectStatusDTO projects;
}

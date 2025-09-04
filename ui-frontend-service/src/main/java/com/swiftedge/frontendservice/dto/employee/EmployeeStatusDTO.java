package com.swiftedge.frontendservice.dto.employee;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EmployeeStatusDTO {

    private int newCount;
    private int assignedCount;
    private int totalEmployees;
    private int probationCount;
    private int unassignedCount;
    private int resignedCount;
    private int terminatedCount;

}

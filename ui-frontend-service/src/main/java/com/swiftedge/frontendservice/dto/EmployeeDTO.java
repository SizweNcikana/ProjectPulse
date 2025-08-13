package com.swiftedge.frontendservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EmployeeDTO {

    private int newCount;
    private int assignedCount;
    private int totalEmployees;
    private int probationCount;
    private int unassignedCount;
    private int resignedCount;
    private int terminatedCount;

}

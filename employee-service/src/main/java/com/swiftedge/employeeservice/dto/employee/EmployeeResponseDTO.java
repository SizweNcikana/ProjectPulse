package com.swiftedge.employeeservice.dto.employee;

import com.swiftedge.employeeservice.entity.address.EmployeeAddressEntity;
import com.swiftedge.employeeservice.entity.status.EmployeeStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponseDTO {

    private Long employeeId;;
    private String name;
    private String surname;
    private String email;
    private String number;
    private String IdNumber;
    private LocalDate dob;
    private String occupation;
    private String summary;
    private String experience;
    private Long statusId;
    private EmployeeStatus status;

    private String successMessage;
    private String errorMessage;
    private boolean success;
}

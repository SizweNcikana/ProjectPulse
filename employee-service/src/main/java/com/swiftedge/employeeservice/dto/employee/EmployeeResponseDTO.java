package com.swiftedge.employeeservice.dto.employee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponseDTO {

    private Long id;
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
}

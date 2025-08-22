package com.swiftedge.frontendservice.dto.employee;

import com.swiftedge.frontendservice.dto.address.AddressDTO;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Data
@Getter
@Setter
public class EmployeeDTO {

    @NotBlank(message = "First name is required.")
    @Size(max = 20, message = "First name cannot exceed 20 characters.")
    private String name;

    @NotBlank(message = "First name is required.")
    @Size(max = 20, message = "Last name cannot exceed 20 characters.")
    private String surname;

    @NotBlank(message = "Email is required.")
    @Email(message = "Please provide a valid email address.")
    private String email;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Number must be 10 digits")
    private String number;

    @NotBlank(message = "ID number is required")
    @Size(min = 13, max = 13, message = "ID number must be 13 characters")
    private String IdNumber;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dob;

    @NotBlank(message = "Gender is required")
    private String gender;

    @NotBlank(message = "Ethnicity is required")
    private String ethnicity;

    @NotBlank(message = "Occupation is required")
    @Size(max = 100, message = "Occupation cannot exceed 100 characters")
    private String occupation;

    @NotBlank(message = "Experience is required")
    private String experience;

    @NotBlank(message = "Summary is required")
    @Size(max = 500, message = "Summary cannot exceed 500 characters")
    private String summary;

    private AddressDTO address;

    private Long project;
    private String statusName;

}

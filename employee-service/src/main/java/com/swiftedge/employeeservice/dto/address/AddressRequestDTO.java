package com.swiftedge.employeeservice.dto.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.stereotype.Service;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequestDTO {

    @NotBlank(message = "Employee City required.")
    @Size(max = 30, message = "City cannot exceed 30 characters.")
    private String city;

    @NotBlank(message = "Employee suburb required.")
    @Size(max = 20, message = "Suburb cannot exceed 20 characters.")
    private String suburb;

    @NotBlank(message = "Employee address required.")
    @Size(max = 30, message = "Street address cannot exceed 30 characters.")
    private String streetAddress;

    @NotBlank(message = "Employee zip code required.")
    @Size(max = 10, message = "Zip Code cannot exceed 10 characters.")
    private String zipCode;
}

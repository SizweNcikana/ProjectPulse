package com.swiftedge.employeeservice.dto.address;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponseDTO {

    private String city;
    private String suburb;
    private String streetAddress;
    private String zipCode;
}

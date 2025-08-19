package com.swiftedge.employeeservice.dto.address;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponseDTO {

    private String city;
    private String suburb;
    private String streetAddress;
    private String zipCode;
}

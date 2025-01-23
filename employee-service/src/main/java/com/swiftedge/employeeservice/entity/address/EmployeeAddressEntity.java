package com.swiftedge.employeeservice.entity.address;

import com.swiftedge.employeeservice.entity.employee.EmployeeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "addresses")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class EmployeeAddressEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String city;
    private String suburb;
    private String streetAddress;
    private String zipCode;

    @OneToOne(mappedBy = "address")
    private EmployeeEntity employee;
}

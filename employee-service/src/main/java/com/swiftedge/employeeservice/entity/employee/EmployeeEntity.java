package com.swiftedge.employeeservice.entity.employee;

import com.swiftedge.employeeservice.entity.address.EmployeeAddressEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    //@Column(name = "employee_id")
    private Long employeeId;
    private String name;
    private String surname;
    private String email;
    private String number;
    private String IdNumber;
    private LocalDate dob;
    private String gender;
    private String ethnicity;
    private String occupation;

    private String experience;
    private String summary;

    // One-to-One relationship with Address
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private EmployeeAddressEntity address;

}

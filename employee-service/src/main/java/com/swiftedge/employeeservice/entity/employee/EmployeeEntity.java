package com.swiftedge.employeeservice.entity.employee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.swiftedge.employeeservice.entity.address.EmployeeAddressEntity;
import com.swiftedge.employeeservice.entity.status.EmployeeStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_seq")
    @SequenceGenerator(name = "employee_seq", sequenceName = "employee_sequence", allocationSize = 1)

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
    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private EmployeeAddressEntity address;

    /*
    Many-to-one relationship with Status. Many employees can have the same status.
    Cannot implement a one-to-one relationship here as it enforces a status to only be associated with
    one employee
     */
    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "status_id")
    private EmployeeStatus status;

    /*
    Cannot create '@OneToMany' relationship here
    as this causes the microservices to be dependent
    on each other. This is referred to as 'Cyclic dependency'
    hence we just created the below column.
     */
    @JsonIgnore
    private Long projectId;
}

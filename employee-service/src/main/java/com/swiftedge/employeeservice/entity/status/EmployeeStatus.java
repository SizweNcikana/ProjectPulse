package com.swiftedge.employeeservice.entity.status;

import com.swiftedge.employeeservice.entity.employee.EmployeeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "statuses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "status_seq")
    @SequenceGenerator(name = "status_seq", sequenceName = "status_sequence", allocationSize = 1)

    private Long id;
    private String status;

    @OneToOne(mappedBy = "status")
    private EmployeeEntity employee;
}

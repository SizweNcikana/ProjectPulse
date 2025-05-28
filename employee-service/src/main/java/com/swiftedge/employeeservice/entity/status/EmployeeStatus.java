package com.swiftedge.employeeservice.entity.status;

import com.swiftedge.employeeservice.entity.employee.EmployeeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

    /*
    Can be used to view all employees that have a specific status
     */
    @OneToMany(mappedBy = "status")
    private List<EmployeeEntity> employee;

}

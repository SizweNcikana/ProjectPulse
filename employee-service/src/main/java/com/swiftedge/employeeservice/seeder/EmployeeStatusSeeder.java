package com.swiftedge.employeeservice.seeder;

import com.swiftedge.employeeservice.entity.status.EmployeeStatus;
import com.swiftedge.employeeservice.repository.employee.EmployeeRepository;
import com.swiftedge.employeeservice.repository.status.StatusRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter

@Component
public class EmployeeStatusSeeder implements CommandLineRunner {

    private final StatusRepository statusRepository;

    @Override
    public void run(String... args) {

        List<String> defaultStatuses = List.of("NEW", "ASSIGNED", "UNASSIGNED",
                "PROBATION", "TERMINATED", "RESIGNED");

        for (String statusName : defaultStatuses) {
            boolean exists = statusRepository.existsByStatus(statusName);
            if (!exists) {
                EmployeeStatus employeeStatus = new EmployeeStatus();
                employeeStatus.setStatus(statusName);
                statusRepository.save(employeeStatus);
            }
        }
    }
}

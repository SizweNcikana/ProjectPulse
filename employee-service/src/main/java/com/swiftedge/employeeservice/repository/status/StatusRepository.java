package com.swiftedge.employeeservice.repository.status;

import com.swiftedge.employeeservice.entity.status.EmployeeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatusRepository extends JpaRepository<EmployeeStatus, Long> {
    boolean existsByStatus(String status);

    Optional<EmployeeStatus> findByStatus(String name);
}

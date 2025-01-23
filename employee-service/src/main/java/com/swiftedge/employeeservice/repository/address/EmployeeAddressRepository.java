package com.swiftedge.employeeservice.repository.address;

import com.swiftedge.employeeservice.entity.address.EmployeeAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeAddressRepository extends JpaRepository<EmployeeAddressEntity, Long> {
}

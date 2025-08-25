package com.swiftedge.employeeservice.repository.employee;

import com.swiftedge.dtolibrary.dto.StatusDTO;
import com.swiftedge.employeeservice.entity.address.EmployeeAddressEntity;
import com.swiftedge.employeeservice.entity.employee.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {
    Optional<EmployeeEntity> findByEmail(String email);

    @Query("SELECT e FROM EmployeeEntity e INNER JOIN e.address WHERE e.name = :name AND e.surname = :surname")
    List<EmployeeEntity> findByNameAndSurname(@Param("name") String name, @Param("surname") String surname);

    @Query("SELECT e.address FROM EmployeeEntity e WHERE e.name = :name AND e.surname = :surname")
    List<EmployeeAddressEntity> findAddressByNameAndSurname(@Param("name") String name, @Param("surname") String surname);

    @Query("SELECT new com.swiftedge.dtolibrary.dto.StatusDTO(s.id, s.status, COUNT(p)) " +
            "FROM EmployeeEntity p JOIN p.status s GROUP BY s.id, s.status")
    List<StatusDTO> countEmployeesGroupedByStatus();
}

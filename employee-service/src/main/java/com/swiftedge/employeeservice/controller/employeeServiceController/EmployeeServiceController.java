package com.swiftedge.employeeservice.controller.employeeServiceController;

import com.swiftedge.dtolibrary.dto.*;
import com.swiftedge.employeeservice.entity.address.EmployeeAddressEntity;
import com.swiftedge.employeeservice.entity.employee.EmployeeEntity;
import com.swiftedge.employeeservice.service.employee.EmployeeService;
import com.swiftedge.employeeservice.service.status.StatusService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequestMapping("/api/v2/employees")
@Controller
@RequiredArgsConstructor
public class EmployeeServiceController {
    private final EmployeeService employeeService;
    private final StatusService statusService;
    List<ProjectDTO> projectList;
    List<StatusDTO> statuses;
    Long selectedProjectId;

    @GetMapping("/add-employee")
    public ResponseEntity<Map<String, Object>> addEmployeeForm() {
        Map<String, Object> response = new HashMap<>();

        response.put("activeMenu", "employees");
        response.put("activePage", "employees");

        projectList = employeeService.getAllProjectsFromProjectService();
        response.put("projects", projectList);

        EmployeeDTO employee = new EmployeeDTO();
        employee.setAddress(new AddressDTO());
        response.put("employee", employee);

        log.info("Projects: {}", projectList);

        return ResponseEntity.ok(response);

    }

    @PostMapping("/save-employee")
    public ResponseEntity<EmployeeResponseDTO> saveEmployee(@RequestBody EmployeeDTO employeeDTO, ProjectDTO projectDTO) {
        try {
            selectedProjectId = projectDTO.getProjectId();
            String defaultStatus = "NEW";

            StatusDTO employeeStatus = statusService.getAllStatuses().stream()
                    .filter(s -> s.getStatusName().equalsIgnoreCase(defaultStatus))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("No matching status found for: " + defaultStatus));

            log.info("Assigning status '{}' (id={}) to employee", employeeStatus.getStatusName(), employeeStatus.getStatusId());

            EmployeeResponseDTO savedEmployee = employeeService.saveEmployee(
                    employeeDTO,
                    employeeDTO.getProjectId(),
                    employeeStatus.getStatusId()
            );

            return ResponseEntity.ok(savedEmployee);

        } catch (IllegalArgumentException ex) {
            log.error("Failed to save employee: {}", ex.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception ex) {
            log.error("Unexpected error saving employee", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/view-employees")
    public ResponseEntity<List<EmployeeResponseDTO>> viewEmployees() {
        List<EmployeeResponseDTO> employees = employeeService.getAllEmployees();
        for (EmployeeResponseDTO employee : employees) {
            System.out.println("Search results: " + employee);
        }
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/success")
    public String showSuccessPage() {
        return "employee-success";
    }

    @GetMapping("/view-employee")
    public ResponseEntity<Map<String, Object>> viewEmployee() {
        Map<String, Object> response = new HashMap<>();

        response.put("activeMenu", "employees");
        response.put("activePage", "view-employee");

        return ResponseEntity.ok(response);

    }

    @GetMapping("/edit")
    public String editEmployee(Model model) {
        model.addAttribute("activeMenu", "employees");
        model.addAttribute("activePage", "edit-employee");

        projectList = employeeService.getAllProjectsFromProjectService();
        statuses = statusService.getAllStatuses();

        model.addAttribute("projects", projectList);
        model.addAttribute("statuses", statuses);

        return "edit-employee";
    }

    @GetMapping("/search-employee")
    public ResponseEntity<EmployeeSearchResponseDTO> searchEmployee(
            @RequestParam("name") String name,
            @RequestParam("surname") String surname) {

        List<EmployeeEntity> employees = employeeService.searchEmployee(name, surname);
        List<EmployeeAddressEntity> addresses = employeeService.getEmployeeAddress(name, surname);
        List<StatusDTO> statuses = statusService.getAllStatuses();
        List<ProjectDTO> projectList = employeeService.getAllProjectsFromProjectService();

        if (employees.isEmpty()) {
            System.out.println("No matching employees");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        EmployeeEntity employee = employees.get(0);  // single employee

        // Map address
        AddressDTO addressDTO = null;
        if (!addresses.isEmpty()) {
            EmployeeAddressEntity address = addresses.get(0);
            addressDTO = new AddressDTO(
                    address.getCity(),
                    address.getSuburb(),
                    address.getStreetAddress(),
                    address.getZipCode()
            );
        }

        // Assigned project
        ProjectDTO assignedProject = null;
        if (employee.getProjectId() != null) {
            assignedProject = employeeService.getProjectById(employee.getProjectId());
        }

        // EmployeeResponseDTO
        EmployeeResponseDTO employeeDTO = EmployeeResponseDTO.builder()
                .employee(EmployeeDTO.builder()
                        .employeeId(employee.getEmployeeId())
                        .name(employee.getName())
                        .surname(employee.getSurname())
                        .email(employee.getEmail())
                        .number(employee.getNumber())
                        .IdNumber(employee.getIdNumber())
                        .gender(employee.getGender())
                        .dob(employee.getDob())
                        .occupation(employee.getOccupation())
                        .ethnicity(employee.getEthnicity())
                        .experience(employee.getExperience())
                        .summary(employee.getSummary())
                        .address(employee.getAddress() != null ?
                                new AddressDTO(employee.getAddress().getStreetAddress(),
                                        employee.getAddress().getSuburb(),
                                        employee.getAddress().getCity(),
                                        employee.getAddress().getZipCode()) : null)
                        .status(employee.getStatus() != null ?
                                new StatusDTO(employee.getStatus().getId(),
                                        employee.getStatus().getStatus(), 0L) : null)
                        .build())
                .success(true)
                .successMessage("Employee found")
                .build();

        // Build final response DTO
        EmployeeSearchResponseDTO responseDTO = EmployeeSearchResponseDTO.builder()
                .employee(employeeDTO)
                .address(addressDTO)
                .statuses(statuses)
                .availableProjects(projectList.stream()
                        .sorted(Comparator.comparing(ProjectDTO::getProjectName))
                        .collect(Collectors.toList()))
                .assignedProject(assignedProject)
                .build();

        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/update-employee/{id}")
    public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable Long id, @RequestBody EmployeeDTO employeeDTO) {

        try {
            EmployeeDTO updatedEmployee = employeeService.updateEmployee(id, employeeDTO);
            return ResponseEntity.ok(updatedEmployee);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/assign-project/{employeeId}")
    public ResponseEntity<EmployeeDTO> assignProjectToEmployee(@PathVariable Long employeeId,
                                                               @RequestBody EmployeeDTO employeeDTO) {

        EmployeeDTO updated = employeeService.updateAssignedEmployeeProject(employeeId, employeeDTO.getProjectId());

        return updated != null
                ? ResponseEntity.ok(updated)
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete-employee/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id) {

        try {
            employeeService.deleteEmployee(id);
            return ResponseEntity.ok("Employee deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while deleting employee. " + e.getMessage());
        }
    }
}
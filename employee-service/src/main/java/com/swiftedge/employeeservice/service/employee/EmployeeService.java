package com.swiftedge.employeeservice.service.employee;

import com.swiftedge.dtolibrary.dto.*;
import com.swiftedge.employeeservice.entity.address.EmployeeAddressEntity;
import com.swiftedge.employeeservice.entity.employee.EmployeeEntity;
import com.swiftedge.employeeservice.entity.status.EmployeeStatus;
import com.swiftedge.employeeservice.exceptions.StatusNotFoundException;
import com.swiftedge.employeeservice.repository.employee.EmployeeRepository;
import com.swiftedge.employeeservice.repository.address.EmployeeAddressRepository;
import com.swiftedge.employeeservice.service.status.StatusService;
import jakarta.persistence.EntityNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Getter
@Setter
@Slf4j
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final EmployeeAddressRepository employeeAddressRepository;
    private final StatusService statusService;
    private final WebClient.Builder webClientBuilder;

    EmployeeEntity existingEmployee;
    ProjectDTO projectDTO;

    @Transactional
    public EmployeeResponseDTO saveEmployee(EmployeeDTO employeeDTO, Long projectId, Long statusId) {

        try {
            //Employee Entity
            EmployeeEntity employeeEntity = new EmployeeEntity();

            employeeRepository.findByEmail(employeeDTO.getEmail())
                    .ifPresent(e -> {
                        throw new IllegalArgumentException("Email '" + employeeDTO.getEmail() + "' already exists");
                    });

            employeeEntity.setName(employeeDTO.getName());
            employeeEntity.setSurname(employeeDTO.getSurname());
            employeeEntity.setEmail(employeeDTO.getEmail());
            employeeEntity.setNumber(employeeDTO.getNumber());
            employeeEntity.setIdNumber(employeeDTO.getIdNumber());
            employeeEntity.setDob(employeeDTO.getDob());
            employeeEntity.setGender(employeeDTO.getGender());
            employeeEntity.setEthnicity(employeeDTO.getEthnicity());
            employeeEntity.setOccupation(employeeDTO.getOccupation());
            employeeEntity.setExperience(employeeDTO.getExperience());
            employeeEntity.setSummary(employeeDTO.getSummary());

            employeeEntity.setProjectId(projectId);

            //Address Entity
            if (employeeDTO.getAddress() != null) {
                EmployeeAddressEntity employeeAddressEntity = new EmployeeAddressEntity();

                employeeAddressEntity.setStreetAddress(employeeDTO.getAddress().getStreetAddress());
                employeeAddressEntity.setCity(employeeDTO.getAddress().getCity());
                employeeAddressEntity.setSuburb(employeeDTO.getAddress().getSuburb());
                employeeAddressEntity.setZipCode(employeeDTO.getAddress().getZipCode());
                employeeEntity.setAddress(employeeAddressEntity);
            }

            EmployeeStatus status = statusService.getStatusById(statusId)
                    .orElseThrow(() -> new RuntimeException("Status with ID " + statusId + " not found"));
            employeeEntity.setStatus(status);

            EmployeeEntity saved = employeeRepository.save(employeeEntity);

            // Mapping back to DTO for response
            EmployeeDTO savedEmployeeDTO = new EmployeeDTO(
                    saved.getEmployeeId(),
                    saved.getName(),
                    saved.getSurname(),
                    saved.getEmail(),
                    saved.getNumber(),
                    saved.getIdNumber(),
                    saved.getDob(),
                    saved.getGender(),
                    saved.getEthnicity(),
                    saved.getOccupation(),
                    saved.getExperience(),
                    saved.getSummary(),
                    employeeDTO.getAddress(),
                    (saved.getProjectId() != null ? saved.getProjectId() : null),
                    saved.getStatus() != null ? new StatusDTO(
                            saved.getStatus().getId(),
                            saved.getStatus().getStatus(),
                            0L
                    ) : null

            );

            StatusDTO statusDTO = new StatusDTO(status.getId(), status.getStatus(), 1L);

            return new EmployeeResponseDTO(
                    savedEmployeeDTO,
                    statusDTO,
                    projectDTO,
                    "Employee saved successfully",
                    null,
                    true
            );

        } catch (Exception e) {
            return new EmployeeResponseDTO(
                    null,
                    null,
                    null,
                    null,
                    "Failed to save employee: " + e.getMessage(),
                    false
            );
        }
    }

    public List<EmployeeResponseDTO> getAllEmployees() {
        List<EmployeeEntity> employees = employeeRepository.findAll();

        return employees.stream()
                .map(e -> {
                    EmployeeDTO employeeDTO = new EmployeeDTO(
                            e.getEmployeeId(),
                            e.getName(),
                            e.getSurname(),
                            e.getEmail(),
                            e.getNumber(),
                            e.getIdNumber(),
                            e.getDob(),
                            e.getGender(),
                            e.getEthnicity(),
                            e.getOccupation(),
                            e.getExperience(),
                            e.getSummary(),
                            e.getAddress() != null ? new AddressDTO(
                                    e.getAddress().getCity(),
                                    e.getAddress().getSuburb(),
                                    e.getAddress().getStreetAddress(),
                                    e.getAddress().getZipCode()
                            ) : null,
                            (e.getProjectId() != null ? e.getProjectId() : null),
                            e.getStatus() != null ? new StatusDTO(
                                    e.getStatus().getId(),
                                    e.getStatus().getStatus(),
                                    0L
                            ) : null
                    );

                    // map Status to StatusDTO
                    StatusDTO statusDTO = (e.getStatus() != null)
                            ? new StatusDTO(e.getStatus().getId(), e.getStatus().getStatus(), 1L) : null;

                    return new EmployeeResponseDTO(
                            employeeDTO,
                            statusDTO,
                            projectDTO,
                            "Employee fetched successfully",
                            null,
                            true
                    );
                }).collect(Collectors.toList());
    }

    public List<EmployeeEntity> searchEmployee(String name, String surname) {
        return employeeRepository.findByNameAndSurname(name, surname);
    }

    public List<EmployeeAddressEntity> getEmployeeAddress(String name, String surname) {
        return employeeRepository.findAddressByNameAndSurname(name, surname);
    }

    @Transactional
    public EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO) {
        System.out.println("Here.....");

        existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee with ID " + id + " not found"));


        if (!existingEmployee.getName().equals(employeeDTO.getName())) {
            validateName(employeeDTO.getName());
            existingEmployee.setName(employeeDTO.getName());
        }

        if (!existingEmployee.getSurname().equals(employeeDTO.getSurname())) {
            validateSurname(employeeDTO.getSurname());
            existingEmployee.setSurname(employeeDTO.getSurname());
        }

        if (!existingEmployee.getEmail().equals(employeeDTO.getEmail())) {
            validateEmail(employeeDTO.getEmail());
            existingEmployee.setEmail(employeeDTO.getEmail());
        }

        if (!existingEmployee.getNumber().equals(employeeDTO.getNumber())) {
            validatePhoneNumber(employeeDTO.getNumber());
            existingEmployee.setNumber(employeeDTO.getNumber());
        }

        if (!existingEmployee.getOccupation().equals(employeeDTO.getOccupation())) {
            validateOccupation(employeeDTO.getOccupation());
            existingEmployee.setOccupation(employeeDTO.getOccupation());
        }

        if (!existingEmployee.getExperience().equals(employeeDTO.getExperience())) {
            validateExperience(employeeDTO.getExperience());
            existingEmployee.setExperience(employeeDTO.getExperience());
        }

        if (!existingEmployee.getSummary().equals(employeeDTO.getSummary())) {
            validateSummary(employeeDTO.getSummary());
            existingEmployee.setSummary(employeeDTO.getSummary());
        }

        if (employeeDTO.getStatus() != null) {
            EmployeeStatus newStatus = statusService.getStatusById(employeeDTO.getStatus().getStatusId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid status ID"));
            existingEmployee.setStatus(newStatus);
        }

        if (employeeDTO.getAddress() != null) {
            EmployeeAddressEntity addressEntity = existingEmployee.getAddress();
            if (addressEntity == null) {
                addressEntity = new EmployeeAddressEntity();
            }
            addressEntity.setStreetAddress(employeeDTO.getAddress().getStreetAddress());
            addressEntity.setCity(employeeDTO.getAddress().getCity());
            addressEntity.setSuburb(employeeDTO.getAddress().getSuburb());
            addressEntity.setZipCode(employeeDTO.getAddress().getZipCode());
        }

        //To add Section for Status and Project Update

        EmployeeEntity saved = employeeRepository.save(existingEmployee);

        EmployeeDTO response = new EmployeeDTO(
                saved.getEmployeeId(),
                saved.getName(),
                saved.getSurname(),
                saved.getEmail(),
                saved.getNumber(),
                saved.getIdNumber(),
                saved.getDob(),
                saved.getGender(),
                saved.getEthnicity(),
                saved.getOccupation(),
                saved.getExperience(),
                saved.getSummary(),
                saved.getAddress() != null
                        ? new AddressDTO(
                        saved.getAddress().getSuburb(),
                        saved.getAddress().getCity(),
                        saved.getAddress().getStreetAddress(),
                        saved.getAddress().getZipCode()
                )
                        : null,
                saved.getProjectId(),
                saved.getStatus() != null
                        ? new StatusDTO(
                        saved.getStatus().getId(),
                        saved.getStatus().getStatus(),
                        0L
                )
                        : null
        );

        return response;
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
    }

    private void validateSurname(String surname) {
        if (surname == null || surname.trim().isEmpty()) {
            throw new IllegalArgumentException("Surname cannot be null or empty.");
        }
    }

    private void validateEmail(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format.");
        }

        if (employeeRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email is already in use.");
        }
    }

    private void validatePhoneNumber(String number) {
        if (number == null || !number.matches("^0[0-9]{9}$")) {
            throw new IllegalArgumentException("Phone number must be 10 digits and start with 0.");
        }
    }

    private void validateOccupation(String occupation) {
        if (occupation == null || occupation.trim().isEmpty()) {
            throw new IllegalArgumentException("Occupation cannot be null or empty.");
        }
    }

    private void validateExperience(String experience) {
        if (experience == null || experience.trim().isEmpty()) {
            throw new IllegalArgumentException("Experience cannot be null or empty.");
        }
    }

    private void validateSummary(String summary) {
        if (summary == null || summary.trim().isEmpty()) {
            throw new IllegalArgumentException("Summary cannot be null or empty.");
        }
    }

    public void validateStatus(Long statusId) {
        if (statusId == null || statusId <= 0) {
            throw new IllegalArgumentException("Status id cannot be null or empty.");
        }
    }

    public void updateAssignedEmployeeProject(Long id) {
        existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee with ID " + id + " does not exist."));

        existingEmployee.setProjectId(projectDTO.getProjectId());
        employeeRepository.save(existingEmployee);
    }

    /**
     * Deletes an employee and their associated address
     *
     * @param employeeId -> The id of the employee to be deleted
     */
    public void deleteEmployee(Long employeeId) {
        Optional<EmployeeEntity> employeeOptional = employeeRepository.findById(employeeId);

        if (employeeOptional.isPresent()) {
            employeeRepository.delete(employeeOptional.get());
        } else {
            System.out.println("Employee with ID " + employeeId + "does not exist.");
        }
    }

    String baseUrl = "http://project-service/api/v2/projects";

    public List<ProjectDTO> getAllProjectsFromProjectService() {
        return webClientBuilder.build()
                .get()
                .uri(baseUrl + "/list")  // Call ProjectService
                .retrieve()
                .bodyToFlux(ProjectDTO.class)
                .collectList()
                .block();
    }

    public ProjectDTO getProjectById(Long projectId) {
        return webClientBuilder.build()
                .get()
                .uri(baseUrl + "/" + projectId)
                .retrieve()
                .bodyToMono(ProjectDTO.class)
                .block();
    }

    public List<StatusDTO> fetchProjectStatusCounts () {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(baseUrl + "/status/counts")
                    .retrieve()
                    .bodyToFlux(StatusDTO.class)
                    .collectList()
                    .block();
        }  catch (Exception e) {
            log.error("Error while fetching status counts. \n {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<StatusDTO> fetchEmployeeStatusCount () {
        return employeeRepository.countEmployeesGroupedByStatus();
    }

}

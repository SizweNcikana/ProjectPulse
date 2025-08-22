package com.swiftedge.employeeservice.service.employee;

import com.swiftedge.employeeservice.dto.address.AddressDTO;
import com.swiftedge.employeeservice.dto.employee.EmployeeDTO;
import com.swiftedge.employeeservice.dto.employee.EmployeeResponseDTO;
import com.swiftedge.employeeservice.dto.project.ProjectDTO;
import com.swiftedge.employeeservice.dto.status.StatusDTO;
import com.swiftedge.employeeservice.entity.address.EmployeeAddressEntity;
import com.swiftedge.employeeservice.entity.employee.EmployeeEntity;
import com.swiftedge.employeeservice.entity.status.EmployeeStatus;
import com.swiftedge.employeeservice.exceptions.StatusNotFoundException;
import com.swiftedge.employeeservice.repository.employee.EmployeeRepository;
import com.swiftedge.employeeservice.repository.address.EmployeeAddressRepository;
import com.swiftedge.employeeservice.service.status.StatusService;
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

//                employeeAddressRepository.save(employeeAddressEntity);
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
                    saved.getStatus().getStatus()

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
                            e.getStatus().getStatus()
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

    public boolean updateEmployee(Long id, EmployeeDTO employeeDTO, AddressDTO addressDTO, Long statusId) {

        existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee with ID " + id + " does not exist."));

        boolean isUpdated = false;

        if (!existingEmployee.getName().equals(employeeDTO.getName())) {
            validateName(employeeDTO.getName());
            existingEmployee.setName(employeeDTO.getName());
            isUpdated = true;
        }

        if (!existingEmployee.getSurname().equals(employeeDTO.getSurname())) {
            validateSurname(employeeDTO.getSurname());
            existingEmployee.setSurname(employeeDTO.getSurname());
            isUpdated = true;
        }

        if (!existingEmployee.getEmail().equals(employeeDTO.getEmail())) {
            validateEmail(employeeDTO.getEmail());
            existingEmployee.setEmail(employeeDTO.getEmail());
            isUpdated = true;
        }

        if (!existingEmployee.getNumber().equals(employeeDTO.getNumber())) {
            validatePhoneNumber(employeeDTO.getNumber());
            existingEmployee.setNumber(employeeDTO.getNumber());
            isUpdated = true;
        }

        if (!existingEmployee.getOccupation().equals(employeeDTO.getOccupation())) {
            validateOccupation(employeeDTO.getOccupation());
            existingEmployee.setOccupation(employeeDTO.getOccupation());
            isUpdated = true;
        }

        if (!existingEmployee.getExperience().equals(employeeDTO.getExperience())) {
            validateExperience(employeeDTO.getExperience());
            existingEmployee.setExperience(employeeDTO.getExperience());
            isUpdated = true;
        }

        if (!existingEmployee.getSummary().equals(employeeDTO.getSummary())) {
            validateSummary(employeeDTO.getSummary());
            existingEmployee.setSummary(employeeDTO.getSummary());
            isUpdated = true;
        }

        EmployeeStatus newStatus = statusService.getStatusById(statusId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid status ID"));

        EmployeeStatus currentStatus = existingEmployee.getStatus();

        System.out.println("New status ID: " + newStatus.getId());


        if (currentStatus == null) {
            throw new StatusNotFoundException("Employee status not found");
        } else if (currentStatus.getId().equals(newStatus.getId())) {
            System.out.println("Status is the same");
        } else {
            existingEmployee.setStatus(newStatus);
            isUpdated = true;
        }

        //Save employee Address
        EmployeeAddressEntity addressEntity = existingEmployee.getAddress();
        if (addressEntity != null) {
            if (!addressEntity.getStreetAddress().equals(addressDTO.getStreetAddress())) {
                addressEntity.setStreetAddress(addressDTO.getStreetAddress());
                isUpdated = true;
            }

            if (!addressEntity.getCity().equals(addressDTO.getCity())) {
                addressEntity.setCity(addressDTO.getCity());
                isUpdated = true;
            }

            if (!addressEntity.getSuburb().equals(addressDTO.getSuburb())) {
                addressEntity.setSuburb(addressDTO.getSuburb());
                isUpdated = true;
            }

            if (!addressEntity.getZipCode().equals(addressDTO.getZipCode())) {
                addressEntity.setZipCode(addressDTO.getZipCode());
                isUpdated = true;
            }
            existingEmployee.setAddress(addressEntity);
        }

        if (isUpdated) {
            employeeRepository.save(existingEmployee);
        }

        return isUpdated;
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

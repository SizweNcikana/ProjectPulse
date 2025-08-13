package com.swiftedge.employeeservice.service.employee;

import com.swiftedge.employeeservice.dto.address.AddressRequestDTO;
import com.swiftedge.employeeservice.dto.employee.EmployeeRequestDTO;
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

import java.time.LocalDateTime;
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

    @Transactional
    public void saveEmployee(EmployeeRequestDTO employeeRequestDTO, Long projectId, Long statusId) {
        EmployeeEntity employeeEntity = new EmployeeEntity();
        EmployeeAddressEntity addressEntity = new EmployeeAddressEntity();

        EmployeeStatus status = statusService.getStatusById(statusId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid status ID"));


        employeeRepository.findByEmail(employeeRequestDTO.getEmail())
                .ifPresent(e -> {
                    throw new IllegalArgumentException("Email " + "'" + employeeRequestDTO.getEmail() + "'" + " already exists");
                });

        employeeEntity.setName(employeeRequestDTO.getName());
        employeeEntity.setSurname(employeeRequestDTO.getSurname());
        employeeEntity.setEmail(employeeRequestDTO.getEmail());
        employeeEntity.setNumber(employeeRequestDTO.getNumber());
        employeeEntity.setIdNumber(employeeRequestDTO.getIdNumber());
        employeeEntity.setDob(employeeRequestDTO.getDob());
        employeeEntity.setGender(employeeRequestDTO.getGender());
        employeeEntity.setEthnicity(employeeRequestDTO.getEthnicity());
        employeeEntity.setOccupation(employeeRequestDTO.getOccupation());
        employeeEntity.setExperience(employeeRequestDTO.getExperience());
        employeeEntity.setSummary(employeeRequestDTO.getSummary());
        employeeEntity.setStatus(status);

        employeeEntity.setProjectId(projectId);

        addressEntity.setCity(employeeRequestDTO.getAddress().getCity());
        addressEntity.setSuburb(employeeRequestDTO.getAddress().getSuburb());
        addressEntity.setStreetAddress(employeeRequestDTO.getAddress().getStreetAddress());
        addressEntity.setZipCode(employeeRequestDTO.getAddress().getZipCode());

        employeeEntity.setAddress(addressEntity);
        addressEntity.setEmployee(employeeEntity);

        employeeRepository.save(employeeEntity);
    }

    public List<EmployeeResponseDTO> getAllEmployees() {
        List<EmployeeEntity> employees = employeeRepository.findAll();

        // Map each Employee entity to EmployeeResponseDTO
        return employees.stream()
                .map(this::mapToEmployeeResponseDTO)
                .collect(Collectors.toList());
    }

    private EmployeeResponseDTO mapToEmployeeResponseDTO(EmployeeEntity employeeEntity) {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();

        dto.setName(employeeEntity.getName());
        dto.setSurname(employeeEntity.getSurname());
        dto.setEmail(employeeEntity.getEmail());
        dto.setNumber(employeeEntity.getNumber());
        dto.setIdNumber(employeeEntity.getIdNumber());
        dto.setDob(employeeEntity.getDob());
        dto.setOccupation(employeeEntity.getOccupation());
        dto.setStatus(employeeEntity.getStatus());

        return dto;

    }

    public List<EmployeeEntity> searchEmployee(String name, String surname) {
        return employeeRepository.findByNameAndSurname(name, surname);
    }

    public List<EmployeeAddressEntity> getEmployeeAddress(String name, String surname) {
        return employeeRepository.findAddressByNameAndSurname(name, surname);
    }

    public boolean updateEmployee(Long id, EmployeeResponseDTO employeeResponseDTO, AddressRequestDTO addressRequestDTO, Long statusId) {

        existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee with ID " + id + " does not exist."));

        boolean isUpdated = false;

        if (!existingEmployee.getName().equals(employeeResponseDTO.getName())) {
            validateName(employeeResponseDTO.getName());
            existingEmployee.setName(employeeResponseDTO.getName());
            isUpdated = true;
        }

        if (!existingEmployee.getSurname().equals(employeeResponseDTO.getSurname())) {
            validateSurname(employeeResponseDTO.getSurname());
            existingEmployee.setSurname(employeeResponseDTO.getSurname());
            isUpdated = true;
        }

        if (!existingEmployee.getEmail().equals(employeeResponseDTO.getEmail())) {
            validateEmail(employeeResponseDTO.getEmail());
            existingEmployee.setEmail(employeeResponseDTO.getEmail());
            isUpdated = true;
        }

        if (!existingEmployee.getNumber().equals(employeeResponseDTO.getNumber())) {
            validatePhoneNumber(employeeResponseDTO.getNumber());
            existingEmployee.setNumber(employeeResponseDTO.getNumber());
            isUpdated = true;
        }

        if (!existingEmployee.getOccupation().equals(employeeResponseDTO.getOccupation())) {
            validateOccupation(employeeResponseDTO.getOccupation());
            existingEmployee.setOccupation(employeeResponseDTO.getOccupation());
            isUpdated = true;
        }

        if (!existingEmployee.getExperience().equals(employeeResponseDTO.getExperience())) {
            validateExperience(employeeResponseDTO.getExperience());
            existingEmployee.setExperience(employeeResponseDTO.getExperience());
            isUpdated = true;
        }

        if (!existingEmployee.getSummary().equals(employeeResponseDTO.getSummary())) {
            validateSummary(employeeResponseDTO.getSummary());
            existingEmployee.setSummary(employeeResponseDTO.getSummary());
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
            if (!addressEntity.getStreetAddress().equals(addressRequestDTO.getStreetAddress())) {
                addressEntity.setStreetAddress(addressRequestDTO.getStreetAddress());
                isUpdated = true;
            }

            if (!addressEntity.getCity().equals(addressRequestDTO.getCity())) {
                addressEntity.setCity(addressRequestDTO.getCity());
                isUpdated = true;
            }

            if (!addressEntity.getSuburb().equals(addressRequestDTO.getSuburb())) {
                addressEntity.setSuburb(addressRequestDTO.getSuburb());
                isUpdated = true;
            }

            if (!addressEntity.getZipCode().equals(addressRequestDTO.getZipCode())) {
                addressEntity.setZipCode(addressRequestDTO.getZipCode());
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

    public void updateAssignedEmployeeProject(Long id, long projectId) {
        existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee with ID " + id + " does not exist."));

        existingEmployee.setProjectId(projectId);
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

package com.swiftedge.employeeservice.service.status;

import com.swiftedge.employeeservice.dto.status.StatusDTO;
import com.swiftedge.employeeservice.entity.status.EmployeeStatus;
import com.swiftedge.employeeservice.repository.status.StatusRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
@Setter

@Service
public class StatusService {
    private final StatusRepository statusRepository;

    public List<StatusDTO> getAllStatuses() {
        return statusRepository.findAll().stream()
                .map(status -> new StatusDTO(status.getId(),
                        status.getStatus(), 0L))
                .collect(Collectors.toList());
    }

    public Optional<EmployeeStatus> getStatusById(Long id) {
        return statusRepository.findById(id);
    }

    public Optional<EmployeeStatus> getStatusByName(String name) {
        return statusRepository.findByStatus(name);
    }

}

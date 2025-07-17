package com.swiftedge.projectservice.service;

import com.swiftedge.projectservice.dto.ProjectStatusDTO;
import com.swiftedge.projectservice.entity.ProjectStatus;
import com.swiftedge.projectservice.repository.ProjectStatusRepository;
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
public class ProjectStatusService {
    private final ProjectStatusRepository projectStatusRepository;

    public List<ProjectStatusDTO> getAllProjectStatus() {
        return projectStatusRepository.findAll().stream()
                .map(status -> new ProjectStatusDTO(status.getId(),
                        status.getStatus(), 0L))
                .collect(Collectors.toList());
    }

    public Optional<ProjectStatus> getStatusById(Long id) {
        return projectStatusRepository.findById(id);
    }

    public Optional<ProjectStatus> getStatusByName(String statusName) {
        return projectStatusRepository.findByStatus(statusName);
    }
}

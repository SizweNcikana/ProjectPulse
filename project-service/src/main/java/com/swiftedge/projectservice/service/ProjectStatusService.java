package com.swiftedge.projectservice.service;

import com.swiftedge.dtolibrary.dto.StatusDTO;
import com.swiftedge.projectservice.entity.ProjectStatus;
import com.swiftedge.projectservice.repository.ProjectStatusRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Getter
@Setter

@Service
public class ProjectStatusService {
    private final ProjectStatusRepository projectStatusRepository;

    public List<StatusDTO> getAllProjectStatus() {
        return projectStatusRepository.findAll().stream()
                .map(status -> new StatusDTO(
                        status.getId(),
                        status.getStatus(),
                        0L))
                .toList();
    }


    public Optional<ProjectStatus> getStatusById(Long id) {
        return projectStatusRepository.findById(id);
    }

    public Optional<ProjectStatus> getStatusByName(String statusName) {
        return projectStatusRepository.findByStatus(statusName);
    }
}

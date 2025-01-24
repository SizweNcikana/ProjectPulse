package com.swiftedge.projectservice.service;

import com.swiftedge.projectservice.dto.ProjectRequestDTO;
import com.swiftedge.projectservice.dto.ProjectResponseDTO;
import com.swiftedge.projectservice.entity.ProjectEntity;
import com.swiftedge.projectservice.repository.ProjectRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Getter
@Setter

public class ProjectService {

    private final ProjectRepository projectRepository;

    @Transactional
    public void saveProject(ProjectRequestDTO projectRequestDTO) {
        projectRepository.findByProjectName(projectRequestDTO.getProjectName())
                .ifPresent(project -> {
                    throw new IllegalStateException("Project with name '" + projectRequestDTO.getProjectName() + "' already exists");
                    //System.out.println("Saving project " + project.getProjectName());
                });

        ProjectEntity projectEntity = new ProjectEntity();

        projectEntity.setProjectName(projectRequestDTO.getProjectName());
        projectEntity.setStartDate(projectRequestDTO.getStartDate());
        projectEntity.setDuration(projectRequestDTO.getDuration());
        projectEntity.setDescription(projectRequestDTO.getDescription());

        projectRepository.save(projectEntity);
    }

    public List<ProjectResponseDTO> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private ProjectResponseDTO mapToResponseDTO(ProjectEntity projectEntity) {
        ProjectResponseDTO projectResponseDTO = new ProjectResponseDTO();

        projectResponseDTO.setProjectName(projectEntity.getProjectName());
        projectResponseDTO.setStartDate(projectEntity.getStartDate());
        projectResponseDTO.setDuration(projectEntity.getDuration());
        projectResponseDTO.setDescription(projectEntity.getDescription());
        return projectResponseDTO;
    }
}

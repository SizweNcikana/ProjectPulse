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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Getter
@Setter

public class ProjectService {

    private final ProjectRepository projectRepository;

    @Transactional
    public void saveProject(ProjectRequestDTO projectRequestDTO) {
//        projectRepository.findByProjectName(projectRequestDTO.getProjectName())
//                .ifPresent(project -> {
//                    throw new IllegalStateException("Project with name '" + projectRequestDTO.getProjectName() + "' already exists");
//                    //System.out.println("Saving project " + project.getProjectName());
//                });
        List<ProjectEntity> existingProjects = projectRepository.findByProjectName(projectRequestDTO.getProjectName());

        if (!existingProjects.isEmpty()) {
            throw new IllegalStateException("Project with name '" + projectRequestDTO.getProjectName() + "' already exists.");
        }

        ProjectEntity projectEntity = new ProjectEntity();

        projectEntity.setProjectName(projectRequestDTO.getProjectName());
        projectEntity.setStartDate(projectRequestDTO.getStartDate());
        projectEntity.setDuration(projectRequestDTO.getDuration());
        projectEntity.setDescription(projectRequestDTO.getDescription());

        projectRepository.save(projectEntity);
    }

    public List<ProjectResponseDTO> getAllProjects() {

        // Map each Project entity to ProjectResponseDTO
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

    public List<ProjectEntity> searchProjectByName(ProjectRequestDTO projectRequestDTO) {
        if (projectRequestDTO == null || projectRequestDTO.getProjectName().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be empty");
        }

        return projectRepository.findByProjectName(projectRequestDTO.getProjectName());
    }

    public boolean updateProject(Long id, ProjectRequestDTO projectRequestDTO) {
        ProjectEntity existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        boolean updated = false;
        if (!existingProject.getDescription().equals(projectRequestDTO.getDescription())) {
            validateDescription(projectRequestDTO.getDescription());
            existingProject.setDescription(projectRequestDTO.getDescription());
            updated = true;
        }

        if (!existingProject.getProjectName().equals(projectRequestDTO.getProjectName())) {
            validateProjectName(projectRequestDTO.getProjectName());
            existingProject.setProjectName(projectRequestDTO.getProjectName());
            updated = true;
        }
        if (!existingProject.getStartDate().equals(projectRequestDTO.getStartDate())) {
            validateStartDate(projectRequestDTO.getStartDate());
            existingProject.setStartDate(projectRequestDTO.getStartDate());
            updated = true;
        }
        if (!existingProject.getDuration().equals(projectRequestDTO.getDuration())) {
            validateDuration(projectRequestDTO.getDuration());
            existingProject.setDuration(projectRequestDTO.getDuration());
            updated = true;
        }

        if (updated) {
            projectRepository.save(existingProject);
            System.out.println("Updating project with id '" + id + "'" + " with project name '" + projectRequestDTO.getProjectName() + "'");
        }

        return updated;
    }

    private void validateDescription(String description) {
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
    }

    private void validateProjectName(String projectName) {
        if (projectName == null || projectName.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be empty");
        }
    }

    private void validateDuration(Integer duration) {
        if (duration <= 0) {
            throw new IllegalArgumentException("Duration cannot be negative");
        }
    }

    private void validateStartDate(LocalDate startDate) {
        if (startDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Start date cannot be before current date");
        }
    }

    public void deleteProject(Long id) {
        Optional<ProjectEntity> existingProject = projectRepository.findById(id);

        if (existingProject.isPresent()) {
            projectRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Project not found");
        }

    }
}

package com.swiftedge.projectservice.service;

import com.swiftedge.dtolibrary.dto.ProjectDTO;
import com.swiftedge.dtolibrary.dto.ProjectResponseDTO;
import com.swiftedge.dtolibrary.dto.StatusDTO;
import com.swiftedge.projectservice.dto.ProjectRequestDTO;
import com.swiftedge.projectservice.dto.ProjectStatusDTO;
import com.swiftedge.projectservice.entity.ProjectEntity;
import com.swiftedge.projectservice.entity.ProjectStatus;
import com.swiftedge.projectservice.repository.ProjectRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Getter
@Setter

public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectStatusService projectStatusService;

    public ProjectDTO saveProject(ProjectRequestDTO projectRequestDTO, Long statusId) {

        Optional<ProjectEntity> existingProject = projectRepository.findByProjectName(
                projectRequestDTO.getProjectName()
        );

        if (existingProject.isPresent()) {
            throw new IllegalStateException("Project with name '" + projectRequestDTO.getProjectName() + "' already exists.");
        }

        ProjectStatus status = projectStatusService.getStatusById(statusId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid status ID"));

        //Mapping incoming DTO to Entity then save
        ProjectEntity projectEntity = new ProjectEntity();

        projectEntity.setProjectName(projectRequestDTO.getProjectName());
        projectEntity.setStartDate(projectRequestDTO.getStartDate());
        projectEntity.setDuration(projectRequestDTO.getDuration());
        projectEntity.setDescription(projectRequestDTO.getDescription());
        projectEntity.setStatus(status);

        System.out.println("Saving project " + projectEntity.toString());

        ProjectEntity savedProjectEntity = projectRepository.save(projectEntity);

        // Mapping back to DTO
        ProjectDTO projectDTO = new ProjectDTO();

        projectDTO.setProjectId(savedProjectEntity.getProjectId());
        projectDTO.setProjectName(savedProjectEntity.getProjectName());
        projectDTO.setStartDate(savedProjectEntity.getStartDate());
        projectDTO.setDuration(savedProjectEntity.getDuration());
        projectDTO.setDescription(savedProjectEntity.getDescription());
        projectDTO.setStatusName(savedProjectEntity.getStatus().getStatus());

        return projectDTO;
    }

    public List<ProjectResponseDTO> getAllProjects() {

        // Map each Project entity to ProjectResponseDTO
        return projectRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private ProjectResponseDTO mapToResponseDTO(ProjectEntity projectEntity) {
        ProjectResponseDTO projectDTO = new ProjectResponseDTO();

        projectDTO.setProjectId(projectEntity.getProjectId());
        projectDTO.setProjectName(projectEntity.getProjectName());
        projectDTO.setStartDate(projectEntity.getStartDate());
        projectDTO.setDuration(projectEntity.getDuration());
        projectDTO.setDescription(projectEntity.getDescription());

        if (projectEntity.getStatus() != null) {
            StatusDTO currentStatus = new StatusDTO(
                    projectEntity.getStatus().getId(),
                    projectEntity.getStatus().getStatus(),
                    0L
            );
            projectDTO.setCurrentStatus(currentStatus);
        }

        List<StatusDTO> statuses = projectStatusService.getAllProjectStatus().stream()
                .map(status -> new StatusDTO(
                        status.getStatusId(),
                        status.getStatusName(),
                        0L))
                .toList();
        projectDTO.setStatuses(statuses);

        return projectDTO;
    }

    public ProjectResponseDTO updateProject(Long id, ProjectRequestDTO projectRequestDTO, Long statusId) {
        ProjectEntity existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        boolean updated = false;
        if (!Objects.equals(existingProject.getDescription(), projectRequestDTO.getDescription())) {
            validateDescription(projectRequestDTO.getDescription());
            existingProject.setDescription(projectRequestDTO.getDescription());
            updated = true;
        }

        if (!Objects.equals(existingProject.getProjectName(), projectRequestDTO.getProjectName())) {
            validateProjectName(projectRequestDTO.getProjectName());
            existingProject.setProjectName(projectRequestDTO.getProjectName());
            updated = true;
        }
        if (!Objects.equals(existingProject.getStartDate(), projectRequestDTO.getStartDate())) {
            validateStartDate(projectRequestDTO.getStartDate());
            existingProject.setStartDate(projectRequestDTO.getStartDate());
            updated = true;
        }
        if (!Objects.equals(existingProject.getDuration(), projectRequestDTO.getDuration())) {
            validateDuration(projectRequestDTO.getDuration());
            existingProject.setDuration(projectRequestDTO.getDuration());
            updated = true;
        }

        if (statusId != null) {
            ProjectStatus newStatus = projectStatusService.getStatusById(statusId)
                    .orElseThrow(() -> new IllegalArgumentException("Status not found."));

            ProjectStatus currentStatus = existingProject.getStatus();

            if (currentStatus == null) {
                throw new IllegalArgumentException("Project status not found.");
            } else if (!currentStatus.getId().equals(newStatus.getId())) {
                existingProject.setStatus(newStatus);
                updated = true;
            }
        }

        if (updated) {
            existingProject = projectRepository.save(existingProject);
            System.out.printf("Updating project with id '%d' and name '%s'%n",
                    id, projectRequestDTO.getProjectName());
        } else {
            System.out.printf("No changes detected for project with id '%d'%n", id);
        }

        return mapToResponseDTO(existingProject);
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

    public List<ProjectRequestDTO> getProjects() {
        List<ProjectEntity> projects = projectRepository.findAll();
        return projects.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ProjectRequestDTO convertToDTO(ProjectEntity project) {
        return new ProjectRequestDTO(
                project.getProjectId(),
                project.getProjectName(),
                project.getStartDate(),
                project.getDuration(),
                project.getDescription()
        );
    }

    public ProjectResponseDTO getProjectByName(String projectName) {
        Optional<Long> projectIdOpt = projectRepository.findProjectIdByProjectName(projectName);

        if (projectIdOpt.isEmpty()) {
            return null;
        }

        Optional<ProjectEntity> projectEntityOpt = projectRepository.findById(projectIdOpt.get());

        if (projectEntityOpt.isEmpty()) {
            return null;
        }

        ProjectEntity projectEntity = projectEntityOpt.get();

        ProjectResponseDTO projectResponseDTO = new ProjectResponseDTO();
        projectResponseDTO.setProjectId(projectEntity.getProjectId());
        projectResponseDTO.setProjectName(projectEntity.getProjectName());
        projectResponseDTO.setDescription(projectEntity.getDescription());
        projectResponseDTO.setStartDate(projectEntity.getStartDate());
        projectResponseDTO.setDuration(projectEntity.getDuration());

        if (projectEntity.getStatus() != null) {
            projectResponseDTO.setCurrentStatus(new StatusDTO(
                    projectEntity.getStatus().getId(),
                    projectEntity.getStatus().getStatus(),
                    0L
            ));
        }
        projectResponseDTO.setStatuses(projectStatusService.getAllProjectStatus());

        return projectResponseDTO;
    }

    public List<ProjectStatusDTO> getProjectsStatusCount() {
        System.out.println("Project status count" + projectRepository.countProjectsGroupedByStatus());
        return projectRepository.countProjectsGroupedByStatus();
    }

    public Optional<ProjectResponseDTO> getProjectById(Long projectId) {

        Optional<ProjectEntity> existingProject = projectRepository.findById(projectId);

        existingProject.ifPresent(project ->
            log.info("Project ID '{}' is named '{}'", projectId, project.getProjectName())
        );
        return existingProject.map(this::mapToResponseDTO);
    }
}

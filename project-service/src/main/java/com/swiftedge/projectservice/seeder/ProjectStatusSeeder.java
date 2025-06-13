package com.swiftedge.projectservice.seeder;

import com.swiftedge.projectservice.entity.ProjectStatus;
import com.swiftedge.projectservice.repository.ProjectStatusRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Component

public class ProjectStatusSeeder implements CommandLineRunner {
    private final ProjectStatusRepository projectStatusRepository;

    @Override
    public void run(String... args) throws Exception {
        List<String> defaultStatuses = List.of("Not Started", "In Progress", "On Hold", "Cancelled", "Completed");

        for (String statusName : defaultStatuses) {
            boolean exists = projectStatusRepository.existsByStatus(statusName);
            if (!exists) {
                ProjectStatus projectStatus = new ProjectStatus();
                projectStatus.setStatus(statusName);
                projectStatusRepository.save(projectStatus);
            }
        }
    }
}

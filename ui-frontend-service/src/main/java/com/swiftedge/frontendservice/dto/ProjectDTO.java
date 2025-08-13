package com.swiftedge.frontendservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {

    private int onholdCount;
    private int notstartedCount;
    private int totalProjects;
    private int cancelledCount;
    private int completedCount;
    private int inprogressCount;
}

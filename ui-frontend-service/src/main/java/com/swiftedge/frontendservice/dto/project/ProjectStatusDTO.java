package com.swiftedge.frontendservice.dto.project;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectStatusDTO {

    private int onholdCount;
    private int notstartedCount;
    private int totalProjects;
    private int cancelledCount;
    private int completedCount;
    private int inprogressCount;
}

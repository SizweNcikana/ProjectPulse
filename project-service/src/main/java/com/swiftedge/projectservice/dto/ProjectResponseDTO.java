package com.swiftedge.projectservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponseDTO {
    private Long id;
    private String projectName;
    private LocalDate startDate;
    private Integer duration;
    private String description;
}

package com.swiftedge.dtolibrary.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProjectDTO {

    @NotNull
    private Long projectId;

    private String projectName;

    private LocalDate startDate;

    private Integer duration;

    private String description;

    private String statusName;

}
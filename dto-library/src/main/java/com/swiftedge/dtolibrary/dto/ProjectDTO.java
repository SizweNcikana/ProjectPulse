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

    @NotNull
    private String projectName;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private int duration;

    @NotNull
    private String description;

    private String statusName;

}
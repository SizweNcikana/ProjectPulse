package com.swiftedge.dtolibrary.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode

public class ProjectResponseDTO {

    private String activeMenu;
    private String activePage;

    private Long projectId;
    private String projectName;
    private LocalDate startDate;
    private Integer duration;
    private String description;

    private StatusDTO currentStatus;
    private List<StatusDTO> statuses;
}

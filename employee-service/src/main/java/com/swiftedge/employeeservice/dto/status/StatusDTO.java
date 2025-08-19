package com.swiftedge.employeeservice.dto.status;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusDTO {

    private Long statusId;
    private String statusName;
    private Long count;
}

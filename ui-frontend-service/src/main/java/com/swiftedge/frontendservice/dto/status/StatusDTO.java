package com.swiftedge.frontendservice.dto.status;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class StatusDTO {
    private long id;
    private String status;
}

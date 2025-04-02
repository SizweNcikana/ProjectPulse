package com.swiftedge.projectservice.dto.employee;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {

    @NotNull
    private Long id;

    @NotNull
    private String firstName;
}

package com.swiftedge.frontendservice.service;

import com.swiftedge.frontendservice.dto.dashboard.DashboardDataDTO;
import com.swiftedge.frontendservice.dto.employee.EmployeeFormResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@AllArgsConstructor
public class EmployeeClient {
    private final WebClient.Builder builder;

    public EmployeeFormResponseDTO employeeData(String uri) {
        return builder.baseUrl("http://api-gateway/api/v2/employees")
                .build()
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(EmployeeFormResponseDTO.class)
                .block();
    }
}

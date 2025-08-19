package com.swiftedge.frontendservice.service;

import com.swiftedge.frontendservice.dto.employee.EmployeeFormDTO;
import com.swiftedge.frontendservice.dto.employee.EmployeeRequestDTO;
import com.swiftedge.frontendservice.dto.employee.EmployeeResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class EmployeeClient {
    private final WebClient.Builder builder;

    public EmployeeFormDTO employeeData(String uri) {
        return builder.baseUrl("http://api-gateway/api/v2/employees")
                .build()
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(EmployeeFormDTO.class)
                .block();
    }

    public EmployeeResponseDTO saveEmployee(EmployeeRequestDTO employeeRequestDTO, String uri) {
        System.out.println("URI: " + uri);
        return builder.baseUrl("http://api-gateway/api/v2/employees")
                .build()
                .post()
                .uri(uri)
                .bodyValue(employeeRequestDTO)
                .retrieve()
                .bodyToMono(EmployeeResponseDTO.class)
                .block();
    }
}

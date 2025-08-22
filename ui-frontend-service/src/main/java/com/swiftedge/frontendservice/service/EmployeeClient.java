package com.swiftedge.frontendservice.service;

import com.swiftedge.frontendservice.dto.employee.EmployeeFormDTO;
import com.swiftedge.frontendservice.dto.employee.EmployeeDTO;
import com.swiftedge.frontendservice.dto.employee.EmployeeResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@AllArgsConstructor
public class EmployeeClient {
    private final WebClient.Builder builder;
    private final String baseUrl = "http://api-gateway/api/v2/employees";

    public EmployeeFormDTO employeeData(String uri) {

        return builder.baseUrl(baseUrl)
                .build()
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(EmployeeFormDTO.class)
                .block();
    }

    public EmployeeResponseDTO saveEmployee(EmployeeDTO employeeDTO, String uri) {

        return builder.baseUrl(baseUrl)
                .build()
                .post()
                .uri(uri)
                .bodyValue(employeeDTO)
                .retrieve()
                .bodyToMono(EmployeeResponseDTO.class)
                .block();
    }

    public List<EmployeeResponseDTO> getEmployees(String uri) {

        return builder.baseUrl(baseUrl)
                .build()
                .get()
                .uri(uri)
                .retrieve()
                .bodyToFlux(EmployeeResponseDTO.class)
                .collectList()
                .block();
    }
}

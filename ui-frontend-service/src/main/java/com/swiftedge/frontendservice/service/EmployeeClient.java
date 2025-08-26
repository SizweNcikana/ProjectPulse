package com.swiftedge.frontendservice.service;

import com.swiftedge.dtolibrary.dto.EmployeeSearchResponseDTO;
import com.swiftedge.frontendservice.dto.employee.EmployeeFormDTO;
import com.swiftedge.dtolibrary.dto.EmployeeDTO;
import com.swiftedge.dtolibrary.dto.EmployeeResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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

    public EmployeeSearchResponseDTO employeeResponseData(String uri) {

        try {
            return builder.baseUrl(baseUrl)
                    .build()
                    .get()
                    .uri(uri)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                            Mono.error(new RuntimeException("Employee not found.")))
                    .bodyToMono(EmployeeSearchResponseDTO.class)
                    .block();

        } catch (Exception e) {
            System.out.println(e.getMessage());;
        }

        return null;
    }

}

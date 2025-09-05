package com.swiftedge.frontendservice.service.employee;

import com.swiftedge.frontendservice.dto.dashboard.DashboardDataDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@AllArgsConstructor
public class DashboardClient {

    private final WebClient.Builder builder;

    public DashboardDataDTO getDashboardData() {
        return builder.baseUrl("http://api-gateway/api/v2/employees")
                .build()
                .get()
                .uri("/home")
                .retrieve()
                .bodyToMono(DashboardDataDTO.class)
                .block();
    }

}

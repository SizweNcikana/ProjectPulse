package com.swiftedge.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()

                // Route for Employee Service API
                .route("employee-service", r -> r.path("/api/v2/employees/**")
                        .uri("lb://employee-service"))

                // Route for Project Service API
                .route("project-service", r -> r.path("/api/v2/projects/**")
                        .uri("lb://project-service"))

                // Route for UI FRONTEND Static files
                .route("ui-frontend-static", r -> r.path("/static/**", "/js/**", "/css/**", "/images/**")
                        .uri("lb://ui-frontend"))

                // Route for UI FRONTEND UI pages
                .route("ui-frontend", r -> r.path("/home", "/employees", "/**")  // catch-all your UI URLs
                        .uri("lb://ui-frontend"))

                // Route for Discovery Server UI
                .route("discovery-server", r -> r.path("/eureka/web")
                        .filters(f -> f.setPath("/"))
                        .uri("http://localhost:8761"))

                // Route for Discovery Server Static Files
                .route("discovery-server-static", r -> r.path("/eureka/**")
                        .uri("http://localhost:8761"))

                .build();
    }
}

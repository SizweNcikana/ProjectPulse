package com.swiftedge.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.RouteRefreshListener;
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
                        .uri("lb://EMPLOYEE-SERVICE"))

                // Route for Project Service API
                .route("project-service", r -> r.path("/api/v2/projects/**")
                        .uri("lb://PROJECT-SERVICE"))

                // Route for Employee Service Static Files (CSS, JS, Images)
                .route("employee-static", r -> r.path("/static/**", "/js/**", "/css/**", "/images/**", "/icon/**", "/pages/**")
                        .filters(f -> f.rewritePath("/(?<path>.*)", "/${path}"))
                        .uri("lb://EMPLOYEE-SERVICE"))

                // Route for Project Service Static Files (CSS, JS, Images)
                .route("project-static", r -> r.path("/static/**", "/js/**", "/css/**", "/images/**", "/icon/**", "/pages/**")
                        .filters(f -> f.rewritePath("/(?<path>.*)", "/${path}"))
                        .uri("lb://PROJECT-SERVICE"))

                // Route for Employee Service Thymeleaf Templates
                .route("employee-templates", r -> r.path("/templates/**")
                        .filters(f -> f.rewritePath("/templates/(?<path>.*)", "/${path}"))
                        .uri("lb://EMPLOYEE-SERVICE"))

                // Route for Project Service Thymeleaf Templates
                .route("project-templates", r -> r.path("/templates/**")
                        .filters(f -> f.rewritePath("/templates/(?<path>.*)", "/${path}"))
                        .uri("lb://EMPLOYEE-SERVICE"))

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

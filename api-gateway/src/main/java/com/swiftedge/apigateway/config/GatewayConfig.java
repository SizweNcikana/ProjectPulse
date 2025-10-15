package com.swiftedge.apigateway.config;

import com.swiftedge.apigateway.InMemoryRateLimiter;
import io.github.resilience4j.ratelimiter.internal.InMemoryRateLimiterRegistry;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@EnableScheduling
public class GatewayConfig {

    private final Map<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    private final int MAX_REQUESTS = 5;
    private final long WINDOW_MILLIS = 10000;

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("employee-service", r -> r.path("/api/v2/employees/**")
                        .filters(f -> f
                                .requestRateLimiter
                                        (c -> c
                                                .setRateLimiter(inMemoryRateLimiter())
                                                .setKeyResolver(inMemoryKeyResolver()))
                                .circuitBreaker(config -> config
                                        .setName("employeeCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/employee"))
                        )
                        .uri("lb://employee-service"))

                .route("project-service", r -> r
                        .path("/api/v2/projects/**")
                        .filters(f -> f
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(inMemoryRateLimiter())
                                        .setKeyResolver(inMemoryKeyResolver()))
                                .circuitBreaker(config -> config
                                        .setName("projectCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/project"))
                        ).uri("lb://project-service"))

                        // Route for UI FRONTEND Static files
                .route("ui-frontend-static", r -> r
                        .path("/static/**", "/js/**", "/css/**", "/images/**")
                        .uri("lb://ui-frontend"))

                // Route for UI FRONTEND UI pages
                .route("ui-frontend", r -> r
                        .path("/home", "/employees", "/**")  // catch-all your UI URLs
                        .uri("lb://ui-frontend"))

                // Route for Discovery Server UI
                .route("discovery-server", r -> r
                        .path("/eureka/web")
                        .filters(f -> f.setPath("/"))
                        .uri("http://localhost:8761"))

                // Route for Discovery Server Static Files
                .route("discovery-server-static", r -> r
                        .path("/eureka/**")
                        .uri("http://localhost:8761"))

                .build();
    }

    /**
     * Key resolver for in-memory rate limiting.
     * It limits requests per client IP.
     */

    @Bean
    public KeyResolver inMemoryKeyResolver() {
        return exchange -> Mono.just(
                exchange.getRequest()
                        .getRemoteAddress()
                        .getAddress()
                        .getHostAddress()
        );
    }

    // In memory rate limiter
    @Bean
    public InMemoryRateLimiter inMemoryRateLimiter() {
        return new InMemoryRateLimiter(requestCounts, MAX_REQUESTS, WINDOW_MILLIS);
    }
}

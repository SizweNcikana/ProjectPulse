package com.swiftedge.apigateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class GatewayFallbackController {

    @GetMapping("/employee")
    public ResponseEntity<Map<String, Object>> employeeServiceFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "status", "SERVICE_UNAVAILABLE",
                        "message", "Employee Service is currently unavailable. Please try again later.",
                        "timestamp", LocalDateTime.now().toString()
                ));
    }

    @GetMapping("/project")
    public ResponseEntity<Map<String, Object>> projectServiceFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "status", "SERVICE_UNAVAILABLE",
                        "message", "Project Service is currently unavailable. Please try again later.",
                        "timestamp", LocalDateTime.now().toString()
                ));
    }

}

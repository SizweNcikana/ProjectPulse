package com.swiftedge.employeeservice.controller.statusController;

import com.swiftedge.employeeservice.dto.status.StatusDTO;
import com.swiftedge.employeeservice.service.status.StatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@RequestMapping("/api/v2/statuses")

@Controller
@RequiredArgsConstructor
public class StatusController {

    private final StatusService statusService;

    @GetMapping("/list")
    public List<StatusDTO> getEmployeeStatuses() {
        return statusService.getAllStatuses();
    }
}

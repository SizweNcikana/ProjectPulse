package com.swiftedge.frontendservice.controller;

import com.swiftedge.frontendservice.dto.DashboardDataDTO;
import com.swiftedge.frontendservice.dto.EmployeeDTO;
import com.swiftedge.frontendservice.dto.ProjectDTO;
import com.swiftedge.frontendservice.service.DashboardClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class DashboardController {

    private final DashboardClient dashboardClient;

    @GetMapping("/home")
    public String home(Model model) {

        DashboardDataDTO data = dashboardClient.getDashboardData();

        EmployeeDTO employees = data.getEmployees();
        ProjectDTO projects = data.getProjects();

        if (employees != null) {
            model.addAttribute("newCount", employees.getNewCount());
            model.addAttribute("assignedCount", employees.getAssignedCount());
            model.addAttribute("allEmployees", employees.getTotalEmployees());
            model.addAttribute("probationCount", employees.getProbationCount());
            model.addAttribute("unassignedCount", employees.getUnassignedCount());
            model.addAttribute("resignedCount", employees.getResignedCount());
            model.addAttribute("terminatedCount", employees.getTerminatedCount());
        }

        if (projects != null) {
            model.addAttribute("onHoldCount", projects.getOnholdCount());
            model.addAttribute("notStartedCount", projects.getNotstartedCount());
            model.addAttribute("totalProjects", projects.getTotalProjects());
            model.addAttribute("cancelledCount", projects.getCancelledCount());
            model.addAttribute("completedCount", projects.getCompletedCount());
            model.addAttribute("inProgressCount", projects.getInprogressCount());
        }
        return "index";
    }

}

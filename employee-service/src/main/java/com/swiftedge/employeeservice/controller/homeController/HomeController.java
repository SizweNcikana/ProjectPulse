package com.swiftedge.employeeservice.controller.homeController;

import com.swiftedge.employeeservice.dto.employee.EmployeeResponseDTO;
import com.swiftedge.employeeservice.dto.project.ProjectDTO;
import com.swiftedge.employeeservice.dto.status.StatusDTO;
import com.swiftedge.employeeservice.service.employee.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequestMapping("/api/v2/employees")
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final EmployeeService employeeService;

    @GetMapping("/home")
    public ResponseEntity<Map<String, Object>> dashboardData() {
        Map<String, Object> response = new HashMap<>();

        // Fetch data from services
        List<ProjectDTO> projectList = employeeService.getAllProjectsFromProjectService();
        List<StatusDTO> projectStatus = employeeService.fetchProjectStatusCounts();
        List<StatusDTO> employeeStatus = employeeService.fetchEmployeeStatusCount();
        List<EmployeeResponseDTO> employeeList = employeeService.getAllEmployees();

        Map<String, Object> projectData = new HashMap<>();
        projectStatus.forEach(status ->
                projectData.put(
                        formatStatusKey(status.getStatusName()),
                        status.getCount()
                )
        );
        projectData.put("totalProjects", projectList.size());
        projectData.put("projectsList", projectList);

        Map<String, Object> employeeData = new HashMap<>();
        employeeStatus.forEach(status ->
                employeeData.put(
                        formatStatusKey(status.getStatusName()),
                        status.getCount()
                )
        );
        employeeData.put("totalEmployees", employeeList.size());
        employeeData.put("employeesList", employeeList);

        response.put("projects", projectData);
        response.put("employees", employeeData);

        return ResponseEntity.ok(response);
    }

    private String formatStatusKey(String statusName) {
        return statusName.replaceAll("\\s+", "").toLowerCase() + "Count";
    }

}

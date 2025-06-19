package com.swiftedge.employeeservice.controller.homeController;

import com.swiftedge.employeeservice.dto.employee.EmployeeResponseDTO;
import com.swiftedge.employeeservice.dto.project.ProjectDTO;
import com.swiftedge.employeeservice.service.employee.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@RequestMapping("/api/v2/employees")
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final EmployeeService employeeService;
    List<ProjectDTO> projectList;
    List<EmployeeResponseDTO> employeeList;

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("activePage", "index");
        projectList = employeeService.getAllProjectsFromProjectService();
        List<EmployeeResponseDTO> employeeList = employeeService.getAllEmployees();

        int numberOfProjects = projectList.size();
        int numberOfEmployees = employeeList.size();

        System.out.println("Number of Employees: " + numberOfEmployees);
        System.out.println("Number of Project: " + numberOfProjects);

        model.addAttribute("totalProjects", numberOfProjects);
        model.addAttribute("allEmployees", numberOfEmployees);

        return "index";
    }
}

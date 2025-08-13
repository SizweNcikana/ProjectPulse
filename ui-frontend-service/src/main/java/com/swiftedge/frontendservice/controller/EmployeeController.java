package com.swiftedge.frontendservice.controller;

import com.swiftedge.frontendservice.dto.employee.EmployeeFormResponseDTO;
import com.swiftedge.frontendservice.service.EmployeeClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class EmployeeController {

    private final EmployeeClient employeeClient;

    @GetMapping("employees/add-employee")
    public String showAddEmployeeForm(Model model) {

        EmployeeFormResponseDTO formResponseDTO = employeeClient.employeeData("/add-employee");

        model.addAttribute("activeMenu", formResponseDTO.getActiveMenu());
        model.addAttribute("activePage", formResponseDTO.getActivePage());
        model.addAttribute("employee", formResponseDTO.getEmployee());
        model.addAttribute("projects", formResponseDTO.getProjects());

        return "add-employee";

    }
}

package com.swiftedge.frontendservice.controller;

import com.swiftedge.frontendservice.dto.employee.EmployeeFormDTO;
import com.swiftedge.frontendservice.dto.employee.EmployeeRequestDTO;
import com.swiftedge.frontendservice.dto.employee.EmployeeResponseDTO;
import com.swiftedge.frontendservice.service.EmployeeClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@AllArgsConstructor
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeClient employeeClient;

    @GetMapping("/add-employee")
    public String showAddEmployeeForm(Model model) {

        EmployeeFormDTO formResponseDTO = employeeClient.employeeData("/add-employee");

        model.addAttribute("activeMenu", formResponseDTO.getActiveMenu());
        model.addAttribute("activePage", formResponseDTO.getActivePage());
        model.addAttribute("employee", formResponseDTO.getEmployee());
        model.addAttribute("projects", formResponseDTO.getProjects());

        return "add-employee";

    }

    @PostMapping("/save-employee")
    public String saveEmployee(@ModelAttribute("employee") EmployeeRequestDTO employeeRequestDTO,
                               RedirectAttributes redirectAttributes) {
        EmployeeResponseDTO responseDTO = employeeClient.saveEmployee(employeeRequestDTO, "/save-employee");

        redirectAttributes.addFlashAttribute("successMessage", responseDTO.getSuccessMessage());
        redirectAttributes.addFlashAttribute("errorMessage", responseDTO.getErrorMessage());

        return "redirect:/employees/add-employee";


    }
}

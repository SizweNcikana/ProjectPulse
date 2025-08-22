package com.swiftedge.frontendservice.controller;

import com.swiftedge.frontendservice.dto.employee.EmployeeFormDTO;
import com.swiftedge.frontendservice.dto.employee.EmployeeDTO;
import com.swiftedge.frontendservice.dto.employee.EmployeeResponseDTO;
import com.swiftedge.frontendservice.service.EmployeeClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeClient employeeClient;
    EmployeeFormDTO formResponseDTO;

    @GetMapping("/add-employee")
    public String showAddEmployeeForm(Model model) {

        formResponseDTO = employeeClient.employeeData("/add-employee");

        model.addAttribute("activeMenu", formResponseDTO.getActiveMenu());
        model.addAttribute("activePage", formResponseDTO.getActivePage());
        model.addAttribute("employee", formResponseDTO.getEmployee());
        model.addAttribute("projects", formResponseDTO.getProjects());

        return "add-employee";

    }

    @PostMapping("/save-employee")
    public String saveEmployee(@ModelAttribute("employee") EmployeeDTO employeeDTO,
                               RedirectAttributes redirectAttributes) {
        EmployeeResponseDTO responseDTO = employeeClient.saveEmployee(employeeDTO, "/save-employee");

        redirectAttributes.addFlashAttribute("successMessage", responseDTO.getSuccessMessage());
        redirectAttributes.addFlashAttribute("errorMessage", responseDTO.getErrorMessage());

        return "redirect:/employees/add-employee";

    }

    @GetMapping("/view-employees")
    public String viewEmployees(Model model) {
        List<EmployeeResponseDTO> employees = employeeClient.getEmployees("/view-employees");

        for (EmployeeResponseDTO employee : employees) {
            model.addAttribute("status", employee.getStatus());
            log.info("Status: {}", employee.getStatus());
        }

        model.addAttribute("activeMenu", "employees");
        model.addAttribute("activePage", "all-employees");
        model.addAttribute("employees", employees);

        return "employees-view-all";
    }

}

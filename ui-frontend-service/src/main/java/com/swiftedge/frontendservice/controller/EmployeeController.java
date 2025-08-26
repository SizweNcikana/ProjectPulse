package com.swiftedge.frontendservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swiftedge.dtolibrary.dto.*;
import com.swiftedge.frontendservice.dto.employee.EmployeeFormDTO;
import com.swiftedge.frontendservice.service.EmployeeClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/employees")
public class EmployeeController {

    private final ObjectMapper objectMapper;
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

        model.addAttribute("activeMenu", "employees");
        model.addAttribute("activePage", "all-employees");
        model.addAttribute("employees", employees);

        return "employees-view-all";
    }

    @GetMapping("/view-employee")
    public String viewEmployee(Model model) {

        formResponseDTO = employeeClient.employeeData("/view-employee");

        model.addAttribute("activeMenu", formResponseDTO.getActiveMenu());
        model.addAttribute("activePage", formResponseDTO.getActivePage());

        return "edit-employee";
    }

    @GetMapping("/search-employee")
    public String searchEmployee(@RequestParam("name") String name,
                                 @RequestParam("surname") String surname,
                                 Model model) {

        // Implementing 'UriComponentsBuilder' as this is a safer way of searching
        // because names can have spacings and special characters
        String path = UriComponentsBuilder.fromPath("/search-employee")
                .queryParam("name", name)
                .queryParam("surname", surname)
                .toUriString();

        EmployeeSearchResponseDTO responseDTO = employeeClient.employeeResponseData(path);

        model.addAttribute("activeMenu", "employees");
        model.addAttribute("activePage", "view-employee");

        if (responseDTO == null || responseDTO.getEmployee() == null) {
            model.addAttribute("searchErrorMessage",
                    String.format("Employee with name %s %s does not exist.", name, surname));
            return "edit-employee"; // stay on same page with error
        }

        try {
            String prettyResponse = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(responseDTO);

            System.out.println("Response from search: \n" + prettyResponse);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        String statusName = responseDTO.getEmployee().getEmployee().getStatus().getStatusName();
        Long selectedStatusId = responseDTO.getEmployee().getEmployee().getStatus().getStatusId();

        List<StatusDTO> statusList = responseDTO.getStatuses();

        model.addAttribute("statusList", statusList);
        model.addAttribute("selectedStatusId", selectedStatusId);

        System.out.println("Employee current status: " + statusName);
        statusList.forEach(s -> System.out.println("\nAvailable status: \n" + s.getStatusName()));

        assert responseDTO != null;

        model.addAttribute("employee", responseDTO.getEmployee());
        model.addAttribute("address", responseDTO.getAddress());
        model.addAttribute("statuses", responseDTO.getStatuses());
        model.addAttribute("currentStatus", statusName);
        model.addAttribute("availableProjects", responseDTO.getAvailableProjects());
        model.addAttribute("assignedProject", responseDTO.getAssignedProject());

        return "edit-employee";
    }

}

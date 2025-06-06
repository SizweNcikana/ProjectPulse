package com.swiftedge.employeeservice.controller;

import com.swiftedge.employeeservice.dto.address.AddressRequestDTO;
import com.swiftedge.employeeservice.dto.employee.EmployeeRequestDTO;
import com.swiftedge.employeeservice.dto.employee.EmployeeResponseDTO;
import com.swiftedge.employeeservice.dto.project.ProjectDTO;
import com.swiftedge.employeeservice.dto.status.StatusDTO;
import com.swiftedge.employeeservice.entity.address.EmployeeAddressEntity;
import com.swiftedge.employeeservice.entity.employee.EmployeeEntity;
import com.swiftedge.employeeservice.service.employee.EmployeeService;
import com.swiftedge.employeeservice.service.status.StatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.MessageFormat;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequestMapping("/api/v2/employees")
@Controller
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;
    private final StatusService statusService;
    List<ProjectDTO> projectList;
    List<StatusDTO> statuses;
    boolean isUpdated;
    Long projectId;
    Long selectedProjectId;


    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("activePage", "index");
        projectList = employeeService.getAllProjectsFromProjectService();
        List<EmployeeResponseDTO> employeeList = employeeService.getAllEmployees();

        int numberOfProjects = projectList.size();
        int numberOfEmployees = employeeList.size();
        System.out.println("Employee List: " + numberOfEmployees);
        model.addAttribute("totalProjects", numberOfProjects);
        model.addAttribute("allEmployees", numberOfEmployees);

        return "index";
    }

    @GetMapping("/add")
    public String getEmployeeForm(Model model) {
        model.addAttribute("activeMenu", "employees");
        model.addAttribute("activePage", "employees");

        projectList = employeeService.getAllProjectsFromProjectService();
        EmployeeRequestDTO employee = new EmployeeRequestDTO();
        AddressRequestDTO address = new AddressRequestDTO();

        employee.setAddress(address); // Link the address to the employee DTO
        log.info("Projects: {}", projectList);

        model.addAttribute("employee", employee);
        model.addAttribute("projects", projectList);

        return "add-employee";
    }

    @PostMapping("/save")
    public String saveEmployee(@Valid
                               @ModelAttribute("employee")
                               EmployeeRequestDTO employeeRequestDTO,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Validation errors occurred. Please correct them and try again.");
            return "redirect:/api/v2/employees/add";
        }

        try {
            // Fetch the project ID from the dropdown (selected project ID)
            selectedProjectId = employeeRequestDTO.getProject();

            statuses = statusService.getAllStatuses();

            String statusName = "NEW";

            /*
            Search for the ID for of status 'NEW', if found, save it as the default for a new employee
            being added to the System.
             */
            statuses.stream()
                    .filter(s -> s.getStatusName().equalsIgnoreCase(statusName))
                    .findFirst()
                    .flatMap(status -> {
                        Long statusId = status.getStatusId();
                        return statusService.getStatusById(statusId);
                    })
                    .ifPresentOrElse(employeeStatus -> {
                        log.info("Employee Status: {}", employeeStatus.getId());
                        employeeService.saveEmployee(employeeRequestDTO, selectedProjectId, employeeStatus.getId());
                    }, () -> {
                        log.error("No matching status found or status ID is invalid.");
                    });

            redirectAttributes.addFlashAttribute("successMessage", "Employee data saved successfully.");

        } catch (IllegalArgumentException iex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error occurred. " + iex.getMessage());
            return "redirect:/api/v2/employees/add";
        }
        return "redirect:/api/v2/employees/add";

    }

    @GetMapping("/success")
    public String showSuccessPage() {
        return "employee-success";
    }

    @GetMapping("/view-all")
    public String viewAllEmployees(Model model) {
        model.addAttribute("activeMenu", "employees");
        model.addAttribute("activePage", "all-employees");

        List<EmployeeResponseDTO> employees = employeeService.getAllEmployees();
        model.addAttribute("employeeEntityList", employees);

        return "employees-view-all";
    }

    @GetMapping("/edit")
    public String editEmployee(Model model) {
        model.addAttribute("activeMenu", "employees");
        model.addAttribute("activePage", "edit-employee");

        projectList = employeeService.getAllProjectsFromProjectService();
        statuses = statusService.getAllStatuses();

        model.addAttribute("projects", projectList);
        model.addAttribute("statuses", statuses);

        return "edit-employee";
    }

    @GetMapping("/search")
    public String findEmployee(@RequestParam("name") String name,
                               @RequestParam("surname") String surname,
                               Model model)
    {
        model.addAttribute("activeMenu", "employees");
        model.addAttribute("activePage", "edit-employee");

        List<EmployeeEntity> employees = employeeService.searchEmployee(name, surname);
        List<EmployeeAddressEntity> addresses = employeeService.getEmployeeAddress(name, surname);
        statuses = statusService.getAllStatuses();
        projectList = employeeService.getAllProjectsFromProjectService();


        if (addresses.isEmpty() && employees.isEmpty()) {
            model.addAttribute("searchErrorMessage",
                    MessageFormat.format("Employee with name {0} {1} does not exist.", name, surname));
        } else {
            String streetAddress = addresses.get(0).getStreetAddress();
            String suburb = addresses.get(0).getSuburb();
            String city = addresses.get(0).getCity();
            String zipCode = addresses.get(0).getZipCode();

            EmployeeAddressEntity employeeAddressEntity = new EmployeeAddressEntity();

            employeeAddressEntity.setStreetAddress(streetAddress);
            employeeAddressEntity.setSuburb(suburb);
            employeeAddressEntity.setCity(city);
            employeeAddressEntity.setZipCode(zipCode);

            for (EmployeeEntity employee : employees) {
                model.addAttribute("Id", employee.getEmployeeId());
                model.addAttribute("name", employee.getName());
                model.addAttribute("surname", employee.getSurname());
                model.addAttribute("email", employee.getEmail());
                model.addAttribute("number", employee.getNumber());
                model.addAttribute("idNumber", employee.getIdNumber());
                model.addAttribute("dob", employee.getDob());
                model.addAttribute("occupation", employee.getOccupation());
                model.addAttribute("statuses", statuses);
                model.addAttribute("ethnicity", employee.getEthnicity());
                model.addAttribute("years_experience", employee.getExperience());
                model.addAttribute("status", employee.getStatus().getStatus());
                model.addAttribute("summary", employee.getSummary());

                model.addAttribute("city", city);
                model.addAttribute("suburb", suburb);
                model.addAttribute("address", streetAddress);
                model.addAttribute("zipCode", zipCode);

                //Get the list of Projects and sorts it in Alphabetical order
                model.addAttribute("projects", projectList.stream()
                        .sorted(Comparator.comparing(ProjectDTO::getProjectName))
                        .collect(Collectors.toList()));

                projectId = employee.getProjectId();
                if (projectId != null) {
                    ProjectDTO projectDTO = employeeService.getProjectById(projectId);

                    if (projectDTO != null) {
                        log.info("Project id: {} project name: {}", projectId, projectDTO.getProjectName());
                        model.addAttribute("assignedProject", projectDTO);
                    } else {
                        log.warn("Project id: {} not found", projectId);
                    }
                }
            }
        }

        return "edit-employee";
    }

    @PostMapping("/{id}/update")
    public String updateEmployee(
            @PathVariable("id") Long id,
            @RequestParam("status") Long selectedStatus,
            @ModelAttribute("employee") EmployeeResponseDTO employeeResponseDTO,
            @ModelAttribute("address") AddressRequestDTO addressRequestDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        // Validation errors
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Validation errors occurred. Please correct them and try again.");
            return "redirect:/api/v2/employees/search?name="+employeeResponseDTO.getName()+"&surname="+employeeResponseDTO.getSurname();
        }

        try {
            Long statValue = employeeResponseDTO.getStatusId();
            if (selectedStatus == null) {
                log.info("Selected status is null");
            }

            isUpdated = employeeService.updateEmployee(id, employeeResponseDTO, addressRequestDTO, selectedStatus);

            if (isUpdated) {

                redirectAttributes.addFlashAttribute("successMessage",
                        "Employee updated successfully.");
            } else {
                redirectAttributes.addFlashAttribute("infoMessage",
                        "No changes were made as the provided data matches the current data.");
            }

        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error: " + ex.getMessage());
        }
        return "redirect:/api/v2/employees/edit";
    }

    @PostMapping("/{id}/assign-project")
    public String assignEmployeeToNewProject(
            @PathVariable("id") Long id,
            @ModelAttribute ProjectDTO projectDTO,
            EmployeeRequestDTO employeeRequestDTO,
            RedirectAttributes redirectAttributes
    )
    {
         try {
             // Fetch the project ID from the dropdown (selected project ID)
             selectedProjectId = employeeRequestDTO.getProject();
             log.info("Selected project id: {}", selectedProjectId);

             if (selectedProjectId != null && !selectedProjectId.equals(projectId)) {
                 employeeService.updateAssignedEmployeeProject(id, selectedProjectId);

                 redirectAttributes.addFlashAttribute("successMessage",
                         "Project updated successfully for the employee.");

             } else {
                 redirectAttributes.addFlashAttribute("infoMessage",
                         "No changes were made as the provided data matches the current data.");
             }
         } catch (IllegalArgumentException ex) {
             log.error("Error: {}", ex.getMessage());
         }

        return "redirect:/api/v2/employees/edit";

    }

    @PostMapping("/{id}/delete")
    public String deleteEmployee(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            employeeService.deleteEmployee(id);
            redirectAttributes.addFlashAttribute("successMessage", "Employee was deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete the employee. \nError: " + e.getMessage());
        }
        return "redirect:/api/v2/employees/edit";
    }

}

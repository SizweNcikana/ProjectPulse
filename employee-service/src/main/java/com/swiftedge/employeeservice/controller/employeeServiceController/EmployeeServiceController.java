package com.swiftedge.employeeservice.controller.employeeServiceController;

import com.swiftedge.employeeservice.dto.address.AddressDTO;
import com.swiftedge.employeeservice.dto.employee.EmployeeDTO;
import com.swiftedge.employeeservice.dto.employee.EmployeeResponseDTO;
import com.swiftedge.employeeservice.dto.project.ProjectDTO;
import com.swiftedge.employeeservice.dto.status.StatusDTO;
import com.swiftedge.employeeservice.entity.address.EmployeeAddressEntity;
import com.swiftedge.employeeservice.entity.employee.EmployeeEntity;
import com.swiftedge.employeeservice.service.employee.EmployeeService;
import com.swiftedge.employeeservice.service.status.StatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequestMapping("/api/v2/employees")
@Controller
@RequiredArgsConstructor
public class EmployeeServiceController {
    private final EmployeeService employeeService;
    private final StatusService statusService;
    List<ProjectDTO> projectList;
    List<StatusDTO> statuses;
    boolean isUpdated;
    Long projectId;
    Long selectedProjectId;

    @GetMapping("/add-employee")
    public ResponseEntity<Map<String, Object>> addEmployeeForm() {
        Map<String, Object> response = new HashMap<>();

        response.put("activeMenu", "employees");
        response.put("activePage", "employees");

        projectList = employeeService.getAllProjectsFromProjectService();
        response.put("projects", projectList);

        EmployeeDTO employee = new EmployeeDTO();
        employee.setAddress(new AddressDTO());
        response.put("employee", employee);

        log.info("Projects: {}", projectList);

        return ResponseEntity.ok(response);

    }

    @PostMapping("/save-employee")
    public ResponseEntity<EmployeeResponseDTO> saveEmployee(@RequestBody EmployeeDTO employeeDTO, ProjectDTO projectDTO) {
        try {
            selectedProjectId = projectDTO.getProjectId();
            String defaultStatus = "NEW";

            StatusDTO employeeStatus = statusService.getAllStatuses().stream()
                    .filter(s -> s.getStatusName().equalsIgnoreCase(defaultStatus))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("No matching status found for: " + defaultStatus));

            log.info("Assigning status '{}' (id={}) to employee", employeeStatus.getStatusName(), employeeStatus.getStatusId());

            EmployeeResponseDTO savedEmployee = employeeService.saveEmployee(
                    employeeDTO,
                    employeeDTO.getProjectId(),
                    employeeStatus.getStatusId()
            );

            return ResponseEntity.ok(savedEmployee);

        } catch (IllegalArgumentException ex) {
            log.error("Failed to save employee: {}", ex.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception ex) {
            log.error("Unexpected error saving employee", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/view-employees")
    public ResponseEntity<List<EmployeeResponseDTO>> viewEmployees() {
        List<EmployeeResponseDTO> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/success")
    public String showSuccessPage() {
        return "employee-success";
    }

    @GetMapping("/view-employee")
    public ResponseEntity<Map<String, Object>> viewEmployee() {
        Map<String, Object> response = new HashMap<>();

        response.put("activeMenu", "employees");
        response.put("activePage", "view-employee");

        return ResponseEntity.ok(response);

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
        model.addAttribute("statusList", statuses);
        log.info("Status: {}", statuses.listIterator().next().getStatusName());


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


                Long currentStatus = employee.getStatus().getId();
                model.addAttribute("selectedStatus", currentStatus);

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
                    String projectName = projectDTO.getProjectName();
                    log.info("Project Name: {}", projectName);

                    if (projectDTO != null) {
                        log.info("Project id: {} project name: {}", projectId, projectDTO.getProjectName());
                        model.addAttribute("assignedProject", projectDTO);
                        model.addAttribute("projectName", projectDTO.getProjectName());
                    } else {
                        log.warn("Project id: not found");
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
            @ModelAttribute("employee") EmployeeDTO employeeDTO,
            @ModelAttribute("address") AddressDTO addressDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        // Validation errors
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Validation errors occurred. Please correct them and try again.");
            return "redirect:/api/v2/employees/search";
        }
        StatusDTO statusDTO = new StatusDTO();

        try {
            Long statValue = statusDTO.getStatusId();
            if (selectedStatus == null) {
                log.info("Selected status is null");
            } else {
                System.out.println("Selected status is " + statValue);
            }

            isUpdated = employeeService.updateEmployee(id, employeeDTO, addressDTO, selectedStatus);

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
            EmployeeDTO employeeDTO,
            RedirectAttributes redirectAttributes
    )
    {
         try {
             // Fetch the project ID from the dropdown (selected project ID)
             selectedProjectId = projectDTO.getProjectId();
             log.info("Selected project id: {}", selectedProjectId);

             if (selectedProjectId != null && !selectedProjectId.equals(projectId)) {
                 employeeService.updateAssignedEmployeeProject(id);

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

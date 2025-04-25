package com.reliaquest.api.controller;

import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/employees")
public class EmployeeController implements IEmployeeController<Employee, CreateEmployeeInput> {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    public ResponseEntity<List<Employee>> getAllEmployees() {
        logger.info("Fetching all employees");
        try{
            List<Employee> employees = employeeService.fetchAllEmployees();
            logger.info("Successfully fetched {} employees", employees.size());
            return ResponseEntity.ok(employees);
        } catch (Exception ex){
            logger.error("Failed to fetch employees: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable("searchString") String searchString){
        logger.info("Searching employees with name containing: {}", searchString);
        List<Employee> filteredEmployees = employeeService.searchEmployeesByNameSearch(searchString);
        logger.info("Found {} employees matching search string: {}", filteredEmployees.size(), searchString);
        return ResponseEntity.ok(filteredEmployees);
    }

    public ResponseEntity getEmployeeById(@PathVariable("id") String id) {
        logger.info("Fetching employee with id: {}", id);
        try {
            validateId(id);
            Employee employee = employeeService.fetchEmployeeById(id);
            logger.info("Successfully fetched employee with id: {}", id);
            return ResponseEntity.ok(employee);
        } catch (ResponseStatusException responseStatusException){
            logger.warn("Invalid request for employee id: {}", id);
            throw responseStatusException;
        } catch (Exception exception) {
            logger.error("Failed to fetch employee with id: {}", id, exception);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        logger.info("Fetching highest salary among employees");
        int highestSalary = employeeService.getHighestSalaryOfEmployees();
        logger.info("Highest salary found: {}", highestSalary);
        return ResponseEntity.ok(highestSalary);
    }

    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        logger.info("Fetching top 10 highest earning employee names");
        List<String> topEarners = employeeService.getTop10HighestEarningEmployeeNames();
        logger.info("Found {} top earning employees", topEarners.size());
        return ResponseEntity.ok(topEarners);
    }

    public ResponseEntity<Employee> createEmployee(@RequestBody CreateEmployeeInput employeeInput) {
        logger.info("Creating new employee with name: {}", employeeInput.name());
        try {
            Employee createdEmployee = employeeService.createEmployee(employeeInput);
            logger.info("Successfully created employee with id: {}", createdEmployee.id());
            return ResponseEntity.ok(createdEmployee);
        } catch (Exception ex) {
            logger.error("Failed to create employee with name: {}", employeeInput.name(), ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create employee");
        }
    }

    public ResponseEntity<String> deleteEmployeeById(@PathVariable("id") String id) {
        logger.info("Attempting to delete employee with id: {}", id);
        validateId(id);

        try {
            String deletedEmployeeName = employeeService.deleteEmployeeById(id);
            logger.info("Successfully deleted employee: {}", deletedEmployeeName);
            return ResponseEntity.ok(deletedEmployeeName);
        } catch (Exception ex) {
            logger.error("Failed to delete employee with id: {}", id, ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete employee with id: " + id);
        }
    }

    private void validateId(String uuid) {
        try {
            UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid UUID provided: {}", uuid);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input provided");
        }
    }

}

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
            return ResponseEntity.ok(employees);
        } catch (Exception ex){
            logger.error("Failed to fetch employees: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }

    }

    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable("searchString") String searchString){
    List<Employee> filteredEmployees = employeeService.searchEmployeesByNameSearch(searchString);
        return ResponseEntity.ok(filteredEmployees);
    }

    public ResponseEntity getEmployeeById(@PathVariable("id") String id) {
        try {
            //log
            validateId(id);
            return ResponseEntity.ok(employeeService.fetchEmployeeById(id));
        } catch (ResponseStatusException responseStatusException){
            throw responseStatusException;
        } catch (Exception exception) {
            //log
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        //log
        int highestSalary = employeeService.getHighestSalaryOfEmployees();
        return ResponseEntity.ok(highestSalary);
    }

    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        //log
        List<String> topEarners = employeeService.getTop10HighestEarningEmployeeNames();
        return ResponseEntity.ok(topEarners);
    }

    public ResponseEntity<Employee> createEmployee(@RequestBody CreateEmployeeInput employeeInput) {
        try {
            Employee createdEmployee = employeeService.createEmployee(employeeInput);
            return ResponseEntity.ok(createdEmployee);
        } catch (Exception ex) {
            logger.error("Failed to create employee", ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create employee");
        }
    }

    public ResponseEntity<String> deleteEmployeeById(@PathVariable("id") String id) {
        validateId(id);

        try {
            String deletedEmployeeName = employeeService.deleteEmployeeById(id);
            return ResponseEntity.ok(deletedEmployeeName);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete employee with id: " + id);
        }
    }

    private void validateId(String uuid) {
        try {
            UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input provided");
        }
    }

}

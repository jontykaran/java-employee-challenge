package com.reliaquest.api.service;

import com.reliaquest.api.client.EmployeeClient;
import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.DeleteEmployeeInput;
import com.reliaquest.api.model.Employee;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

@Service
public class EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);
    private final EmployeeClient employeeClient;

    //SUGGESTION:
    /*
    1. I was thinking to implement a map/list to store Employees in memory (sort of cache).
    But then realised the mock API server is the source of truth, and we should depend on it for response.
    2. Cache here would have helped to improve data access and manipulation without fetching all employees again and again.
     */

    public EmployeeService(EmployeeClient employeeClient) {
        this.employeeClient = employeeClient;
    }

    public List<Employee> fetchAllEmployees() {
        try {
            List<Employee> employees = employeeClient.getEmployees();
            logger.info("Fetched {} employees", employees != null ? employees.size() : 0);
            return employees != null ? employees : Collections.emptyList();
        } catch (Exception ex) {
            logger.error("Failed to fetch employees from mock client", ex);
            throw ex;
        }
    }

    public Employee fetchEmployeeById(String id) {
        try {
            Employee employee = employeeClient.getEmployeeById(id);
            logger.info("Fetched employee with id: {}", id);
            if (employee == null) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, String.format("Failed to fetch employee with id %s from mock client", id));
            }
            return employee;
        } catch (Exception ex) {
            logger.error("Failed to fetch employee with id {} from mock client", id, ex);
            throw ex;
        }
    }

    public List<Employee> searchEmployeesByNameSearch(String name) {
        try {
            List<Employee> allEmployees = fetchAllEmployees();
            List<Employee> filteredEmployees = allEmployees.stream()
                    .filter(e -> e.employee_name().toLowerCase().contains(name.toLowerCase()))
                    .toList();
            logger.info("Found {} employees matching name search: {}", filteredEmployees.size(), name);
            return filteredEmployees;
        } catch (Exception ex) {
            logger.error("Failed to search employees with name string '{}'", name, ex);
            throw ex;
        }
    }

    public Integer getHighestSalaryOfEmployees() {
        try {
            Integer highestSalary = fetchAllEmployees().stream()
                    .map(Employee::employee_salary)
                    .max(Integer::compareTo)
                    .orElse(0);
            logger.info("Highest salary found: {}", highestSalary);
            return highestSalary;
        } catch (Exception ex) {
            logger.error("Failed to fetch highest salary from employees", ex);
            throw ex;
        }
    }

    public List<String> getTop10HighestEarningEmployeeNames() {
        try {
            List<String> topEarners = fetchAllEmployees().stream()
                    .sorted((e1, e2) -> Integer.compare(e2.employee_salary(), e1.employee_salary()))
                    .limit(10)
                    .map(Employee::employee_name)
                    .toList();
            logger.info("Found {} top earning employees", topEarners.size());
            return topEarners;
        } catch (Exception ex) {
            logger.error("Failed to get top 10 highest earning employees", ex);
            throw ex;
        }
    }

    public Employee createEmployee(@Valid CreateEmployeeInput input) {
        try {
            Employee createdEmployee = employeeClient.createEmployee(input);
            logger.info("Successfully created employee with id: {}", createdEmployee.id());
            return createdEmployee;
        } catch (Exception ex) {
            logger.error("Failed to create employee with name: {}", input.name(), ex);
            throw ex;
        }
    }

    public String deleteEmployeeById(String id) {
        try {
            Employee employee = fetchEmployeeById(id);
            Boolean response = employeeClient.deleteEmployeeByName(new DeleteEmployeeInput(employee.employee_name()));

            if (response) {
                logger.info("Successfully deleted employee: {}", employee.employee_name());
                return employee.employee_name();
            } else {
                logger.warn("Failed to delete employee: {}", employee.employee_name());
                throw new RuntimeException("Failed to delete employee");
            }
        } catch (Exception ex) {
            logger.error("Failed to delete employee by ID {}", id, ex);
            throw ex;
        }
    }
}
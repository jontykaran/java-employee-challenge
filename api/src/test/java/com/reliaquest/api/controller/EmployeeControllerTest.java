package com.reliaquest.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService employeeService;

    @Test
    void getAllEmployees_returnsListOfEmployees() throws Exception {
        // Arrange
        List<Employee> mockEmployees = List.of(
                new Employee("10000000-0000-0000-0000-000000000000", "Employee_A", 50000, 25, "Developer", "a@example.com"),
                new Employee("20000000-0000-0000-0000-000000000000", "Employee_B", 60000, 30, "Manager", "b@example.com")
        );
        when(employeeService.fetchAllEmployees()).thenReturn(mockEmployees);

        // Act & Assert
        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)))
                .andExpect(jsonPath("$[0].employee_name", is("Employee_A")))
                .andExpect(jsonPath("$[0].employee_salary", is(50000)))
                .andExpect(jsonPath("$[1].employee_name", is("Employee_B")))
                .andExpect(jsonPath("$[1].employee_salary", is(60000)));
    }

    @Test
    void getAllEmployees_whenServiceThrowsException_returns500() throws Exception {
        when(employeeService.fetchAllEmployees()).thenThrow(new RuntimeException("Service failure"));

        mockMvc.perform(get("/employees"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getEmployeeById_returnsEmployee() throws Exception {
        // Arrange
        Employee mockEmployee = new Employee("10000000-0000-0000-0000-000000000000", "Employee_A", 50000, 25, "Developer", "a@example.com");
        when(employeeService.fetchEmployeeById("10000000-0000-0000-0000-000000000000")).thenReturn(mockEmployee);

        // Act & Assert
        mockMvc.perform(get("/employees/10000000-0000-0000-0000-000000000000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employee_name", is("Employee_A")))
                .andExpect(jsonPath("$.employee_salary", is(50000)))
                .andExpect(jsonPath("$.employee_age", is(25)))
                .andExpect(jsonPath("$.employee_title", is("Developer")));
    }

    @Test
    void getEmployeeById_withInvalidUUID_returns400() throws Exception {
        mockMvc.perform(get("/employees/invalid-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchEmployeesByName_returnsMatchingEmployees() throws Exception {
        // Arrange
        List<Employee> mockEmployees = List.of(
                new Employee("10000000-0000-0000-0000-000000000000", "Employee_A", 50000, 25, "Developer", "a@example.com"),
                new Employee("20000000-0000-0000-0000-000000000000", "Employee_B", 60000, 30, "Manager", "b@example.com")
        );
        when(employeeService.searchEmployeesByNameSearch("Employee")).thenReturn(mockEmployees);

        // Act & Assert
        mockMvc.perform(get("/employees/search/Employee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)))
                .andExpect(jsonPath("$[0].employee_name", is("Employee_A")))
                .andExpect(jsonPath("$[1].employee_name", is("Employee_B")));
    }

    @Test
    void searchEmployeesByName_whenNoMatches_returnsEmptyList() throws Exception {
        when(employeeService.searchEmployeesByNameSearch("xyz")).thenReturn(List.of());

        mockMvc.perform(get("/employees/search/xyz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(0)));
    }

    @Test
    void getHighestSalary_returnsHighestSalary() throws Exception {
        when(employeeService.getHighestSalaryOfEmployees()).thenReturn(60000);

        mockMvc.perform(get("/employees/highestSalary"))
                .andExpect(status().isOk())
                .andExpect(content().string("60000"));
    }

    @Test
    void getTop10HighestEarningNames_returnsNames() throws Exception {
        List<String> mockNames = List.of("Employee_B", "Employee_C", "Employee_A");
        when(employeeService.getTop10HighestEarningEmployeeNames()).thenReturn(mockNames);

        mockMvc.perform(get("/employees/topTenHighestEarningEmployeeNames"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(3)))
                .andExpect(jsonPath("$[0]", is("Employee_B")))
                .andExpect(jsonPath("$[1]", is("Employee_C")))
                .andExpect(jsonPath("$[2]", is("Employee_A")));
    }

    @Test
    void createEmployee_returnsCreatedEmployee() throws Exception {
        // Arrange
        CreateEmployeeInput input = new CreateEmployeeInput("Employee_A", 50000, 25, "Developer");
        Employee mockEmployee = new Employee("10000000-0000-0000-0000-000000000000", "Employee_A", 50000, 25, "Developer", "a@example.com");
        when(employeeService.createEmployee(any(CreateEmployeeInput.class))).thenReturn(mockEmployee);

        // Act & Assert
        mockMvc.perform(post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employee_name", is("Employee_A")))
                .andExpect(jsonPath("$.employee_salary", is(50000)))
                .andExpect(jsonPath("$.employee_age", is(25)))
                .andExpect(jsonPath("$.employee_title", is("Developer")));
    }

    @Test
    void deleteEmployee_returnsEmployeeName() throws Exception {
        when(employeeService.deleteEmployeeById("10000000-0000-0000-0000-000000000000")).thenReturn("Employee_A");

        mockMvc.perform(delete("/employees/10000000-0000-0000-0000-000000000000"))
                .andExpect(status().isOk())
                .andExpect(content().string("Employee_A"));
    }

    @Test
    void deleteEmployee_withInvalidUUID_returns400() throws Exception {
        mockMvc.perform(delete("/employees/invalid-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteEmployee_whenServiceThrowsException_returns500() throws Exception {
        when(employeeService.deleteEmployeeById("10000000-0000-0000-0000-000000000000"))
                .thenThrow(new RuntimeException("Service failure"));

        mockMvc.perform(delete("/employees/10000000-0000-0000-0000-000000000000"))
                .andExpect(status().isInternalServerError());
    }
}

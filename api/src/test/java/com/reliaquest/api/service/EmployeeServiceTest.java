package com.reliaquest.api.service;

import com.reliaquest.api.client.EmployeeClient;
import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.DeleteEmployeeInput;
import com.reliaquest.api.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeClient employeeClient;

    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeService(employeeClient);
    }

    @Test
    void fetchAllEmployees_returnsListFromClient() {
        // When
        List<Employee> mockEmployees = List.of(
                new Employee("10000000-0000-0000-0000-000000000000", "Employee_A", 100, 25, "Developer", "a@example.com"),
                new Employee("20000000-0000-0000-0000-000000000000", "Employee_B", 200, 30, "Manager", "b@example.com")
        );
        when(employeeClient.getEmployees()).thenReturn(mockEmployees);

        // Act
        List<Employee> result = employeeService.fetchAllEmployees();

        // Assert
        assertEquals(2, result.size());
        verify(employeeClient, times(1)).getEmployees();
    }

    @Test
    void fetchAllEmployees_whenClientReturnsNull_returnsEmptyList() {
        when(employeeClient.getEmployees()).thenReturn(null);

        // Act
        List<Employee> result = employeeService.fetchAllEmployees();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void fetchAllEmployees_whenClientThrowsException_throwsException() {
        // Act
        when(employeeClient.getEmployees()).thenThrow(new RuntimeException("Internal Server Error"));

        // Assert
        assertThrows(RuntimeException.class, () -> employeeService.fetchAllEmployees());
        verify(employeeClient, times(1)).getEmployees();
    }


    @Test
    void fetchEmployeeById_returnsEmployee() {
        // When
        Employee mockEmployee = new Employee("10000000-0000-0000-0000-000000000000", "Employee_A", 100, 25, "Developer", "a@example.com");
        when(employeeClient.getEmployeeById("10000000-0000-0000-0000-000000000000")).thenReturn(mockEmployee);

        // Act
        Employee result = employeeService.fetchEmployeeById("10000000-0000-0000-0000-000000000000");

        // Assert
        assertEquals(mockEmployee, result);
        verify(employeeClient, times(1)).getEmployeeById("10000000-0000-0000-0000-000000000000");
    }

    @Test
    void fetchEmployeeById_WhenNotPresent_returnsNull() {
        // When
        when(employeeClient.getEmployeeById("10000000-0000-0000-0000-000000000000")).thenReturn(null);

        // Act
        // Assert
        assertThrows(ResponseStatusException.class, () -> employeeService.fetchEmployeeById("10000000-0000-0000-0000-000000000000"));
        verify(employeeClient, times(1)).getEmployeeById("10000000-0000-0000-0000-000000000000");
    }

    @Test
    void fetchEmployeeById_whenClientThrowsException_throwsException() {
        // Act
        when(employeeClient.getEmployeeById("10000000-0000-0000-0000-000000000000")).thenThrow(new RuntimeException("Internal Server Error"));

        // Assert
        assertThrows(RuntimeException.class, () -> employeeService.fetchEmployeeById("10000000-0000-0000-0000-000000000000"));
        verify(employeeClient, times(1)).getEmployeeById("10000000-0000-0000-0000-000000000000");
    }

    @Test
    void searchEmployeesByNameSearch_returnsMatchingEmployees() {
        // When
        List<Employee> mockEmployees = List.of(
                new Employee("10000000-0000-0000-0000-000000000000", "Employee TestA", 100, 25, "Developer", "a@example.com"),
                new Employee("20000000-0000-0000-0000-000000000000", "Employee TestB", 200, 30, "Manager", "b@example.com"),
                new Employee("30000000-0000-0000-0000-000000000000", "Employee TestC", 300, 28, "Designer", "c@example.com")
        );
        when(employeeClient.getEmployees()).thenReturn(mockEmployees);

        // Act
        List<Employee> result = employeeService.searchEmployeesByNameSearch("TestA");

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.stream().anyMatch(e -> e.employee_name().equals("Employee TestA")));

        result = employeeService.searchEmployeesByNameSearch("Test");
        assertEquals( 3, result.size());
        assertTrue(result.stream().anyMatch(e -> e.employee_name().equals("Employee TestA")));
        assertTrue(result.stream().anyMatch(e -> e.employee_name().equals("Employee TestB")));
        assertTrue(result.stream().anyMatch(e -> e.employee_name().equals("Employee TestC")));
    }

    @Test
    void searchEmployeesByNameSearch_whenClientThrowsException_throwsException() {
        // When
        when(employeeClient.getEmployees()).thenThrow(new RuntimeException("Internal Server Error"));

        // Assert
        assertThrows(RuntimeException.class, () -> employeeService.searchEmployeesByNameSearch("test"));
        verify(employeeClient, times(1)).getEmployees();
    }

    @Test
    void searchEmployeesByNameSearch_whenNoEmployeeNameExists_returnEmptyList() {
        // When
        List<Employee> mockEmployees = List.of(
                new Employee("10000000-0000-0000-0000-000000000000", "Employee TestA", 100, 25, "Developer", "a@example.com"),
                new Employee("20000000-0000-0000-0000-000000000000", "Employee TestB", 200, 30, "Manager", "b@example.com"),
                new Employee("30000000-0000-0000-0000-000000000000", "Employee TestC", 300, 28, "Designer", "c@example.com")
        );
        when(employeeClient.getEmployees()).thenReturn(mockEmployees);

        // Act
        List<Employee> result = employeeService.searchEmployeesByNameSearch("Random Employee");

        // Assert
        assertEquals(0, result.size());
        assertTrue(result.isEmpty());
    }

    @Test
    void getHighestSalaryOfEmployees_returnsHighestSalary() {
        // When
        List<Employee> mockEmployees = List.of(
                new Employee("10000000-0000-0000-0000-000000000000", "Employee TestA", 100, 25, "Developer", "a@example.com"),
                new Employee("20000000-0000-0000-0000-000000000000", "Employee TestB", 400, 30, "Manager", "b@example.com"),
                new Employee("30000000-0000-0000-0000-000000000000", "Employee TestC", 200, 30, "Manager", "b@example.com")
        );
        when(employeeClient.getEmployees()).thenReturn(mockEmployees);

        // Act
        Integer result = employeeService.getHighestSalaryOfEmployees();

        //Assert
        assertEquals(400, result);
    }

    @Test
    void getHighestSalaryOfEmployees_whenClientThrowsException_throwsException() {
        // When
        when(employeeClient.getEmployees()).thenThrow(new RuntimeException("Internal Server Error"));

        // Assert
        assertThrows(RuntimeException.class, () -> employeeService.getHighestSalaryOfEmployees());
        verify(employeeClient, times(1)).getEmployees();
    }

    @Test
    void getTop10HighestEarningEmployeeNames_returnsCorrectNames() {
        // When
        List<Employee> mockEmployees = List.of(
                new Employee("10000000-0000-0000-0000-000000000000", "Employee_A", 50000, 25, "Developer", "a@example.com"),
                new Employee("20000000-0000-0000-0000-000000000000", "Employee_B", 60000, 30, "Manager", "b@example.com"),
                new Employee("30000000-0000-0000-0000-000000000000", "Employee_C", 55000, 28, "Designer", "c@example.com")
        );
        when(employeeClient.getEmployees()).thenReturn(mockEmployees);

        // Arrange
        List<String> result = employeeService.getTop10HighestEarningEmployeeNames();

        // Assert
        assertEquals(3, result.size());
        assertEquals("Employee_B", result.get(0));
        assertEquals("Employee_C", result.get(1));
        assertEquals("Employee_A", result.get(2));
    }

    @Test
    void getTop10HighestEarningEmployeeNames_whenClientThrowsException_throwsException() {
        // Act
        when(employeeClient.getEmployees()).thenThrow(new RuntimeException("Internal Server Error"));

        // Assert
        assertThrows(RuntimeException.class, () -> employeeService.getTop10HighestEarningEmployeeNames());
        verify(employeeClient, times(1)).getEmployees();
    }

    @Test
    void createEmployee_returnsCreatedEmployee() {
        // When
        CreateEmployeeInput input = new CreateEmployeeInput("New Employee", 200, 28, "Assistant");
        Employee mockEmployee = new Employee("10000000-0000-0000-0000-000000000000", "New Employee", 200, 28, "Assistant", "new@example.com");
        when(employeeClient.createEmployee(input)).thenReturn(mockEmployee);

        // Act
        Employee result = employeeService.createEmployee(input);

        //Assert
        assertEquals(mockEmployee, result);
        verify(employeeClient, times(1)).createEmployee(input);
    }

    @Test
    void createEmployee_whenClientThrowsException_throwsException() {
        // Act
        CreateEmployeeInput input = new CreateEmployeeInput("New Employee", 200, 28, "Assistant");
        when(employeeClient.createEmployee(input)).thenThrow(new RuntimeException("Internal Server Error"));

        //Arrange
        assertThrows(RuntimeException.class, () -> employeeService.createEmployee(input));
        verify(employeeClient, times(1)).createEmployee(input);
    }

    @Test
    void deleteEmployeeById_returnsEmployeeName() {
        // When
        String employeeId = "10000000-0000-0000-0000-000000000000";
        Employee mockEmployee = new Employee(employeeId, "Employee_name", 1000, 25, "Developer", "example@example.com");
        when(employeeClient.getEmployeeById(employeeId)).thenReturn(mockEmployee);
        when(employeeClient.deleteEmployeeByName(new DeleteEmployeeInput("Employee_name"))).thenReturn(true);

        // Act
        String result = employeeService.deleteEmployeeById(employeeId);

        // Assert
        assertEquals("Employee_name", result);
        verify(employeeClient, times(1)).getEmployeeById(employeeId);
        verify(employeeClient, times(1)).deleteEmployeeByName(new DeleteEmployeeInput("Employee_name"));
    }

    @Test
    void deleteEmployeeById_whenGetEmployeeThrowsException_throwsException() {
        // When
        String employeeId = "10000000-0000-0000-0000-000000000000";
        when(employeeClient.getEmployeeById(employeeId)).thenThrow(new RuntimeException("Internal Server Error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> employeeService.deleteEmployeeById(employeeId));
        verify(employeeClient, times(1)).getEmployeeById(employeeId);
        verify(employeeClient, never()).deleteEmployeeByName(any());
    }

    @Test
    void deleteEmployeeById_whenDeleteEmployeeThrowsException_throwsException() {
        // When
        String employeeId = "10000000-0000-0000-0000-000000000000";
        Employee mockEmployee = new Employee(employeeId, "Employee_name", 1000, 25, "Developer", "example@example.com");
        when(employeeClient.getEmployeeById(employeeId)).thenReturn(mockEmployee);
        when(employeeClient.deleteEmployeeByName(new DeleteEmployeeInput("Employee_name")))
            .thenThrow(new RuntimeException("Internal Server Error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> employeeService.deleteEmployeeById(employeeId));
        verify(employeeClient, times(1)).getEmployeeById(employeeId);
        verify(employeeClient, times(1)).deleteEmployeeByName(new DeleteEmployeeInput("Employee_name"));
    }
}

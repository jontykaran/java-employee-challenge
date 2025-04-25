package com.reliaquest.api.client;

import com.reliaquest.api.model.ApiResponse;
import com.reliaquest.api.model.DeleteEmployeeInput;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.CreateEmployeeInput;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Component
public class EmployeeClient {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeClient.class);
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String EMPLOYEE_API_URL = "http://localhost:8112/api/v1/employee";

    public List<Employee> getEmployees() {
        logger.info("Calling mock API to get employee list");
        ResponseEntity<ApiResponse<List<Employee>>> response = restTemplate.exchange(
                EMPLOYEE_API_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        if(response.getBody().error() != null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, response.getBody().error());
        }

        List<Employee> data = response.getBody() != null ? response.getBody().data() : List.of();
        logger.info("Received {} employees", data.size());

        return data;
    }

    public Employee getEmployeeById(String id) {
        try {
            ResponseEntity<ApiResponse<Employee>> response = restTemplate.exchange(
                    EMPLOYEE_API_URL + "/" + id,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );
            if(response.getBody().error() != null) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, response.getBody().error());
            }
            return response.getBody() != null ? response.getBody().data() : null;
        } catch (HttpClientErrorException.NotFound e) {
            logger.warn("Employee not found with ID: {}", id);
            throw e;
        }
    }

    public Employee createEmployee(@Valid CreateEmployeeInput input) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CreateEmployeeInput> entity = new HttpEntity<>(input, headers);

        ResponseEntity<ApiResponse<Employee>> response = restTemplate.exchange(
                EMPLOYEE_API_URL,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {}
        );
        if(response.getBody().error() != null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, response.getBody().error());
        }
        return response.getBody() != null ? response.getBody().data() : null;
    }

    public Boolean deleteEmployeeByName(DeleteEmployeeInput deleteEmployeeInput) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<DeleteEmployeeInput> entity = new HttpEntity<>(deleteEmployeeInput, headers);
        ResponseEntity<ApiResponse<Boolean>> response = restTemplate.exchange(
                EMPLOYEE_API_URL,
                HttpMethod.DELETE,
                entity,
                new ParameterizedTypeReference<>() {}
        );
        if(response.getBody().error() != null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, response.getBody().error());
        }
        return response.getBody() != null ? response.getBody().data() : null;
    }
}

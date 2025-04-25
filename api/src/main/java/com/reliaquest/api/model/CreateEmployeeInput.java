package com.reliaquest.api.model;

import jakarta.validation.constraints.*;

public record CreateEmployeeInput(
        @NotBlank(message = "Name cannot be empty")
        String name,
        
        @NotNull(message = "Salary cannot be null")
        @Positive(message = "Salary must be positive")
        int salary,
        
        @NotNull(message = "Age cannot be null")
        @Min(value = 16, message = "Age must be at least 16")
        @Max(value = 75, message = "Age must be at most 75")
        int age,
        
        @NotBlank(message = "Title cannot be empty")
        String title
) {}
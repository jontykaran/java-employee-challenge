package com.reliaquest.api.model;

public record Employee(
        String id,
        String employee_name,
        int employee_salary,
        int employee_age,
        String employee_title,
        String employee_email
) {
}

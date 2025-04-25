package com.reliaquest.api.model;

public record ApiResponse<T>(
        T data,
        String status,
        String error
) {
}

package com.reliaquest.api.model;

import jakarta.validation.constraints.NotNull;

public record DeleteEmployeeInput (

    @NotNull
    String name
) {}
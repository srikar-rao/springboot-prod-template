package com.dev.org.common.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Hello World response DTO.
 */
public record HelloResponse(@NotBlank String message, @NotBlank String timestamp, String name) {}

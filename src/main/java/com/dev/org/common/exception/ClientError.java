package com.dev.org.common.exception;

import java.util.Map;

/**
 * Client errors (4xx) - errors caused by client input.
 * These are expected errors that should be handled gracefully.
 */
public sealed interface ClientError extends ApplicationError {

    /**
     * Resource not found error (404).
     *
     * @param resourceType Type of resource that was not found (e.g., "User", "Product")
     * @param identifier The identifier used to search for the resource
     */
    record NotFound(String resourceType, String identifier) implements ClientError {}

    /**
     * Validation error (400).
     *
     * @param message Human-readable validation error message
     * @param fieldErrors Map of field names to error messages (optional)
     */
    record Validation(String message, Map<String, String> fieldErrors) implements ClientError {
        public Validation(String message) {
            this(message, Map.of());
        }
    }

    /**
     * Bad request error (400).
     *
     * @param message Description of why the request is invalid
     */
    record BadRequest(String message) implements ClientError {}

    /**
     * Conflict error (409) - resource already exists or state conflict.
     *
     * @param message Description of the conflict
     */
    record Conflict(String message) implements ClientError {}
}

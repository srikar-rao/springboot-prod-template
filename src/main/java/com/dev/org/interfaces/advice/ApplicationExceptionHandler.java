package com.dev.org.interfaces.advice;

import com.dev.org.common.exception.ApplicationException;
import com.dev.org.common.exception.ClientError;
import com.dev.org.common.exception.ServerError;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Global exception handler for the application. Handles all ApplicationException and common
 * Spring exceptions. Converts exceptions to RFC 7807 Problem Details with consistent structure.
 * Includes centralized logging and trace ID propagation.
 */
@RestControllerAdvice
public class ApplicationExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApplicationExceptionHandler.class);
    private static final String TRACE_ID_KEY = "traceId";

    /**
     * Handles custom ApplicationException and delegates to specific handlers based on error type.
     */
    @ExceptionHandler(ApplicationException.class)
    public ProblemDetail handleApplicationException(
            ApplicationException ex, HttpServletRequest request) {
        return switch (ex.getApplicationError()) {
            case ClientError.NotFound error -> handleNotFound(error, request);
            case ClientError.Validation error -> handleValidation(error, request);
            case ClientError.BadRequest error -> handleBadRequest(error, request);
            case ClientError.Conflict error -> handleConflict(error, request);
            case ServerError.RemoteServiceError error -> handleRemoteServiceError(error, request);
            case ServerError.InternalError error -> handleInternalError(error, request);
            case ServerError.DatabaseError error -> handleDatabaseError(error, request);
        };
    }

    /**
     * Handles Spring's @Valid validation failures (400 Bad Request).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult()
                .getAllErrors()
                .forEach(
                        error -> {
                            String fieldName = ((FieldError) error).getField();
                            String errorMessage = error.getDefaultMessage();
                            fieldErrors.put(fieldName, errorMessage);
                        });

        var problemDetail =
                buildProblemDetail(HttpStatus.BAD_REQUEST, "Validation failed", request);
        problemDetail.setProperty("fieldErrors", fieldErrors);

        log.error("Validation failed");

        return problemDetail;
    }

    /**
     * Handles type mismatch in request parameters (400 Bad Request).
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        Class<?> requiredType = ex.getRequiredType();
        String expectedType = requiredType != null ? requiredType.getSimpleName() : "unknown";
        String message =
                String.format(
                        "Invalid value '%s' for parameter '%s'. Expected type: %s",
                        ex.getValue(), ex.getName(), expectedType);

        var problemDetail = buildProblemDetail(HttpStatus.BAD_REQUEST, message, request);

        log.error("Type mismatch");

        return problemDetail;
    }

    /**
     * Handles all other uncaught exceptions (500 Internal Server Error).
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex, HttpServletRequest request) {
        var problemDetail =
                buildProblemDetail(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "An unexpected error occurred. Please try again later.",
                        request);

        log.error("Unhandled exception", ex);

        return problemDetail;
    }

    // ===== CLIENT ERROR HANDLERS =====

    private ProblemDetail handleNotFound(ClientError.NotFound error, HttpServletRequest request) {
        String message =
                String.format(
                        "%s with identifier '%s' not found",
                        error.resourceType(), error.identifier());
        var problemDetail = buildProblemDetail(HttpStatus.NOT_FOUND, message, request);
        problemDetail.setProperty("resourceType", error.resourceType());
        problemDetail.setProperty("identifier", error.identifier());

        log.error("Resource not found");

        return problemDetail;
    }

    private ProblemDetail handleValidation(
            ClientError.Validation error, HttpServletRequest request) {
        var problemDetail = buildProblemDetail(HttpStatus.BAD_REQUEST, error.message(), request);
        if (!error.fieldErrors().isEmpty()) {
            problemDetail.setProperty("fieldErrors", error.fieldErrors());
        }

        log.error("Validation error");

        return problemDetail;
    }

    private ProblemDetail handleBadRequest(
            ClientError.BadRequest error, HttpServletRequest request) {
        var problemDetail = buildProblemDetail(HttpStatus.BAD_REQUEST, error.message(), request);

        log.error("Bad request");

        return problemDetail;
    }

    private ProblemDetail handleConflict(ClientError.Conflict error, HttpServletRequest request) {
        var problemDetail = buildProblemDetail(HttpStatus.CONFLICT, error.message(), request);

        log.error("Conflict");

        return problemDetail;
    }

    // ===== SERVER ERROR HANDLERS =====

    private ProblemDetail handleRemoteServiceError(
            ServerError.RemoteServiceError error, HttpServletRequest request) {
        String message =
                String.format(
                        "Service '%s' is currently unavailable: %s",
                        error.service(), error.message());
        var problemDetail = buildProblemDetail(HttpStatus.SERVICE_UNAVAILABLE, message, request);
        problemDetail.setProperty("service", error.service());

        log.error("Remote service error");

        return problemDetail;
    }

    private ProblemDetail handleInternalError(
            ServerError.InternalError error, HttpServletRequest request) {
        var problemDetail =
                buildProblemDetail(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "An internal error occurred. Please contact support.",
                        request);

        if (error.cause() != null) {
            log.error("Internal error", error.cause());
        } else {
            log.error("Internal error");
        }

        return problemDetail;
    }

    private ProblemDetail handleDatabaseError(
            ServerError.DatabaseError error, HttpServletRequest request) {
        String message = String.format("Database operation '%s' failed", error.operation());
        var problemDetail = buildProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, message, request);
        problemDetail.setProperty("operation", error.operation());

        log.error("Database error");

        return problemDetail;
    }

    // ===== HELPER METHODS =====

    /**
     * Builds a ProblemDetail with consistent structure including traceId.
     *
     * @param status HTTP status
     * @param detail Error detail message
     * @param request HTTP request
     * @return ProblemDetail with traceId
     */
    @SuppressWarnings("null")
    private ProblemDetail buildProblemDetail(
            HttpStatus status, String detail, HttpServletRequest request) {
        var problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setDetail(detail);

        // Add traceId from MDC (set by tracing filter/interceptor)
        String traceId = MDC.get(TRACE_ID_KEY);
        if (traceId != null && !traceId.isEmpty()) {
            problemDetail.setProperty(TRACE_ID_KEY, traceId);
        }

        // Add request path for debugging
        problemDetail.setProperty("path", request.getRequestURI());

        return problemDetail;
    }
}

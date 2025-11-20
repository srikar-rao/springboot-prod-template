package com.dev.org.common.exception;

import java.util.Map;

/**
 * Utility class providing static factory methods for throwing common application exceptions.
 * Using generic return type <T> allows these methods to be used in any return context.
 */
public final class ApplicationExceptions {

    private ApplicationExceptions() {
        // Prevent instantiation
    }

    // ===== CLIENT ERRORS (4xx) =====

    /**
     * Throws a NotFoundException.
     *
     * @param resourceType Type of resource (e.g., "User", "Order")
     * @param identifier Resource identifier
     */
    public static <T> T notFound(String resourceType, String identifier) {
        throw new ApplicationException(new ClientError.NotFound(resourceType, identifier));
    }

    /**
     * Throws a ValidationException with a simple message.
     *
     * @param message Validation error message
     */
    public static <T> T validationError(String message) {
        throw new ApplicationException(new ClientError.Validation(message));
    }

    /**
     * Throws a ValidationException with field-level errors.
     *
     * @param message Overall validation message
     * @param fieldErrors Map of field names to error messages
     */
    public static <T> T validationError(String message, Map<String, String> fieldErrors) {
        throw new ApplicationException(new ClientError.Validation(message, fieldErrors));
    }

    /**
     * Throws a BadRequestException.
     *
     * @param message Description of why the request is invalid
     */
    public static <T> T badRequest(String message) {
        throw new ApplicationException(new ClientError.BadRequest(message));
    }

    /**
     * Throws a ConflictException.
     *
     * @param message Description of the conflict
     */
    public static <T> T conflict(String message) {
        throw new ApplicationException(new ClientError.Conflict(message));
    }

    // ===== SERVER ERRORS (5xx) =====

    /**
     * Throws a RemoteServiceException.
     *
     * @param service Name of the remote service
     * @param message Error details
     */
    public static <T> T remoteServiceError(String service, String message) {
        throw new ApplicationException(new ServerError.RemoteServiceError(service, message));
    }

    /**
     * Throws an InternalServerException.
     *
     * @param message Error description
     */
    public static <T> T internalError(String message) {
        throw new ApplicationException(new ServerError.InternalError(message));
    }

    /**
     * Throws an InternalServerException with cause.
     * Useful for wrapping checked exceptions.
     *
     * @param message Error description
     * @param cause Original exception
     */
    public static <T> T internalError(String message, Throwable cause) {
        throw new ApplicationException(new ServerError.InternalError(message, cause));
    }

    /**
     * Throws a DatabaseException.
     *
     * @param operation Type of operation that failed
     * @param message Error details
     */
    public static <T> T databaseError(String operation, String message) {
        throw new ApplicationException(new ServerError.DatabaseError(operation, message));
    }

    // ===== UTILITY METHODS FOR CHECKED EXCEPTION CONVERSION =====

    /**
     * Wraps a checked exception as an unchecked ApplicationException.
     * Useful in lambda expressions and streams.
     *
     * @param message Context message
     * @param cause The checked exception to wrap
     */
    public static <T> T wrapCheckedException(String message, Throwable cause) {
        throw new ApplicationException(new ServerError.InternalError(message, cause));
    }
}

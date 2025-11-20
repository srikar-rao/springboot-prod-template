package com.dev.org.common.exception;

/**
 * Server errors (5xx) - internal system errors.
 * These are unexpected errors that typically require investigation.
 */
public sealed interface ServerError extends ApplicationError {

    /**
     * Remote service error (503) - external service unavailable or failed.
     *
     * @param service Name of the remote service
     * @param message Error details
     */
    record RemoteServiceError(String service, String message) implements ServerError {}

    /**
     * Internal server error (500) - general system error.
     *
     * @param message Error description
     * @param cause Original exception that caused this error (optional)
     */
    record InternalError(String message, Throwable cause) implements ServerError {
        public InternalError(String message) {
            this(message, null);
        }
    }

    /**
     * Database operation error (500).
     *
     * @param operation Type of operation that failed (e.g., "save", "update", "delete")
     * @param message Error details
     */
    record DatabaseError(String operation, String message) implements ServerError {}
}

package com.dev.org.common.exception;

/**
 * Base sealed interface for all application errors.
 * Permits only ClientError and ServerError subtypes for clear error categorization.
 */
public sealed interface ApplicationError permits ClientError, ServerError {}

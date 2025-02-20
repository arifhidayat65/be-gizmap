package com.apps.util;

import com.apps.payload.response.ErrorResponse;
import com.apps.payload.response.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for handling exceptions and creating standardized error responses
 */
public class ExceptionUtil {

    /**
     * Creates a standard error response
     * @param status HTTP status code
     * @param message Error message
     * @param details Additional error details
     * @return ErrorResponse object
     */
    public static ErrorResponse createErrorResponse(HttpStatus status, String message, Map<String, Object> details) {
        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(LocalDateTime.now());
        error.setStatus(status.value());
        error.setError(status.getReasonPhrase());
        error.setMessage(message);
        error.setDetails(details != null ? details : new HashMap<>());
        return error;
    }

    /**
     * Creates a ResponseEntity with error response for validation errors
     * @param message Error message
     * @param details Validation error details
     * @return ResponseEntity with BAD_REQUEST status
     */
    public static ResponseEntity<ErrorResponse> handleValidationError(String message, Map<String, Object> details) {
        ErrorResponse error = createErrorResponse(HttpStatus.BAD_REQUEST, message, details);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Creates a ResponseEntity with error response for entity not found
     * @param ex EntityNotFoundException
     * @return ResponseEntity with NOT_FOUND status
     */
    public static ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        ErrorResponse error = createErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), null);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * Creates a ResponseEntity with error response for illegal state
     * @param ex IllegalStateException
     * @return ResponseEntity with BAD_REQUEST status
     */
    public static ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
        ErrorResponse error = createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Creates a ResponseEntity with error response for illegal arguments
     * @param ex IllegalArgumentException
     * @return ResponseEntity with BAD_REQUEST status
     */
    public static ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponse error = createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Creates a ResponseEntity with error response for unexpected errors
     * @param ex Exception
     * @return ResponseEntity with INTERNAL_SERVER_ERROR status
     */
    public static ResponseEntity<ErrorResponse> handleUnexpectedError(Exception ex) {
        ErrorResponse error = createErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR, 
            "An unexpected error occurred", 
            Map.of("error", ex.getMessage())
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Creates a simple message response
     * @param message The message
     * @return MessageResponse object
     */
    public static MessageResponse createMessageResponse(String message) {
        return new MessageResponse(message);
    }

    /**
     * Creates a ResponseEntity with success message
     * @param message Success message
     * @return ResponseEntity with OK status
     */
    public static ResponseEntity<MessageResponse> handleSuccess(String message) {
        return ResponseEntity.ok(createMessageResponse(message));
    }
}

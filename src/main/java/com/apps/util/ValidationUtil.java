package com.apps.util;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Utility class for common validation operations
 */
public class ValidationUtil {

    /**
     * Validates if a value is not null
     * @param value The value to check
     * @param message The error message if validation fails
     * @throws IllegalArgumentException if validation fails
     */
    public static void validateNotNull(Object value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Validates if a string is not blank
     * @param value The string to check
     * @param message The error message if validation fails
     * @throws IllegalArgumentException if validation fails
     */
    public static void validateNotBlank(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Validates if a number is positive
     * @param value The number to check
     * @param message The error message if validation fails
     * @throws IllegalArgumentException if validation fails
     */
    public static void validatePositive(Number value, String message) {
        if (value == null || value.doubleValue() <= 0) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Validates if a number is not negative
     * @param value The number to check
     * @param message The error message if validation fails
     * @throws IllegalArgumentException if validation fails
     */
    public static void validateNotNegative(Number value, String message) {
        if (value == null || value.doubleValue() < 0) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Validates if a date is in the future
     * @param date The date to check
     * @param message The error message if validation fails
     * @throws IllegalArgumentException if validation fails
     */
    public static void validateFutureDate(LocalDateTime date, String message) {
        if (date == null || date.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Validates if a value exists in the database
     * @param optional The optional value from the database
     * @param message The error message if validation fails
     * @throws EntityNotFoundException if validation fails
     */
    public static <T> T validateExists(Optional<T> optional, String message) {
        return optional.orElseThrow(() -> new EntityNotFoundException(message));
    }

    /**
     * Validates if a value is unique
     * @param exists Function that checks if the value exists
     * @param message The error message if validation fails
     * @throws IllegalStateException if validation fails
     */
    public static void validateUnique(Supplier<Boolean> exists, String message) {
        if (exists.get()) {
            throw new IllegalStateException(message);
        }
    }

    /**
     * Validates if coordinates are valid
     * @param latitude The latitude to check
     * @param longitude The longitude to check
     * @throws IllegalArgumentException if validation fails
     */
    public static void validateCoordinates(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException("Latitude and longitude are required");
        }
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90");
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180");
        }
    }
}

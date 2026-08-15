package com.thewildchild.management.common.exception;

// Used when request data is invalid.

// Examples:

// Invalid email
// Quantity cannot be negative
// Payment amount should be positive

public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

}
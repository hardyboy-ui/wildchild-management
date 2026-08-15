package com.thewildchild.management.common.exception;

// Used when business rules are violated.

// Examples:

// Table already occupied
// Session already closed
// Order already served

public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

}
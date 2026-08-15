package com.thewildchild.management.common.exception;

// Used when a requested resource doesn't exist.

// Examples:

// User not found
// Menu item not found
// Order not found
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

}
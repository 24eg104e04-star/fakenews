package com.fakenews.detection.exception;

/**
 * Exception thrown when login credentials do not match.
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}

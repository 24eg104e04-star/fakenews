package com.fakenews.detection.exception;

/**
 * Exception thrown when trying to register an already existing email.
 */
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}

package ru.oldzoomer.nodehistj_historic_nodelists.util;

/**
 * Exception thrown when a requested resource is not found.
 * This exception is typically used to indicate that a requested resource
 * does not exist in the system.
 */
public class NotFoundException extends RuntimeException {
    /**
     * Constructs a new NotFoundException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method)
     */
    public NotFoundException(String message) {
        super(message);
    }
}
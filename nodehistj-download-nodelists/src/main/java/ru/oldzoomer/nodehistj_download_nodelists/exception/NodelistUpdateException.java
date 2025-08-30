package ru.oldzoomer.nodehistj_download_nodelists.exception;

/**
 * Exception thrown when there is an error updating a nodelist.
 * This exception is typically used to indicate that there was an issue
 * while updating a nodelist in the system.
 */
public class NodelistUpdateException extends RuntimeException {
    /**
     * Constructs a new NodelistUpdateException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval
     *               by the {@link #getMessage()} method)
     */
    public NodelistUpdateException(String message) {
        super(message);
    }

    /**
     * Constructs a new NodelistUpdateException with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval
     *               by the {@link #getMessage()} method)
     * @param cause the cause (which is saved for later retrieval by the
     *             {@link #getCause()} method). (A null value is permitted,
     *             and indicates that the cause is nonexistent or unknown.)
     */
    public NodelistUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}

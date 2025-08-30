package ru.oldzoomer.nodehistj_newest_nodelists.exception;

/**
 * Exception thrown when no new objects are found.
 * This exception is typically used to indicate that no new objects
 * were found during a search or update operation.
 */
public class NoNewObjects extends RuntimeException {
    /**
     * Constructs a new NoNewObjects exception with a default message.
     */
    public NoNewObjects() {
        super("No new objects found");
    }

    /**
     * Constructs a new NoNewObjects exception with a default message and a cause.
     *
     * @param cause the cause of the exception
     */
    public NoNewObjects(Throwable cause) {
        super("No new objects found", cause);
    }
}

package ru.oldzoomer.nodehistj_newest_nodelists.exception;

/**
 * Exception thrown when no new objects are found during processing.
 * <p>
 * This exception indicates that the system did not find any new objects
 * that needed to be processed, which might be a normal condition or
 * could indicate a problem depending on the context.
 */
public class NoNewObjects extends RuntimeException {
    /**
     * Constructs a new NoNewObjects exception with a default message.
     */
    public NoNewObjects() {
        super("No new objects found");
    }

    /**
     * Constructs a new NoNewObjects exception with a default message
     * and the specified cause.
     *
     * @param cause the cause of this exception
     */
    public NoNewObjects(Throwable cause) {
        super("No new objects found", cause);
    }
}

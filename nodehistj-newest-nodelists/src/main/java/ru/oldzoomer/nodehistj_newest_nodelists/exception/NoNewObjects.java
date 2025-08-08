package ru.oldzoomer.nodehistj_newest_nodelists.exception;

public class NoNewObjects extends RuntimeException {
    public NoNewObjects() {
        super("No new objects found");
    }

    public NoNewObjects(Throwable cause) {
        super("No new objects found", cause);
    }
}

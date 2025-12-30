package ru.oldzoomer.nodehistj_history_diff.exception;

public class DuplicateEntryException extends IllegalStateException {
    public DuplicateEntryException(String msg) {
        super(msg);
    }
}

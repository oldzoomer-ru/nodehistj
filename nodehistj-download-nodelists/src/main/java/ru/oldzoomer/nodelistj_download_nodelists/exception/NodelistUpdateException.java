package ru.oldzoomer.nodelistj_download_nodelists.exception;

public class NodelistUpdateException extends RuntimeException {
    public NodelistUpdateException(String message) {
        super(message);
    }

    public NodelistUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}

package ru.oldzoomer.nodehistj_download_nodelists.exception;

import ru.oldzoomer.nodehistj.common.exception.BusinessException;

/**
 * Exception thrown when there is an error updating a nodelist.
 * This exception is typically used to indicate that there was an issue
 * while updating a nodelist in the system.
 */
public class NodelistUpdateException extends BusinessException {

    public NodelistUpdateException(String message, Throwable cause) {
        super(message, cause, 500, "NODELIST_UPDATE_ERROR");
    }
}

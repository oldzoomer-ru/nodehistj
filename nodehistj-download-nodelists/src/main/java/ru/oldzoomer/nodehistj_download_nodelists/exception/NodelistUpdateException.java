package ru.oldzoomer.nodehistj_download_nodelists.exception;

import org.springframework.http.HttpStatus;
import ru.oldzoomer.nodehistj.common.exception.BusinessException;

/**
 * Exception thrown when there is an error updating a nodelist.
 * This exception is typically used to indicate that there was an issue
 * while updating a nodelist in the system.
 */
public class NodelistUpdateException extends BusinessException {

    public NodelistUpdateException(String message, Throwable cause) {
        super(message, cause, HttpStatus.INTERNAL_SERVER_ERROR, "NODELIST_UPDATE_ERROR");
    }
}

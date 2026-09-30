package com.vitzemtsov.flowmanager.exception.server;

import com.vitzemtsov.flowmanager.exception.basic.FlowManagerException;

public class FileUploadException extends FlowManagerException {

    public FileUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
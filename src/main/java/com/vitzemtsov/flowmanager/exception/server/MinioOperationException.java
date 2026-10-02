package com.vitzemtsov.flowmanager.exception.server;

import com.vitzemtsov.flowmanager.exception.basic.FlowManagerException;

public class MinioOperationException extends FlowManagerException {

    public MinioOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
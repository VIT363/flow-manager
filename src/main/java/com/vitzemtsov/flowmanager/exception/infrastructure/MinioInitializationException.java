package com.vitzemtsov.flowmanager.exception.infrastructure;

import com.vitzemtsov.flowmanager.exception.basic.FlowManagerException;

public class MinioInitializationException extends FlowManagerException {

    public MinioInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
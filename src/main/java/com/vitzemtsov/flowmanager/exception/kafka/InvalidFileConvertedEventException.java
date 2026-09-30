package com.vitzemtsov.flowmanager.exception.kafka;

import com.vitzemtsov.flowmanager.exception.basic.FlowManagerException;

public class InvalidFileConvertedEventException extends FlowManagerException {

    public InvalidFileConvertedEventException(String message) {
        super(message);
    }
}
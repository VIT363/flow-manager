package com.vitzemtsov.flowmanager.exception.client;

import com.vitzemtsov.flowmanager.exception.basic.FlowManagerException;

public class EmptyFileException extends FlowManagerException {

    public EmptyFileException() {
        super("File is empty");
    }
}
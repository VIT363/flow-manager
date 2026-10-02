package com.vitzemtsov.flowmanager.exception.client;

import com.vitzemtsov.flowmanager.exception.basic.FlowManagerException;

import java.util.UUID;

public class FileNotFoundException extends FlowManagerException {

    public FileNotFoundException(UUID id) {
        super("File not found: " + id);
    }
}
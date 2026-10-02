package com.vitzemtsov.flowmanager.exception.client;

import com.vitzemtsov.flowmanager.enums.FileStatus;
import com.vitzemtsov.flowmanager.exception.basic.FlowManagerException;

import java.util.UUID;

public class FileNotReadyException extends FlowManagerException {

    public FileNotReadyException(UUID id, FileStatus status) {
        super("File " + id + " not ready, status: " + status);
    }
}
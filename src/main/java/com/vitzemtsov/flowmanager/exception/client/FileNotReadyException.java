package com.vitzemtsov.flowmanager.exception.client;

import com.vitzemtsov.flowmanager.enums.FileStatus;
import com.vitzemtsov.flowmanager.exception.basic.FlowManagerException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.CONFLICT)
public class FileNotReadyException extends FlowManagerException {

    public FileNotReadyException(UUID id, FileStatus status) {
        super("File " + id + " not ready, status: " + status);
    }
}
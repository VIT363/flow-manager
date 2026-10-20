package com.vitzemtsov.flowmanager.exception.client;

import com.vitzemtsov.flowmanager.exception.basic.FlowManagerException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FileNotFoundException extends FlowManagerException {

    public FileNotFoundException(UUID id) {
        super("File not found: " + id);
    }
}
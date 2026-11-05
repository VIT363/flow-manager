package com.vitzemtsov.flowmanager.dto;

import com.vitzemtsov.flowmanager.enums.FileStatus;

import java.util.UUID;

public record FileStatusResponse(
        UUID fileId,
        FileStatus status,
        String errorMessage,
        String convertedBucket,
        String convertedObjectName
) {
}
package com.vitzemtsov.flowmanager.dto;

import com.vitzemtsov.flowmanager.enums.FileStatus;

import java.util.UUID;

public record FileUploadResponse(
        UUID id,
        FileStatus status) {
}
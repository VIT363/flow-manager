package com.vitzemtsov.flowmanager.controller.dto;

import com.vitzemtsov.flowmanager.enums.FileStatus;

import java.util.UUID;

public record FileUploadResponse(
        UUID id,
        FileStatus status) {
}
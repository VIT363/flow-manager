package com.vitzemtsov.flowmanager.controller;

import com.vitzemtsov.flowmanager.controller.dto.FileStatusResponse;
import com.vitzemtsov.flowmanager.controller.dto.FileUploadResponse;
import com.vitzemtsov.flowmanager.service.FileService;
import com.vitzemtsov.flowmanager.controller.constants.ApiPaths;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.FILES)
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<@NonNull FileUploadResponse> upload(@RequestParam("file") MultipartFile file) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(fileService.upload(file));
    }

    @GetMapping(ApiPaths.FILE_STATUS)
    public FileStatusResponse getStatus(@PathVariable UUID id) {
        return fileService.getStatus(id);
    }

    @GetMapping(ApiPaths.FILE_CONTENT)
    public ResponseEntity<@NonNull InputStreamResource> download(@PathVariable UUID id) {
        return fileService.download(id);
    }
}

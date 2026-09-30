package com.vitzemtsov.flowmanager.service;

import com.vitzemtsov.common.events.FileConversionRequest;
import com.vitzemtsov.flowmanager.controller.dto.FileStatusResponse;
import com.vitzemtsov.flowmanager.controller.dto.FileUploadResponse;
import com.vitzemtsov.flowmanager.entity.FileEntity;
import com.vitzemtsov.flowmanager.enums.FileStatus;
import com.vitzemtsov.flowmanager.exception.client.EmptyFileException;
import com.vitzemtsov.flowmanager.exception.client.FileNotFoundException;
import com.vitzemtsov.flowmanager.exception.client.FileNotReadyException;
import com.vitzemtsov.flowmanager.exception.server.FileUploadException;
import com.vitzemtsov.flowmanager.exception.server.MinioOperationException;
import com.vitzemtsov.flowmanager.kafka.producer.FileConversionProducer;
import com.vitzemtsov.flowmanager.minio.MinioService;
import com.vitzemtsov.flowmanager.repository.FileRepository;
import com.vitzemtsov.flowmanager.util.ObjectNameGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final MinioService minioService;
    private final FileRepository fileRepository;
    private final ObjectNameGenerator objectNameGenerator;
    private final FileConversionProducer fileConversionProducer;

    @Transactional
    public FileUploadResponse upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new EmptyFileException();
        }

        UUID fileId = UUID.randomUUID();
        String objectName = objectNameGenerator.generate(fileId, file.getOriginalFilename());

        try (InputStream in = file.getInputStream()) {
            minioService.upload(objectName, in, file.getSize(), file.getContentType());
        } catch (MinioOperationException | IOException e) {
            throw new FileUploadException("Failed to upload file to MinIO: " + objectName, e);
        }

        FileEntity entity = new FileEntity();
        entity.setId(fileId);
        entity.setOriginalFileName(file.getOriginalFilename());
        entity.setSourceBucket(minioService.getBucket());
        entity.setSourceObjectName(objectName);
        entity.setStatus(FileStatus.PROCESSING);
        fileRepository.save(entity);

        fileConversionProducer.send(new FileConversionRequest(
                fileId, minioService.getBucket(), objectName
        ));

        log.info("File uploaded, event sent to to-convert: id={}, objectName={}", fileId, objectName);

        return new FileUploadResponse(fileId, entity.getStatus());
    }

    public FileStatusResponse getStatus(UUID id) {
        FileEntity entity = fileRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException(id));

        return new FileStatusResponse(
                entity.getId(),
                entity.getStatus(),
                entity.getErrorMessage(),
                entity.getConvertedBucket(),
                entity.getConvertedObjectName()
        );
    }

    public DownloadResult download(UUID id) {
        FileEntity entity = fileRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException(id));

        if (entity.getStatus() != FileStatus.SUCCESS) {
            throw new FileNotReadyException(id, entity.getStatus());
        }

        String bucket = entity.getConvertedBucket();
        String object = entity.getConvertedObjectName();

        InputStream stream = minioService.download(bucket, object);
        long size = minioService.statSize(bucket, object);

        String downloadName = buildPdfFileName(entity.getOriginalFileName());

        return new DownloadResult(stream, size, downloadName);
    }

    private static String buildPdfFileName(String originalFileName) {
        if (originalFileName == null || originalFileName.isBlank()) {
            return "result.pdf";
        }

        int dot = originalFileName.lastIndexOf('.');
        String base = (dot > 0) ? originalFileName.substring(0, dot) : originalFileName;

        return base + ".pdf";
    }

    public record DownloadResult(InputStream stream, long size, String fileName) {
    }
}
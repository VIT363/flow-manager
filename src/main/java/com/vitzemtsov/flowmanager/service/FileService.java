package com.vitzemtsov.flowmanager.service;

import com.vitzemtsov.common.events.FileConversionRequest;
import com.vitzemtsov.flowmanager.dto.FileStatusResponse;
import com.vitzemtsov.flowmanager.dto.FileUploadResponse;
import com.vitzemtsov.flowmanager.entity.FileEntity;
import com.vitzemtsov.flowmanager.enums.FileStatus;
import com.vitzemtsov.flowmanager.exception.business.FileTooLargeException;
import com.vitzemtsov.flowmanager.exception.business.InactiveSubscriptionException;
import com.vitzemtsov.flowmanager.exception.client.EmptyFileException;
import com.vitzemtsov.flowmanager.exception.client.FileNotFoundException;
import com.vitzemtsov.flowmanager.exception.client.FileNotReadyException;
import com.vitzemtsov.flowmanager.exception.infrastructure.FileUploadException;
import com.vitzemtsov.flowmanager.exception.infrastructure.MinioOperationException;
import com.vitzemtsov.flowmanager.kafka.producer.FileConversionProducer;
import com.vitzemtsov.flowmanager.minio.MinioProperties;
import com.vitzemtsov.flowmanager.minio.MinioService;
import com.vitzemtsov.flowmanager.repository.FileRepository;
import com.vitzemtsov.flowmanager.security.CurrentUserService;
import com.vitzemtsov.flowmanager.subscription.dto.SubscriptionResponse;
import com.vitzemtsov.flowmanager.subscription.service.SubscriptionLookupService;
import com.vitzemtsov.flowmanager.util.ObjectNameGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final MinioService minioService;
    private final FileRepository fileRepository;
    private final ObjectNameGenerator objectNameGenerator;
    private final FileConversionProducer fileConversionProducer;
    private final MinioProperties minioProperties;

    private final CurrentUserService currentUserService;
    private final SubscriptionLookupService subscriptionLookupService;

    private static final long FREE_MAX_FILE_SIZE = 100L * 1024 * 1024;

    @Transactional
    public FileUploadResponse upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new EmptyFileException();
        }

        String login = currentUserService.getLogin();

        SubscriptionResponse subscription =
                subscriptionLookupService.getByLogin(login);

        if (subscription.subscriptionType()
                == SubscriptionResponse.SubscriptionType.FREE
                && file.getSize() > FREE_MAX_FILE_SIZE) {
            throw new FileTooLargeException(
                    "FREE subscription allows files up to 100 MiB"
            );
        }

        if (subscription.subscriptionType()
                == SubscriptionResponse.SubscriptionType.PAID
                && !subscription.activePaidSubscription()) {
            throw new InactiveSubscriptionException(
                    "PAID subscription is expired"
            );
        }

        UUID fileId = UUID.randomUUID();
        String objectName = objectNameGenerator.generate(fileId, file.getOriginalFilename());

        try (InputStream in = file.getInputStream()) {
            minioService.upload(objectName, in, file.getSize(), file.getContentType());
        } catch (MinioOperationException | IOException e) {
            throw new FileUploadException("Failed to upload file to MinIO: " + objectName, e);
        }

        String bucket = minioProperties.bucket();

        FileEntity entity = new FileEntity();
        entity.setId(fileId);
        entity.setOriginalFileName(file.getOriginalFilename());
        entity.setSourceBucket(bucket);
        entity.setSourceObjectName(objectName);
        entity.setStatus(FileStatus.PROCESSING);
        fileRepository.save(entity);

        fileConversionProducer.send(new FileConversionRequest(
                fileId, bucket, objectName
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

    public ResponseEntity<@NonNull InputStreamResource> download(UUID id) {
        FileEntity entity = fileRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException(id));

        if (entity.getStatus() != FileStatus.SUCCESS) {
            throw new FileNotReadyException(id, entity.getStatus());
        }

        String bucket = entity.getConvertedBucket();
        String object = entity.getConvertedObjectName();

        long size = minioService.statSize(bucket, object);
        InputStream stream = minioService.download(bucket, object);

        String downloadName = buildPdfFileName(entity.getOriginalFileName());

        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(downloadName, StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(size)
                .body(new InputStreamResource(stream));
    }

    private static String buildPdfFileName(String originalFileName) {
        if (originalFileName == null || originalFileName.isBlank()) {
            return "result.pdf";
        }

        int dot = originalFileName.lastIndexOf('.');
        String base = (dot > 0) ? originalFileName.substring(0, dot) : originalFileName;

        return base + ".pdf";
    }
}
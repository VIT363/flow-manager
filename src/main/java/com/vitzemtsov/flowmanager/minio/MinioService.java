package com.vitzemtsov.flowmanager.minio;

import com.vitzemtsov.flowmanager.exception.server.MinioInitializationException;
import com.vitzemtsov.flowmanager.exception.server.MinioOperationException;
import io.minio.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@Slf4j
public class MinioService {

    private final MinioClient minioClient;
    private final MinioProperties properties;

    public MinioService(MinioClient minioClient, MinioProperties properties) {
        this.minioClient = minioClient;
        this.properties = properties;
    }

    @PostConstruct
    public void ensureBucketExists() {
        String bucket = properties.bucket();

        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucket)
                            .build()
            );

            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucket)
                        .build()
                );
                log.info("Bucket '{}' created", bucket);
            }
        } catch (Exception e) {
            throw new MinioInitializationException(
                    "Cannot initialize MinIO bucket: " + bucket,
                    e
            );
        }
    }

    public void upload(String objectName, InputStream inputStream, long size, String contentType) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(properties.bucket())
                    .object(objectName)
                    .stream(inputStream, size, -1)
                    .contentType(contentType)
                    .build()
            );
        } catch (Exception e) {
            throw new MinioOperationException(
                    "Cannot upload object: " + objectName,
                    e
            );
        }
    }

    public InputStream download(String bucket, String objectName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            throw new MinioOperationException(
                    "Cannot download object: " + bucket + "/" + objectName,
                    e
            );
        }
    }

    public long statSize(String bucket, String objectName) {
        try {
            return minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .build()
            ).size();
        } catch (Exception e) {
            throw new MinioOperationException(
                    "Cannot stat object: " + bucket + "/" + objectName, e);
        }
    }
}
package com.vitzemtsov.flowmanager.entity;

import com.vitzemtsov.flowmanager.enums.FileStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "files")
@Getter
@Setter
public class FileEntity {

    @Id
    private UUID id;

    @Column(name = "original_file_name", nullable = false)
    private String originalFileName;

    @Column(name = "source_bucket", nullable = false)
    private String sourceBucket;

    @Column(name = "source_object_name", nullable = false)
    private String sourceObjectName;

    @Column(name = "converted_bucket")
    private String convertedBucket;

    @Column(name = "converted_object_name")
    private String convertedObjectName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private FileStatus status;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }

        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
package com.vitzemtsov.flowmanager.repository;

import com.vitzemtsov.flowmanager.entity.FileEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileRepository extends JpaRepository<@NonNull FileEntity, @NonNull UUID> {
}

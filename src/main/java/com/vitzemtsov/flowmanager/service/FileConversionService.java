package com.vitzemtsov.flowmanager.service;

import com.vitzemtsov.common.events.ConversionStatus;
import com.vitzemtsov.common.events.FileConvertedEvent;
import com.vitzemtsov.flowmanager.entity.FileEntity;
import com.vitzemtsov.flowmanager.enums.FileStatus;
import com.vitzemtsov.flowmanager.exception.client.FileNotFoundException;
import com.vitzemtsov.flowmanager.exception.kafka.InvalidFileConvertedEventException;
import com.vitzemtsov.flowmanager.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileConversionService {

    private final FileRepository fileRepository;

    @Transactional
    public void applyResult(FileConvertedEvent event) {

        if (event == null || event.fileId() == null) {
            throw new InvalidFileConvertedEventException(
                    "Received invalid file converted event"
            );
        }

        FileEntity file = fileRepository.findById(event.fileId())
                .orElseThrow(() -> new FileNotFoundException(event.fileId()));

        if (file.getStatus() == FileStatus.SUCCESS
                || file.getStatus() == FileStatus.ERROR) {

            log.info(
                    "File already processed, ignoring duplicate event: id={}, status={}",
                    event.fileId(),
                    file.getStatus()
            );
            return;
        }

        if (event.status() == ConversionStatus.SUCCESS) {
            file.setStatus(FileStatus.SUCCESS);
            file.setConvertedBucket(event.bucketName());
            file.setConvertedObjectName(event.objectName());
            file.setErrorMessage(null);
        } else {
            file.setStatus(FileStatus.ERROR);
            file.setErrorMessage(event.errorMessage());
        }

        fileRepository.save(file);

        log.info(
                "File processing completed: id={}, status={}",
                event.fileId(),
                event.status()
        );
    }
}
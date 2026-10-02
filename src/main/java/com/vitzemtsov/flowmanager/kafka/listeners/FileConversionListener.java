package com.vitzemtsov.flowmanager.kafka.listeners;

import com.vitzemtsov.common.events.FileConvertedEvent;
import com.vitzemtsov.flowmanager.service.FileConversionService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileConversionListener {

    private final FileConversionService fileConversionService;

    @KafkaListener(topics = "${kafka.topics.converted}")
    public void consume(FileConvertedEvent event) {
        fileConversionService.applyResult(event);
    }
}
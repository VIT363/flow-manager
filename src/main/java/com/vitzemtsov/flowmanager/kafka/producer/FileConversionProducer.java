package com.vitzemtsov.flowmanager.kafka.producer;


import com.vitzemtsov.common.events.FileConversionRequest;
import com.vitzemtsov.flowmanager.kafka.KafkaTopicsProperties;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileConversionProducer {

    private final KafkaTemplate<@NonNull String, @NonNull FileConversionRequest> kafkaTemplate;
    private final KafkaTopicsProperties topics;

    public void send(FileConversionRequest request) {
        kafkaTemplate.send(
                topics.toConvert(),
                request.fileId().toString(),
                request
        );
    }
}
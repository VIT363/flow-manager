package com.vitzemtsov.flowmanager.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka.topics")
public record KafkaTopicsProperties(
        String toConvert,
        String converted
) {
}
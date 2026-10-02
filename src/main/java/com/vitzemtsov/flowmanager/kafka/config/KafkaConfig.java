package com.vitzemtsov.flowmanager.kafka.config;

import com.vitzemtsov.flowmanager.exception.kafka.InvalidFileConvertedEventException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@Configuration
public class KafkaConfig {

    @Bean
    public DefaultErrorHandler kafkaErrorHandler() {

        ConsumerRecordRecoverer recoverer = (record, exception) ->
                log.error(
                        "Kafka message failed after retries: topic={}, partition={}, offset={}",
                        record.topic(),
                        record.partition(),
                        record.offset(),
                        exception
                );

        DefaultErrorHandler handler = new DefaultErrorHandler(
                recoverer,
                new FixedBackOff(1000L, 3L)
        );

        handler.addNotRetryableExceptions(
                InvalidFileConvertedEventException.class
        );

        return handler;
    }
}

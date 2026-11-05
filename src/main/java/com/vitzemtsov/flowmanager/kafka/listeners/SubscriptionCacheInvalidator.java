package com.vitzemtsov.flowmanager.kafka.listeners;

import com.vitzemtsov.flowmanager.subscription.service.SubscriptionLookupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionCacheInvalidator {

    private final SubscriptionLookupService subscriptionLookupService;

    @KafkaListener(
            topics = "subscription-expired",
            groupId = "flow-manager-group",
            properties = {"spring.deserializer.value.delegate.class=org.apache.kafka.common.serialization.StringDeserializer"}
    )
    public void onSubscriptionExpired(String login) {
        log.info("Received Kafka event: subscription expired for user '{}'", login);

        subscriptionLookupService.evictCache(login);
    }
}
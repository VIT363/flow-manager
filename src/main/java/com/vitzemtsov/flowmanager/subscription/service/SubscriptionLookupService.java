package com.vitzemtsov.flowmanager.subscription.service;

import com.vitzemtsov.flowmanager.subscription.client.SubscriptionClient;
import com.vitzemtsov.flowmanager.subscription.dto.SubscriptionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionLookupService {

    private final SubscriptionClient subscriptionClient;

    @Cacheable(value = "subscriptions", key = "#login")
    public SubscriptionResponse getByLogin(String login) {
        log.info("CACHE MISS (fetching from client): user='{}'", login);

        return subscriptionClient.getByLogin(login);
    }

    @CacheEvict(value = "subscriptions", key = "#login")
    public void evictCache(String login) {
        log.info("CACHE INVALIDATED (evicted): user='{}'", login);
    }
}
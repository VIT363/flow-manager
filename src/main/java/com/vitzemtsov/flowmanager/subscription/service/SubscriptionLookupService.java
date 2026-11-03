package com.vitzemtsov.flowmanager.subscription.service;

import com.vitzemtsov.flowmanager.subscription.client.SubscriptionClient;
import com.vitzemtsov.flowmanager.subscription.dto.SubscriptionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionLookupService {

    private final SubscriptionClient subscriptionClient;
    private final RedisTemplate<String, SubscriptionResponse> subscriptionRedisTemplate;

    private static final String KEY_PREFIX = "subscription:";
    private static final Duration TTL = Duration.ofMinutes(10);

    public SubscriptionResponse getByLogin(String login) {
        String key = KEY_PREFIX + login;

        SubscriptionResponse cached = subscriptionRedisTemplate.opsForValue().get(key);
        if (cached != null) {
            log.info("CACHE HIT: user='{}'", login);
            return cached;
        }

        log.info("CACHE MISS: user='{}'", login);
        SubscriptionResponse fresh = subscriptionClient.getByLogin(login);

        if (fresh != null) {
            subscriptionRedisTemplate.opsForValue().set(key, fresh, TTL);
        }
        return fresh;
    }

    public void evictCache(String login) {
        String key = KEY_PREFIX + login;
        Boolean deleted = subscriptionRedisTemplate.delete(key);
        log.info("CACHE INVALIDATED: Removed key '{}' (deleted: {})", key, deleted);
    }
}
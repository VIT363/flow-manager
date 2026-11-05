package com.vitzemtsov.flowmanager.config;

import com.vitzemtsov.flowmanager.subscription.dto.SubscriptionResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
public class CacheConfig {

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        JacksonJsonRedisSerializer<@NonNull SubscriptionResponse> serializer =
                new JacksonJsonRedisSerializer<>(SubscriptionResponse.class);

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(serializer));

        return builder -> builder
                .withCacheConfiguration("subscriptions", config);
    }
}
package com.vitzemtsov.flowmanager.config;

import com.vitzemtsov.flowmanager.subscription.dto.SubscriptionResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, SubscriptionResponse> subscriptionRedisTemplate(
            RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, SubscriptionResponse> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());

        JacksonJsonRedisSerializer<@NonNull SubscriptionResponse> serializer =
                new JacksonJsonRedisSerializer<>(SubscriptionResponse.class);

        template.setValueSerializer(serializer);
        return template;
    }
}
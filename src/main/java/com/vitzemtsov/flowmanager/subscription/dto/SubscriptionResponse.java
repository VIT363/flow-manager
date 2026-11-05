
package com.vitzemtsov.flowmanager.subscription.dto;

import java.time.Instant;

public record SubscriptionResponse(
        String login,
        SubscriptionType subscriptionType,
        Instant expiresAt,
        boolean activePaidSubscription
) {
    public enum SubscriptionType {
        FREE,
        PAID
    }
}

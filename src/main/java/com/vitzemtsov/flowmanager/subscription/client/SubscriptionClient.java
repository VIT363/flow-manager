
package com.vitzemtsov.flowmanager.subscription.client;

import com.vitzemtsov.flowmanager.constants.ApiPaths;
import com.vitzemtsov.flowmanager.subscription.dto.SubscriptionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "subscription-service")
public interface SubscriptionClient {

    @GetMapping(ApiPaths.SUBSCRIPTIONS + ApiPaths.SUBSCRIPTION_LOGIN)
    SubscriptionResponse getByLogin(@PathVariable("login") String login);
}

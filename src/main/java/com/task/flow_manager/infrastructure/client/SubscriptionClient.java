package com.task.flow_manager.infrastructure.client;

import com.task.flow_manager.application.dto.web.SubscriptionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "subscription-service", url = "${clients.subscription-service.url:}")
public interface SubscriptionClient {

    @GetMapping("/subscriptions/{userLogin}")
    SubscriptionResponse getSubscription(@PathVariable String userLogin);

}

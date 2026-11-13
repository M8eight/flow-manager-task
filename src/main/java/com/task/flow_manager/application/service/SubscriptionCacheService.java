package com.task.flow_manager.application.service;

import com.task.flow_manager.application.dto.web.SubscriptionResponse;
import com.task.flow_manager.infrastructure.client.SubscriptionClient;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionCacheService {

    private final SubscriptionClient subscriptionClient;

    @Cacheable(value = "userSubscription", key = "#userLogin")
    public SubscriptionResponse getSubscription(String userLogin) {
        return subscriptionClient.getSubscription(userLogin);
    }

    @CacheEvict(
        value = "userSubscription",
        key = "#userLogin",
        condition = "#userLogin != null && !#userLogin.isBlank()"
    )
    public void evictSubscription(String userLogin) {
    }

}

package com.task.flow_manager.application.service;

import com.task.flow_manager.application.dto.web.SubscriptionResponse;
import com.task.flow_manager.application.enums.SubscriptionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static com.task.flow_manager.application.enums.SubscriptionStatus.FREE;
import static com.task.flow_manager.application.enums.SubscriptionStatus.PAID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionCacheService subscriptionCacheService;

    public SubscriptionStatus getStatus(String userLogin) {

        SubscriptionResponse subscription = subscriptionCacheService.getSubscription(userLogin);
        if ( subscription.status() == PAID
                && subscription.expiresAt() != null
                && subscription.expiresAt().isBefore(Instant.now()) ) {
            subscriptionCacheService.evictSubscription(userLogin);
            log.warn("Manual evict {}", userLogin);
            return FREE;

        }

        return subscription.status();

    }

}

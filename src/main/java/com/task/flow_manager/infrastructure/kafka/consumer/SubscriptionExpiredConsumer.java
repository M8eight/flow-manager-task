package com.task.flow_manager.infrastructure.kafka.consumer;

import com.task.flow_manager.application.service.SubscriptionCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class SubscriptionExpiredConsumer {

    private final SubscriptionCacheService subscriptionCacheService;

    @KafkaListener(topics = "${app.kafka.topics.subscription-input}", groupId = "flow-manager")
    public void onCompleted(List<String> userLogins) {
        userLogins.forEach(subscriptionCacheService::evictSubscription);
    }

}

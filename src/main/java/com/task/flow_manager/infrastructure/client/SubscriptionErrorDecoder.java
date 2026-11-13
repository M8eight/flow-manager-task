package com.task.flow_manager.infrastructure.client;

import com.task.flow_manager.application.exception.NotFoundException;
import com.task.flow_manager.application.exception.ServiceUnavailableException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import static com.task.flow_manager.application.enums.ErrorCode.SUBSCRIPTION_NOT_FOUND;
import static com.task.flow_manager.application.enums.ErrorCode.SUBSCRIPTION_SERVICE_UNAVAILABLE;

@Slf4j
public class SubscriptionErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        return switch (response.status()) {
            case 404 -> {
                log.warn("Подписка не найдена в subscription-service (404)");
                yield new NotFoundException("Subscription not found", SUBSCRIPTION_NOT_FOUND);
            }
            case 500, 502, 503, 504 -> {
                log.error("Сервис подписок вернул ошибку {}", response.status());
                yield new ServiceUnavailableException("Сервис подписок временно недоступен", SUBSCRIPTION_SERVICE_UNAVAILABLE);
            }
            default -> defaultDecoder.decode(methodKey, response);
        };
    }
}

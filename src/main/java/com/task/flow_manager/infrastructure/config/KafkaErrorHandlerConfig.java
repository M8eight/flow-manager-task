package com.task.flow_manager.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;


@Configuration
@Slf4j
public class KafkaErrorHandlerConfig {

    @Bean
    public CommonErrorHandler errorHandler(KafkaTemplate<Object, Object> template) {
        var deadLetterPublishingRecoverer = new DeadLetterPublishingRecoverer(template);
        deadLetterPublishingRecoverer.setFailIfSendResultIsError(true);

        ConsumerRecordRecoverer recoverer = (record, ex) -> {
            try {
                deadLetterPublishingRecoverer.accept(record, ex);
            } catch (Exception exception) {
                log.error("Не удалось поставить FAILED для {}", record.key(), exception);
                throw exception;
            }
        };

        return handleBackoff(recoverer);
    }

    private DefaultErrorHandler handleBackoff(ConsumerRecordRecoverer recoverer) {

        //Суммарная задержка ретраев (82s) 6+12+24+40
        var backoff = new ExponentialBackOffWithMaxRetries(4);
        backoff.setInitialInterval(6_000);
        backoff.setMultiplier(2);
        backoff.setMaxInterval(40_000);

        var errorHandler = new DefaultErrorHandler(recoverer, backoff);
        errorHandler.setBackOffFunction((record, ex) -> backoff);

        return errorHandler;
    }

}

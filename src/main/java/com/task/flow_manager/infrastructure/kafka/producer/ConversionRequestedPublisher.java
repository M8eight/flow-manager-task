package com.task.flow_manager.infrastructure.kafka.producer;

import com.task.flow_manager.application.dto.FileConversionRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversionRequestedPublisher {

    @Value("${app.kafka.topics.input}")
    private String inputTopic;

    private final KafkaTemplate<String, FileConversionRequestedEvent> kafkaTemplate;

    public void sendSync(FileConversionRequestedEvent payload) throws Exception {
        String eventId = payload.eventId().toString();
        try {
            kafkaTemplate.send(inputTopic, eventId, payload)
                    .get(2, TimeUnit.SECONDS);
            log.debug("Отправлено в kafka {}", eventId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new Exception("Interrupted", e.getCause());
        }
    }

}

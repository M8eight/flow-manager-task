package com.task.flow_manager.infrastructure.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.flow_manager.application.dto.event.FileConversionRequestedEvent;
import com.task.flow_manager.application.service.OutboxService;
import com.task.flow_manager.infrastructure.kafka.producer.ConversionRequestedPublisher;
import com.task.flow_manager.infrastructure.persitence.entity.Outbox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;
    private final ConversionRequestedPublisher conversionRequestedPublisher;

    @Scheduled(fixedDelay = 500)
    public void process() {
        List<Outbox> pendingEvents = outboxService.getBatch(50);

        for (Outbox outbox : pendingEvents) {
            try {
                FileConversionRequestedEvent eventDto = objectMapper
                        .treeToValue(outbox.getPayload(), FileConversionRequestedEvent.class);
                conversionRequestedPublisher.sendSync(eventDto);

                outboxService.setSuccess(outbox.getId());
            } catch (Exception e) {
                log.error("Ошибка публикации Outbox id={}",  outbox.getId(), e);
                outboxService.retryOrFail(outbox, e.getMessage());
            }
        }
    }

}

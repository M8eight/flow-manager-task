package com.task.flow_manager.application.service;

import com.task.flow_manager.infrastructure.persitence.entity.Outbox;
import com.task.flow_manager.infrastructure.persitence.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxService {

    private final OutboxRepository outboxRepository;

    @Value("${app.outbox.max-attempts}")
    int maxAttempts;

    @Value("${app.outbox.lease-seconds}")
    int leaseSeconds;

    @Transactional
    public List<Outbox> getBatch(int batchSize) {
        return outboxRepository.findBatch(batchSize);
    }

    @Transactional
    public void setSuccess(UUID eventId) {
        int updated = outboxRepository.setSuccess(eventId);
        checkIsUpdated(eventId, updated);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void retryOrFail(Outbox outbox, String error) {
        if (outbox.getRetryAttempts() >= maxAttempts) {
            outboxRepository.setFailed(outbox.getId(), error);
            return;
        }

        Instant nextAttemptAt = Instant.now().plusSeconds(
                Math.min(
                        (long) maxAttempts * leaseSeconds,
                        (long) Math.pow(2, outbox.getRetryAttempts()
                        )
                )
        );

        outboxRepository.setPending(
                outbox.getId(),
                nextAttemptAt,
                error
        );

    }

    private static void checkIsUpdated(UUID eventId, int updated) {
        if (updated == 0) {
            log.warn("Не удалось сменить статус eventId={}", eventId);
        }
    }

}

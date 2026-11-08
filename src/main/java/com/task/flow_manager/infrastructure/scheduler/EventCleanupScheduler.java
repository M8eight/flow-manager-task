package com.task.flow_manager.infrastructure.scheduler;

import com.task.flow_manager.infrastructure.persitence.repository.FileMetadataRepository;
import com.task.flow_manager.infrastructure.persitence.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventCleanupScheduler {

    private final FileMetadataRepository fileMetadataRepository;
    private final OutboxRepository outboxRepository;

    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void cleanupProcessedMessages() {
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(14);
        Instant threshold = localDateTime.toInstant(ZoneOffset.UTC);

        int deleteBatchLimit = 5000;
        int inboxDeletedCount = fileMetadataRepository.deleteByStatusAndCreatedAtBefore(threshold, deleteBatchLimit);
        int outboxDeletedCount = outboxRepository.deleteByStatusAndCreatedAtBefore(threshold, deleteBatchLimit);
        log.info("Удалено {} просроченных записей из fileMetadata", inboxDeletedCount);
        log.info("Удалено {} просроченных записей из outbox", outboxDeletedCount);
    }

}

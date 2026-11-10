package com.task.flow_manager.infrastructure.scheduler;

import com.task.flow_manager.application.service.EventCleanupService;
import com.task.flow_manager.infrastructure.persitence.repository.FileMetadataRepository;
import com.task.flow_manager.infrastructure.persitence.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventCleanupScheduler {

    private final FileMetadataRepository fileMetadataRepository;
    private final OutboxRepository outboxRepository;
    private final EventCleanupService eventCleanupService;

    @Scheduled(cron = "0 0 2 * * *")
    public void cleanupProcessedMessages() {
        Instant pendingThreshold = Instant.now().minus(14, ChronoUnit.DAYS);
        Instant finalThreshold = Instant.now().minus(7, ChronoUnit.DAYS);

        log.info("Запуск плановой очистки устаревших сообщений...");

        int inboxPendingTotal = eventCleanupService
                .cleanInBatch(pendingThreshold, fileMetadataRepository::deletePending);
        int inboxFinalTotal = eventCleanupService
                .cleanInBatch(finalThreshold, fileMetadataRepository::deleteFinal);

        int outboxPendingTotal = eventCleanupService
                .cleanInBatch(pendingThreshold, outboxRepository::deletePending);
        int outboxFinalTotal = eventCleanupService
                .cleanInBatch(finalThreshold, outboxRepository::deleteFinal);

        log.info("Очистка завершена. Удалено из fileMetadata: PENDING = {}, FINAL = {}", inboxPendingTotal, inboxFinalTotal);
        log.info("Удалено из outbox: PENDING = {}, FINAL = {}", outboxPendingTotal, outboxFinalTotal);
    }



}

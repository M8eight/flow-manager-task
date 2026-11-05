package com.task.flow_manager.infrastructure.scheduler;

import com.task.flow_manager.application.service.FileMetadataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventWatchdogScheduler {

    private final FileMetadataService fileMetadataService;

    @Value("${app.watchdog.timeout}")
    Duration timeout;

    @Scheduled(fixedDelayString = "${app.watchdog.interval:PT1M}")
    public void run() {
        int n = fileMetadataService.markExpired(timeout);
        if (n > 0) {
            log.warn("Watchdog: {} файлов переведено в FAILED", n);
        }
    }

}

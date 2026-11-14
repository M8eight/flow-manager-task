package com.task.flow_manager.infrastructure.metrics;

import com.task.flow_manager.application.enums.FileStatus;
import com.task.flow_manager.application.enums.SubscriptionStatus;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class UploadMetricsRecorder {

    private final MeterRegistry meterRegistry;
    private final DistributionSummary fileSizeSummary;

    public UploadMetricsRecorder(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.fileSizeSummary = DistributionSummary.builder("flow.file.size")
                .description("Размер входящих файлов")
                .baseUnit("bytes")
                .register(meterRegistry);
    }

    public void recordCompleted(String fileExtension, long fileSizeBytes, long s3DurationMs, long totalDurationMs) {
        fileSizeSummary.record(fileSizeBytes);

        Timer.builder("flow.s3.save.duration")
                .description("Время сохранения файла в S3")
                .register(meterRegistry)
                .record(Duration.ofMillis(s3DurationMs));

        Timer.builder("flow.storage.total.duration")
                .description("Общее время сохранения в S3 и публикации события")
                .register(meterRegistry)
                .record(Duration.ofMillis(totalDurationMs));

        Counter.builder("flow.files.processed")
                .description("Количество обработанных файлов")
                .tag("fileStatus", FileStatus.COMPLETED.name())
                .tag("fileExtension", fileExtension)
                .register(meterRegistry)
                .increment();
    }

    public void recordError(String stage, String fileExtension) {
        Counter.builder("flow.files.processed")
                .description("Количество обработанных файлов")
                .tag("stage", stage)
                .tag("fileExtension", fileExtension)
                .register(meterRegistry)
                .increment();
    }

    public void recordLimitRejected(SubscriptionStatus subscriptionStatus) {
        Counter.builder("flow.upload.rejected")
                .description("Отказы в загрузке по бизнес-правилам")
                .tag("reason", "limit_exceeded")
                .tag("subscriptionStatus", subscriptionStatus.name())
                .register(meterRegistry)
                .increment();
    }

}

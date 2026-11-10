package com.task.flow_manager.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.flow_manager.application.dto.event.FileConversionCompletedEvent;
import com.task.flow_manager.application.dto.event.FileConversionRequestedEvent;
import com.task.flow_manager.application.dto.web.FileMetadataResponse;
import com.task.flow_manager.application.enums.FileStatus;
import com.task.flow_manager.application.enums.OutboxStatus;
import com.task.flow_manager.application.exception.NotFoundException;
import com.task.flow_manager.application.mapper.FileMetadataMapper;
import com.task.flow_manager.infrastructure.kafka.ErrorCodeMapper;
import com.task.flow_manager.infrastructure.persitence.entity.FileMetadata;
import com.task.flow_manager.infrastructure.persitence.entity.Outbox;
import com.task.flow_manager.infrastructure.persitence.repository.FileMetadataRepository;
import com.task.flow_manager.infrastructure.persitence.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static com.task.flow_manager.application.enums.ErrorCode.FILE_METADATA_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileMetadataService {

    private final FileMetadataRepository fileMetadataRepository;
    private final OutboxRepository outboxRepository;
    private final FileMetadataMapper fileMetadataMapper;
    private final ObjectMapper objectMapper;

    @Value("${app.s3.bucket-name}")
    String bucketName;

    @Value("${app.s3.folders.incoming}")
    private String incomingFolder;

    public FileMetadata getById(UUID id) {
        return fileMetadataRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("FileMetadata not found", FILE_METADATA_NOT_FOUND));
    }

    @Transactional
    public void createAndPublish(UUID fileId, String objectKey) {

        FileMetadata fileMetadata = FileMetadata.builder()
                .fileId(fileId)
                .status(FileStatus.PENDING)
                .resultObjectKey(objectKey)
                .build();

        fileMetadataRepository.save(fileMetadata);

        FileConversionRequestedEvent event = FileConversionRequestedEvent.builder()
                .eventId(fileId)
                .bucket(bucketName)
                .objectKey(objectKey)
                .build();

        Outbox outbox = Outbox.builder()
                .id(fileId)
                .nextAttemptAt(Instant.now())
                .status(OutboxStatus.PENDING)
                .payload(objectMapper.valueToTree(event))
                .build();

        outboxRepository.save(outbox);
    }

    @Transactional
    public void setCompleted(FileConversionCompletedEvent event) {
        boolean isFailed = event.errorCode() != null;
        String bucket = isFailed ? null : event.bucket();
        String objectKey = isFailed ? null : event.objectKey();
        FileStatus status = isFailed ? FileStatus.FAILED : FileStatus.COMPLETED;

        int updated = fileMetadataRepository.setResult(
                event.correlationId(),
                status,
                bucket,
                objectKey,
                ErrorCodeMapper.mapErrorCode(event.errorCode()),
                event.errorMessage(),
                Instant.now()
        );
        if (updated == 0) {
            log.info("Результат по {} проигнорирован: файл неизвестен или уже обработан (errorCode={})",
                    event.correlationId(), event.errorCode());
        }
    }

    @Transactional
    public int markExpired(Duration timeout) {
        return fileMetadataRepository.markExpired(FileStatus.PENDING, FileStatus.FAILED,
                Instant.now().minus(timeout), Instant.now());
    }

    public FileMetadataResponse getInfoById(UUID fileMetadataId) {
        FileMetadata fileMetadata = getById(fileMetadataId);
        log.info(fileMetadata.getStatus().toString());
        return fileMetadataMapper.toDto(fileMetadata);
    }

}

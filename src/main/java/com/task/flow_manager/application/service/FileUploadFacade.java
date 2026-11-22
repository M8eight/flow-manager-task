package com.task.flow_manager.application.service;

import com.task.flow_manager.application.enums.SubscriptionStatus;
import com.task.flow_manager.application.exception.BadRequestException;
import com.task.flow_manager.infrastructure.config.properties.UploadLimitsProperties;
import com.task.flow_manager.infrastructure.metrics.UploadMetricsRecorder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static com.task.flow_manager.application.enums.ErrorCode.FILE_IS_EMPTY;
import static com.task.flow_manager.application.enums.SubscriptionStatus.FREE;

@Service
@RequiredArgsConstructor
public class FileUploadFacade {

    private final SubscriptionService subscriptionService;
    private final UploadLimitsProperties uploadLimitsProperties;
    private final FileStorageService fileStorageService;
    private final UploadMetricsRecorder uploadMetricsRecorder;

    public UUID uploadFile(String userLogin, MultipartFile file) {

        if (file.isEmpty()) {
            throw new BadRequestException("File is empty", FILE_IS_EMPTY);
        }

        SubscriptionStatus status = subscriptionService.getStatus(userLogin);
        DataSize maxAllowedSize = status == FREE ? uploadLimitsProperties.free() : uploadLimitsProperties.paid();

        if (file.getSize() > maxAllowedSize.toBytes()) {
            uploadMetricsRecorder.recordLimitRejected(status);
            throw new MaxUploadSizeExceededException(file.getSize());
        }

        return fileStorageService.upload(userLogin, file);
    }

}

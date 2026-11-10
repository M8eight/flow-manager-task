package com.task.flow_manager.application.service;

import com.task.flow_manager.application.enums.FileStatus;
import com.task.flow_manager.application.exception.BadRequestException;
import com.task.flow_manager.application.exception.ConflictException;
import com.task.flow_manager.application.exception.FileStorageException;
import com.task.flow_manager.infrastructure.persitence.entity.FileMetadata;
import com.task.flow_manager.infrastructure.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;

import static com.task.flow_manager.application.enums.ErrorCode.*;
import static com.task.flow_manager.infrastructure.kafka.ErrorCodeMapper.mapErrorExceptionByCode;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    private final StorageService storageService;
    private final FileMetadataService fileMetadataService;
    private final S3Presigner s3Presigner;

    @Value("${app.s3.bucket-name}")
    private String bucketName;

    @Value("${app.s3.folders.incoming}")
    private String incomingFolder;

    public UUID upload(MultipartFile file) {

        if (file.isEmpty()) {
            throw new BadRequestException("File is empty", FILE_IS_EMPTY);
        }

        UUID fileId = UUID.randomUUID();
        String objectKey = incomingFolder + fileId;
        try (InputStream inputStream = file.getInputStream()) {
            storageService.saveFile(objectKey, inputStream, file.getSize());
            fileMetadataService.createAndPublish(fileId, objectKey);
        } catch (IOException e) {
            storageService.deleteQuietly(fileId);
            throw new FileStorageException("Ошибка чтения/записи в S3", INTERNAL_ERROR, e);
        } catch (Exception e) {
            storageService.deleteQuietly(fileId);
            throw e;
        }

        return fileId;
    }

    public String generateDownloadUrl(UUID fileMetadataId) {
        FileMetadata fileMetadata = fileMetadataService.getById(fileMetadataId);
        FileStatus status = fileMetadata.getStatus();

        switch (status) {
            case PENDING -> throw new
                    ConflictException("File not ready to download", FILE_NOT_READY_TO_DOWNLOAD);
            case FAILED -> mapErrorExceptionByCode(fileMetadata.getErrorCode());
            case COMPLETED -> {}
        }

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileMetadata.getResultObjectKey())
                .build();

        GetObjectPresignRequest presignRequest =
                GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(15))
                        .getObjectRequest(getObjectRequest).build();

        return s3Presigner.presignGetObject(presignRequest)
                .url().toString();
    }
}

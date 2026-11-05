package com.task.flow_manager.infrastructure.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageService {

    private final S3Client s3Client;

    @Value("${app.s3.bucket-name}")
    private String bucketName;

    public void saveFile(UUID fileId, InputStream inputStream, long contentLength) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(uploadKey(fileId))
                .contentLength(contentLength)
                .build();

        s3Client.putObject(
                putObjectRequest, RequestBody.fromInputStream(inputStream, contentLength)
        );

        log.info("Файл {} загружен", fileId);
    }

    public void deleteQuietly(UUID fileId) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(uploadKey(fileId))
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    public static String uploadKey(UUID fileId) {
        return "uploads/" + fileId;
    }

}

package com.task.flow_manager.infrastructure.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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

    @Value("${app.s3.folders.incoming}")
    private String incomingFolder;

    public void saveFile(String objectKey, InputStream inputStream, long contentLength) {

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentLength(contentLength)
                .build();

        s3Client.putObject(
                putObjectRequest, RequestBody.fromInputStream(inputStream, contentLength)
        );

        log.info("Файл {} загружен", objectKey);
    }

    public void deleteQuietly(UUID fileId) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(incomingFolder + fileId)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

}

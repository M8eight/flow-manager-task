package com.task.flow_manager.application.service;

import com.task.flow_manager.infrastructure.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadService {

    private final StorageService storageService;
    private final FileMetadataService fileMetadataService;

    public void upload(MultipartFile file) {
        UUID fileId = UUID.randomUUID();
        try (InputStream inputStream = file.getInputStream()) {
            storageService.saveFile(fileId, inputStream, file.getSize());
        } catch (IOException e) {
            storageService.deleteQuietly(fileId);
            throw new RuntimeException("Ошибка чтения/записи в S3", e);
        }
        fileMetadataService.createAndPublish(fileId);
    }

}

package com.task.flow_manager.web.controller;

import com.task.flow_manager.application.dto.web.FileMetadataResponse;
import com.task.flow_manager.application.service.FileMetadataService;
import com.task.flow_manager.application.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/files")
public class FileUploadController {

    private final FileStorageService fileStorageService;
    private final FileMetadataService fileMetadataService;

    @GetMapping("/{fileMetadataId}")
    public FileMetadataResponse getById(@PathVariable UUID fileMetadataId) {
        return fileMetadataService.getInfoById(fileMetadataId);
    }

    @PostMapping(path = "/upload", consumes = MULTIPART_FORM_DATA_VALUE)
    public UUID uploadFile(@RequestParam("file") MultipartFile file) {
        return fileStorageService.upload(file);
    }

    @GetMapping("/presigned/{fileMetadataId}")
    public String generatePresignedUrl(@PathVariable UUID fileMetadataId) {
        return fileStorageService.generateDownloadUrl(fileMetadataId);
    }

}

package com.task.flow_manager.web.controller;

import com.task.flow_manager.application.dto.web.FileMetadataResponse;
import com.task.flow_manager.application.service.FileUploadFacade;
import com.task.flow_manager.application.service.FileMetadataService;
import com.task.flow_manager.application.service.FileStorageService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/files")
@Validated
public class FileUploadController {

    private final FileStorageService fileStorageService;
    private final FileMetadataService fileMetadataService;
    private final FileUploadFacade fileUploadFacade;

    @GetMapping("/{fileMetadataId}")
    public FileMetadataResponse getById(@NotBlank @RequestHeader(value = "X-User-Login") String userLogin,
                                        @PathVariable UUID fileMetadataId) {
        loginValid(userLogin);
        return fileMetadataService.getInfoById(userLogin, fileMetadataId);
    }

    @PostMapping(path = "/upload", consumes = MULTIPART_FORM_DATA_VALUE)
    public UUID uploadFile(@NotBlank @RequestHeader(value = "X-User-Login") String userLogin,
                           @RequestParam("file") MultipartFile file) {
        loginValid(userLogin);
        log.info("userLogin={}", userLogin);
        return fileUploadFacade.uploadFile(userLogin, file);
    }

    @GetMapping("/presigned/{fileMetadataId}")
    public String generatePresignedUrl(@NotBlank @RequestHeader(value = "X-User-Login") String userLogin,
                                       @PathVariable UUID fileMetadataId) {
        loginValid(userLogin);
        return fileStorageService.generateDownloadUrl(userLogin, fileMetadataId);
    }


    private void loginValid(String userLogin) {
        if (userLogin == null || userLogin.isBlank()) {
            throw new RuntimeException("Login header is missing");
        }
    }

}

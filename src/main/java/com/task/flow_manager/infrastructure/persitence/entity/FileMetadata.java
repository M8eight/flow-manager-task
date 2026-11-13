package com.task.flow_manager.infrastructure.persitence.entity;

import com.task.flow_manager.application.enums.ErrorCode;
import com.task.flow_manager.application.enums.FileStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "file_metadata")
@EntityListeners(AuditingEntityListener.class)
public class FileMetadata {

    @Id
    private UUID fileId;

    @Enumerated(EnumType.STRING)
    private FileStatus status;

    private String userLogin;

    private String resultBucket;

    private String resultObjectKey;

    @Enumerated(EnumType.STRING)
    private ErrorCode errorCode;

    private String errorMessage;

    private Instant processedAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

}

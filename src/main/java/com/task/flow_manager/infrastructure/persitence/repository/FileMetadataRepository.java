package com.task.flow_manager.infrastructure.persitence.repository;

import com.task.flow_manager.application.enums.FileStatus;
import com.task.flow_manager.infrastructure.persitence.entity.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, UUID> {

    @Modifying
    @Query("""
                update FileMetadata fm
                set fm.status = :status,
                    fm.resultBucket = :bucket,
                    fm.resultKey = :objectKey,
                    fm.errorCode = :errorCode,
                    fm.errorMessage = :errorMessage,
                    fm.processedAt = :processedAt
                where fm.fileId = :correlationId
                    and fm.status = 'PENDING'
            """)
    int setResult(@Param("correlationId") UUID correlationId,
                  @Param("status") FileStatus status,
                  @Param("bucket") String bucket,
                  @Param("objectKey") String objectKey,
                  @Param("errorCode") String errorCode,
                  @Param("errorMessage") String errorMessage,
                  @Param("processedAt") Instant processedAt);

    @Modifying
    @Query("""
                update FileMetadata fm
                set fm.status = :failed,
                    fm.errorCode = 'TIMEOUT',
                    fm.errorMessage = 'Conversation result expired',
                    fm.processedAt = :processedAt
                where fm.status = :pending
                    and fm.createdAt < :threshold
            """)
    int markExpired(@Param("pending") FileStatus pending,
                    @Param("failed") FileStatus failed,
                    @Param("threshold") Instant threshold,
                    @Param("processedAt") Instant processedAt);

}

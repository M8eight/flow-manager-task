package com.task.flow_manager.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.function.BiFunction;

@Service
@RequiredArgsConstructor
public class EventCleanupService {

    @Value("${app.cleanup.delete-batch-limit}")
    private int deleteBatchLimit;

    @Transactional
    public int cleanInBatch(Instant threshold, BiFunction<Instant, Integer, Integer> deleteFunction) {
        int totalDeleted = 0;
        int deleted = deleteFunction.apply(threshold, deleteBatchLimit);

        while (deleted > 0) {
            totalDeleted += deleted;
            deleted = deleteFunction.apply(threshold, deleteBatchLimit);
        }

        return totalDeleted;
    }

}

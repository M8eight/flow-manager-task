package com.task.flow_manager.service;

import com.task.flow_manager.application.service.EventCleanupService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventCleanupServiceTest {

    private final EventCleanupService service = new EventCleanupService();

    @Test
    @SuppressWarnings("unchecked")
    void cleanInBatch_sumsAllBatchesUntilZero() {
        ReflectionTestUtils.setField(service, "deleteBatchLimit", 100);
        Instant threshold = Instant.now();
        BiFunction<Instant, Integer, Integer> deleteFunction = mock(BiFunction.class);
        when(deleteFunction.apply(threshold, 100)).thenReturn(100, 30, 0);

        int result = service.cleanInBatch(threshold, deleteFunction);

        assertThat(result).isEqualTo(130);
        verify(deleteFunction, times(3)).apply(threshold, 100);
    }

}

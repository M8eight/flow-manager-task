package com.task.flow_manager.infrastructure.kafka.consumer;

import com.task.flow_manager.application.dto.FileConversionCompletedEvent;
import com.task.flow_manager.application.service.FileMetadataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ConversionCompletedConsumer {

    private final FileMetadataService fileMetadataService;

    @KafkaListener(topics = "${app.kafka.topics.output}", groupId = "pdf-service")
    public void onCompleted(FileConversionCompletedEvent event) {
        fileMetadataService.setCompleted(event);
        log.info("SET FILE STATUS SUCCESS");
    }

}

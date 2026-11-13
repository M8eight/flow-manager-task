package com.task.flow_manager.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;

@ConfigurationProperties(prefix = "app.upload.limits")
public record UploadLimitsProperties(
        DataSize free,
        DataSize paid
) {}

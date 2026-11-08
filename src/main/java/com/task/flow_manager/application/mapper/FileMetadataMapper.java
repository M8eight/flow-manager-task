package com.task.flow_manager.application.mapper;

import com.task.flow_manager.application.dto.web.FileMetadataResponse;
import com.task.flow_manager.infrastructure.persitence.entity.FileMetadata;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FileMetadataMapper {

    @Mapping(target = "status", source = "status")
    @Mapping(target = "errorCode", source = "errorCode")
    @Mapping(target = "errorMessage", source = "errorMessage")
    @Mapping(target = "processedAt", source = "processedAt")
    FileMetadataResponse toDto(FileMetadata fileMetadata);

}

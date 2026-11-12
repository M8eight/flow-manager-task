package com.task.flow_manager.application.mapper;

import com.task.flow_manager.application.dto.web.FileMetadataResponse;
import com.task.flow_manager.infrastructure.persitence.entity.FileMetadata;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FileMetadataMapper {

    FileMetadataResponse toDto(FileMetadata fileMetadata);

}

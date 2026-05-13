package com.cts.visit.mapper;

import com.cts.visit.dto.SourceDataResponseDto;
import com.cts.visit.entity.SourceData;

public class SourceDataMapper {

    public static SourceDataResponseDto toResponseDto(SourceData sourceData) {
        SourceDataResponseDto dto = new SourceDataResponseDto();
        dto.setSourceId(sourceData.getSourceId());
        dto.setVisitId(sourceData.getVisit().getVisitId());
        dto.setCollectedBy(sourceData.getCollectedBy());
        dto.setDataType(sourceData.getDataType());
        dto.setDataUri(sourceData.getDataUri());
        dto.setCollectedAt(sourceData.getCollectedAt());
        dto.setHash(sourceData.getHash());
        return dto;
    }
}

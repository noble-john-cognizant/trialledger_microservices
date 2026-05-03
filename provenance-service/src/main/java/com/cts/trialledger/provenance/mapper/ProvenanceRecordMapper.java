package com.cts.trialledger.provenance.mapper;


import com.cts.trialledger.provenance.dto.ProvenanceDTO;
import com.cts.trialledger.provenance.entity.ProvenanceRecord;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

public class ProvenanceRecordMapper {
    public static ProvenanceDTO convertEntityToDTO(ProvenanceRecord entity) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> json = mapper.readValue(entity.getMetadataJson(), new TypeReference<>() {
        });
        return new ProvenanceDTO(entity.getProvId(), entity.getEntityType(), entity.getEntityId(),
                entity.getAction(), entity.getPerformedBy(), entity.getPerformedAt(),
                json);
    }
}

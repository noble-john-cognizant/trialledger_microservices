package com.cts.trialledger.provenance.util;


import com.cts.trialledger.provenance.service.ProvenanceRecordService;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Component
public class ProvenanceRecordUtil {
    private final ObjectMapper mapper = new ObjectMapper();
    private final ProvenanceRecordService provenanceRecordService;

    public ProvenanceRecordUtil(ProvenanceRecordService provenanceRecordService) {
        this.provenanceRecordService = provenanceRecordService;
    }

    public void saveProvenanceRecord(String action, String entityType, Long entityId, Map<String, Object> metadata) {
        String metadataJson = mapper.writeValueAsString(metadata);
        provenanceRecordService.recordData(action, entityType, null, entityId, metadataJson);
    }
}

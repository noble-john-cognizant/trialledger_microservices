package com.cts.trialledger.provenance.service;


import com.cts.trialledger.provenance.dto.ProvenanceDTO;
import com.cts.trialledger.provenance.entity.ProvenanceRecord;
import com.cts.trialledger.provenance.mapper.ProvenanceRecordMapper;
import com.cts.trialledger.provenance.repository.ProvenanceRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProvenanceRecordService {

    private final ProvenanceRepository provenanceRepository;

    public ProvenanceRecordService(ProvenanceRepository provenanceRepository) {
        this.provenanceRepository = provenanceRepository;
    }

    public void recordData(String action, String entityType, Long performedBy, Long entityId, String metadata) {
        ProvenanceRecord record = new ProvenanceRecord();
        record.setAction(action);
        record.setEntityType(entityType);
        record.setPerformedBy(performedBy);
        record.setMetadataJson(metadata);
        record.setEntityId(entityId);
        provenanceRepository.save(record);
    }

    public Page<ProvenanceDTO> getAllRecords(int pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "performedAt"));

        List<ProvenanceDTO> data = provenanceRepository.findAll(pageRequest).getContent().stream().map(ProvenanceRecordMapper::convertEntityToDTO).toList();
        return new PageImpl<>(data,pageRequest,data.size());
    }
}

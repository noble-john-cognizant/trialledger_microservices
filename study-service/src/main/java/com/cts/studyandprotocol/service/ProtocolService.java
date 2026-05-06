package com.cts.studyandprotocol.service;

import com.cts.studyandprotocol.client.ProvenanceClient;
import com.cts.studyandprotocol.dto.ProtocolVersionRequestDto;
import com.cts.studyandprotocol.dto.ProtocolVersionResponseDto;
import com.cts.studyandprotocol.dto.ProvenanceRequestDTO;
import com.cts.studyandprotocol.entity.ProtocolVersion;
import com.cts.studyandprotocol.entity.Study;
import com.cts.studyandprotocol.exception.ProtocolNotFoundException;
import com.cts.studyandprotocol.exception.StudyNotFoundException;
import com.cts.studyandprotocol.mapper.ProtocolMapper;
import com.cts.studyandprotocol.model.ProtocolStatus;
import com.cts.studyandprotocol.repository.ProtocolVersionRepository;
import com.cts.studyandprotocol.repository.StudyRepository;
import com.cts.studyandprotocol.util.UserUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProtocolService {

    private final ProtocolVersionRepository protocolVersionRepository;
    private final StudyRepository studyRepository;
    private final ProtocolMapper protocolMapper;
    private final ProvenanceClient provenanceClient;

    public ProtocolService(ProtocolVersionRepository protocolVersionRepository,
                           StudyRepository studyRepository,
                           ProtocolMapper protocolMapper, ProvenanceClient provenanceClient) {
        this.protocolVersionRepository = protocolVersionRepository;
        this.studyRepository = studyRepository;
        this.protocolMapper = protocolMapper;
        this.provenanceClient = provenanceClient;
    }

    public ProtocolVersionResponseDto uploadProtocol(Long studyId, ProtocolVersionRequestDto dto) throws JsonProcessingException {
        Study study = studyRepository.findByStudyIdAndIsDeletedFalse(studyId)
                .orElseThrow(() -> new StudyNotFoundException(studyId));

        ProtocolVersion protocol = protocolMapper.toEntity(dto, study);
        ProtocolVersion saved = protocolVersionRepository.save(protocol);

        // Record in provenance table
        Map<String, Object> map = new HashMap<>();
        map.put("versionNumber", saved.getVersionNumber());
        map.put("documentUrl", saved.getDocumentUrl());
        map.put("effectiveDate", saved.getEffectiveDate());
        map.put("status", saved.getStatus().name());
        ObjectMapper mapper = new ObjectMapper();
        String metadata = mapper.writeValueAsString(map);
        ProvenanceRequestDTO requestDTO = new ProvenanceRequestDTO("UPLOAD_PROTOCOL", "protocol_version", UserUtil.getCurrentUserId(), saved.getProtocolId(), metadata);
        provenanceClient.recordProvenanceData(requestDTO);
        return protocolMapper.toResponse(saved);
    }

    public List<ProtocolVersionResponseDto> getProtocolByStudy(Long studyId) {
        if (!studyRepository.existsByStudyIdAndIsDeletedFalse(studyId)) {
            throw new StudyNotFoundException(studyId);
        }
        return protocolVersionRepository.findByStudy_StudyIdAndIsDeletedFalse(studyId)
                .stream()
                .map(protocolMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<ProtocolVersionResponseDto> getAllProtocols() {
        return protocolVersionRepository.findByIsDeletedFalse()
                .stream()
                .map(protocolMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ProtocolVersionResponseDto getProtocolById(Long protocolId) {
        ProtocolVersion protocol = protocolVersionRepository
                .findByProtocolIdAndIsDeletedFalse(protocolId)
                .orElseThrow(() -> new ProtocolNotFoundException(protocolId));
        return protocolMapper.toResponse(protocol);
    }

    public String updateProtocolStatus(Long protocolId, ProtocolStatus protocolStatus) throws JsonProcessingException {
        ProtocolVersion protocol = protocolVersionRepository
                .findByProtocolIdAndIsDeletedFalse(protocolId)
                .orElseThrow(() -> new ProtocolNotFoundException(protocolId));
        protocol.setStatus(protocolStatus);
        protocolVersionRepository.save(protocol);

        // Record in provenance table
        Map<String, Object> map = new HashMap<>();
        map.put("approvedBy", UserUtil.getCurrentUserId());
        map.put("status", protocol.getStatus());
        ObjectMapper mapper = new ObjectMapper();
        String metadata = mapper.writeValueAsString(map);
        ProvenanceRequestDTO dto = new ProvenanceRequestDTO("UPDATE_PROTOCOL", "protocol_version", UserUtil.getCurrentUserId(), protocol.getProtocolId(), metadata);
        provenanceClient.recordProvenanceData(dto);
        return "Protocol status updated successfully";
    }

    public String deleteProtocol(Long studyId, Long protocolId) throws JsonProcessingException {
        if (!studyRepository.existsByStudyIdAndIsDeletedFalse(studyId)) {
            throw new StudyNotFoundException(studyId);
        }
        ProtocolVersion protocol = protocolVersionRepository
                .findByProtocolIdAndIsDeletedFalse(protocolId)
                .orElseThrow(() -> new ProtocolNotFoundException(protocolId));
        protocol.setIsDeleted(true);
        protocolVersionRepository.save(protocol);

        // Record in provenance table
        Map<String, Object> map = new HashMap<>();
        map.put("versionNumber", protocol.getVersionNumber());
        map.put("documentUrl", protocol.getDocumentUrl());

        ObjectMapper mapper = new ObjectMapper();
        String metadata = mapper.writeValueAsString(map);
        ProvenanceRequestDTO requestDTO = new ProvenanceRequestDTO("DELETE_PROTOCOL", "protocol_version", UserUtil.getCurrentUserId(), protocol.getProtocolId(), metadata);
        provenanceClient.recordProvenanceData(requestDTO);
        return "Protocol with ID " + protocolId + " soft-deleted";
    }
}

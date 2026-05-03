package com.cts.studyandprotocol.service;

import com.cts.studyandprotocol.dto.ProtocolVersionRequestDto;
import com.cts.studyandprotocol.dto.ProtocolVersionResponseDto;
import com.cts.studyandprotocol.entity.ProtocolVersion;
import com.cts.studyandprotocol.entity.Study;
import com.cts.studyandprotocol.exception.ProtocolNotFoundException;
import com.cts.studyandprotocol.exception.StudyNotFoundException;
import com.cts.studyandprotocol.mapper.ProtocolMapper;
import com.cts.studyandprotocol.model.ProtocolStatus;
import com.cts.studyandprotocol.repository.ProtocolVersionRepository;
import com.cts.studyandprotocol.repository.StudyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProtocolService {

    private final ProtocolVersionRepository protocolVersionRepository;
    private final StudyRepository studyRepository;
    private final ProtocolMapper protocolMapper;

    public ProtocolService(ProtocolVersionRepository protocolVersionRepository,
                           StudyRepository studyRepository,
                           ProtocolMapper protocolMapper) {
        this.protocolVersionRepository = protocolVersionRepository;
        this.studyRepository = studyRepository;
        this.protocolMapper = protocolMapper;
    }

    public ProtocolVersionResponseDto uploadProtocol(Long studyId, ProtocolVersionRequestDto dto) {
        Study study = studyRepository.findByStudyIdAndIsDeletedFalse(studyId)
                .orElseThrow(() -> new StudyNotFoundException(studyId));

        ProtocolVersion protocol = protocolMapper.toEntity(dto, study);
        ProtocolVersion saved = protocolVersionRepository.save(protocol);
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

    public String updateProtocolStatus(Long protocolId, ProtocolStatus protocolStatus) {
        ProtocolVersion protocol = protocolVersionRepository
                .findByProtocolIdAndIsDeletedFalse(protocolId)
                .orElseThrow(() -> new ProtocolNotFoundException(protocolId));
        protocol.setStatus(protocolStatus);
        protocolVersionRepository.save(protocol);
        return "Protocol status updated successfully";
    }

    public String deleteProtocol(Long studyId, Long protocolId) {
        if (!studyRepository.existsByStudyIdAndIsDeletedFalse(studyId)) {
            throw new StudyNotFoundException(studyId);
        }
        ProtocolVersion protocol = protocolVersionRepository
                .findByProtocolIdAndIsDeletedFalse(protocolId)
                .orElseThrow(() -> new ProtocolNotFoundException(protocolId));
        protocol.setIsDeleted(true);
        protocolVersionRepository.save(protocol);
        return "Protocol with ID " + protocolId + " soft-deleted";
    }
}

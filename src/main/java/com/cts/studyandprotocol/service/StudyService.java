package com.cts.studyandprotocol.service;

import com.cts.studyandprotocol.client.ProvenanceClient;
import com.cts.studyandprotocol.dto.ProvenanceRequestDTO;
import com.cts.studyandprotocol.dto.StudyRequestDto;
import com.cts.studyandprotocol.dto.StudyResponseDto;
import com.cts.studyandprotocol.entity.ProtocolVersion;
import com.cts.studyandprotocol.entity.Study;
import com.cts.studyandprotocol.exception.StudyNotFoundException;
import com.cts.studyandprotocol.mapper.StudyMapper;
import com.cts.studyandprotocol.model.StudyStatus;
import com.cts.studyandprotocol.repository.ProtocolVersionRepository;
import com.cts.studyandprotocol.repository.StudyRepository;
import com.cts.studyandprotocol.util.UserUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StudyService {

    private final StudyRepository studyRepository;
    private final ProtocolVersionRepository protocolVersionRepository;
    private final StudyMapper studyMapper;
    private final ProvenanceClient provenanceClient;

    public StudyService(StudyRepository studyRepository,
                        ProtocolVersionRepository protocolVersionRepository,
                        StudyMapper studyMapper, ProvenanceClient provenanceClient) {
        this.studyRepository = studyRepository;
        this.protocolVersionRepository = protocolVersionRepository;
        this.studyMapper = studyMapper;
        this.provenanceClient = provenanceClient;
    }

    public List<StudyResponseDto> getAllStudies() {
        return studyRepository.findByIsDeletedFalse()
                .stream()
                .map(studyMapper::toResponse)
                .collect(Collectors.toList());
    }

    public StudyResponseDto getStudyById(Long studyId) {
        Study study = studyRepository.findByStudyIdAndIsDeletedFalse(studyId)
                .orElseThrow(() -> new StudyNotFoundException(studyId));
        return studyMapper.toResponse(study);
    }

    public StudyResponseDto createStudy(StudyRequestDto dto) throws JsonProcessingException {
        if (studyRepository.existsByProtocolNumberAndIsDeletedFalse(dto.getProtocolNumber())) {
            throw new IllegalArgumentException(
                    "Study with protocol number '" + dto.getProtocolNumber() + "' already exists");
        }
        if (studyRepository.existsByTitleAndIsDeletedFalse(dto.getTitle())) {
            throw new IllegalArgumentException(
                    "Study with title '" + dto.getTitle() + "' already exists");
        }
        Study study = studyMapper.toEntity(dto);
        Study saved = studyRepository.save(study);

            // Record in provenance table

        Map<String, Object> map = new HashMap<>();
        map.put("studyTitle", saved.getTitle());
        map.put("sponsor", saved.getSponsor());
        map.put("protocolNumber", saved.getProtocolNumber());
        map.put("status", saved.getStatus());
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String metadata = mapper.writeValueAsString(map);
        ProvenanceRequestDTO requestDTO = new ProvenanceRequestDTO("CREATE_STUDY", "study", UserUtil.getCurrentUserId(), saved.getStudyId(), metadata);
        provenanceClient.recordProvenanceData(requestDTO);
        return studyMapper.toResponse(saved);
    }

    public String updateStudyStatus(Long studyId, StudyStatus status) throws JsonProcessingException {
        Study study = studyRepository.findByStudyIdAndIsDeletedFalse(studyId)
                .orElseThrow(() -> new StudyNotFoundException(studyId));

        study.setStatus(status);
        studyRepository.save(study);

        // Provenance recording
        Map<String, Object> map = new HashMap<>();
        map.put("studyId", studyId);
        map.put("newStatus", status);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String metadata = mapper.writeValueAsString(map);
        ProvenanceRequestDTO dto = new ProvenanceRequestDTO(
                "UPDATE_STUDY_STATUS", "study",
                UserUtil.getCurrentUserId(), studyId, metadata);
        provenanceClient.recordProvenanceData(dto);

        return "Study status updated to " + status;
    }

    @Transactional
    public String deleteStudy(Long studyId) throws JsonProcessingException {
        Study study = studyRepository.findByStudyIdAndIsDeletedFalse(studyId)
                .orElseThrow(() -> new StudyNotFoundException(studyId));

        List<ProtocolVersion> protocols =
                protocolVersionRepository.findByStudy_StudyIdAndIsDeletedFalse(studyId);
        protocols.forEach(p -> p.setIsDeleted(true));
        protocolVersionRepository.saveAll(protocols);

        study.setIsDeleted(true);
        studyRepository.save(study);

        // Record in provenance table
        Map<String, Object> map = new HashMap<>();
        map.put("studyTitle", study.getTitle());
        map.put("softDeleted", true);
        map.put("status", study.getStatus());
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String metadata = mapper.writeValueAsString(map);
        ProvenanceRequestDTO requestDTO = new ProvenanceRequestDTO("CREATE_STUDY", "study", UserUtil.getCurrentUserId(), study.getStudyId(), metadata);
        provenanceClient.recordProvenanceData(requestDTO);

        return "Study with ID " + studyId + " deleted along with " + protocols.size() + " protocol(s)";
    }
}
package com.cts.studyandprotocol.service;

import com.cts.studyandprotocol.dto.StudyRequestDto;
import com.cts.studyandprotocol.dto.StudyResponseDto;
import com.cts.studyandprotocol.entity.ProtocolVersion;
import com.cts.studyandprotocol.entity.Study;
import com.cts.studyandprotocol.exception.StudyNotFoundException;
import com.cts.studyandprotocol.mapper.StudyMapper;
import com.cts.studyandprotocol.repository.ProtocolVersionRepository;
import com.cts.studyandprotocol.repository.StudyRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudyService {

    private final StudyRepository studyRepository;
    private final ProtocolVersionRepository protocolVersionRepository;
    private final StudyMapper studyMapper;

    public StudyService(StudyRepository studyRepository,
                        ProtocolVersionRepository protocolVersionRepository,
                        StudyMapper studyMapper) {
        this.studyRepository = studyRepository;
        this.protocolVersionRepository = protocolVersionRepository;
        this.studyMapper = studyMapper;
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

    public StudyResponseDto createStudy(StudyRequestDto dto) {
        if (studyRepository.existsByProtocolNumberAndIsDeletedFalse(dto.getProtocolNumber())) {
            throw new IllegalArgumentException("Protocol number already exists: " + dto.getProtocolNumber());
        }
        if (studyRepository.existsByTitleAndIsDeletedFalse(dto.getTitle())) {
            throw new IllegalArgumentException("Title already exists: " + dto.getTitle());
        }
        Study study = studyMapper.toEntity(dto);
        Study saved = studyRepository.save(study);
        return studyMapper.toResponse(saved);
    }


    @Transactional
    public String deleteStudy(Long studyId) {
        Study study = studyRepository.findByStudyIdAndIsDeletedFalse(studyId)
                .orElseThrow(() -> new StudyNotFoundException(studyId));

        // Cascade soft delete on linked protocols
        List<ProtocolVersion> protocols =
                protocolVersionRepository.findByStudy_StudyIdAndIsDeletedFalse(studyId);
        protocols.forEach(p -> p.setIsDeleted(true));
        protocolVersionRepository.saveAll(protocols);

        study.setIsDeleted(true);
        studyRepository.save(study);

        return "Study with ID " + studyId + " soft-deleted along with " + protocols.size() + " protocol(s)";
    }
}
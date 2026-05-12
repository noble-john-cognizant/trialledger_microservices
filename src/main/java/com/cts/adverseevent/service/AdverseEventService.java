package com.cts.adverseevent.service;

import com.cts.adverseevent.client.ParticipantClient;
import com.cts.adverseevent.client.StudyClient;
import com.cts.adverseevent.dto.*;
import com.cts.adverseevent.entity.AEFollowUp;
import com.cts.adverseevent.entity.AdverseEvent;
import com.cts.adverseevent.exception.AdverseEventNotFoundException;
import com.cts.adverseevent.mapper.AEFollowUpMapper;
import com.cts.adverseevent.mapper.AdverseEventMapper;
import com.cts.adverseevent.model.AEStatus;
import com.cts.adverseevent.repository.AEFollowUpRepository;
import com.cts.adverseevent.repository.AdverseEventRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdverseEventService {

    private final AdverseEventRepository adverseEventRepository;
    private final AEFollowUpRepository aeFollowUpRepository;
    private final AdverseEventMapper aeMapper;
    private final AEFollowUpMapper followUpMapper;

    private final StudyClient studyClient;
    private final ParticipantClient participantClient;

    public AdverseEventService(AdverseEventRepository adverseEventRepository,
                               AEFollowUpRepository aeFollowUpRepository,
                               AdverseEventMapper aeMapper,
                               AEFollowUpMapper followUpMapper,
                               StudyClient studyClient,
                               ParticipantClient participantClient) {
        this.adverseEventRepository = adverseEventRepository;
        this.aeFollowUpRepository = aeFollowUpRepository;
        this.aeMapper = aeMapper;
        this.followUpMapper = followUpMapper;
        this.studyClient = studyClient;
        this.participantClient = participantClient;
    }

    public List<AdverseEventResponseDto> getAllAE() {
        return adverseEventRepository.findByIsDeletedFalse()
                .stream()
                .map(aeMapper::toResponse)
                .collect(Collectors.toList());
    }

    public AdverseEventResponseDto getAEById(Long aeId) {
        AdverseEvent ae = adverseEventRepository.findByIdAndIsDeletedFalse(aeId)
                .orElseThrow(() -> new AdverseEventNotFoundException(aeId));
        return aeMapper.toResponse(ae);
    }

    public List<AdverseEventResponseDto> getAEByStudy(Long studyId) {
        return adverseEventRepository.findByStudyIdAndIsDeletedFalse(studyId)
                .stream()
                .map(aeMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<AdverseEventResponseDto> getAEByParticipant(Long participantId) {
        return adverseEventRepository.findByParticipantIdAndIsDeletedFalse(participantId)
                .stream()
                .map(aeMapper::toResponse)
                .collect(Collectors.toList());
    }

    public AdverseEventResponseDto createAE(AdverseEventRequestDto dto) {
        AdverseEvent ae = aeMapper.toEntity(dto);
        AdverseEvent saved = adverseEventRepository.save(ae);
        return aeMapper.toResponse(saved);
    }

    public AdverseEventResponseDto updateStatus(Long aeId, String status) {
        AdverseEvent ae = adverseEventRepository.findByIdAndIsDeletedFalse(aeId)
                .orElseThrow(() -> new AdverseEventNotFoundException(aeId));
        ae.setStatus(AEStatus.valueOf(status.toUpperCase()));
        AdverseEvent saved = adverseEventRepository.save(ae);
        return aeMapper.toResponse(saved);
    }

    @Transactional
    public String deleteAE(Long aeId) {
        AdverseEvent ae = adverseEventRepository.findByIdAndIsDeletedFalse(aeId)
                .orElseThrow(() -> new AdverseEventNotFoundException(aeId));

        List<AEFollowUp> followUps =
                aeFollowUpRepository.findByAdverseEvent_IdAndIsDeletedFalse(aeId);
        followUps.forEach(f -> f.setIsDeleted(true));
        aeFollowUpRepository.saveAll(followUps);

        ae.setIsDeleted(true);
        adverseEventRepository.save(ae);

        return "Adverse Event with ID " + aeId + " soft-deleted along with "
                + followUps.size() + " follow-up(s)";
    }


    public ApiResponseDto getFullAdverseEvent(Long aeId) {
        AdverseEvent ae = adverseEventRepository.findByIdAndIsDeletedFalse(aeId)
                .orElseThrow(() -> new AdverseEventNotFoundException(aeId));

        AdverseEventResponseDto aeDto = aeMapper.toResponse(ae);

        List<AEFollowUpResponseDto> followUps =
                aeFollowUpRepository.findByAdverseEvent_IdAndIsDeletedFalse(aeId)
                        .stream()
                        .map(followUpMapper::toResponse)
                        .collect(Collectors.toList());

        StudyDto study = studyClient.getStudyById(ae.getStudyId());
        ParticipantDto participant = participantClient.getParticipantById(ae.getParticipantId());

        return new ApiResponseDto(aeDto, followUps, study, participant);
    }
}

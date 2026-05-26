package com.cts.trialledger.service;

import com.cts.trialledger.client.NotificationClient;
import com.cts.trialledger.client.ProvenanceClient;
import com.cts.trialledger.dto.ChainOfCustodyRequestDTO;
import com.cts.trialledger.dto.ChainOfCustodyResponseDTO;
import com.cts.trialledger.dto.NotificationRequestDTO;
import com.cts.trialledger.dto.ProvenanceRequestDTO;
import com.cts.trialledger.entity.ChainOfCustody;
import com.cts.trialledger.entity.Sample;
import com.cts.trialledger.exception.ResourceNotFoundException;
import com.cts.trialledger.exception.SampleNotFoundException;
import com.cts.trialledger.mapper.ChainOfCustodyMapper;
import com.cts.trialledger.repository.ChainOfCustodyRepository;
import com.cts.trialledger.repository.SampleRepository;
import com.cts.trialledger.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChainOfCustodyServiceImpl implements ChainOfCustodyService {

    private final ChainOfCustodyRepository chainOfCustodyRepository;
    private final SampleRepository sampleRepository;
    private final ProvenanceClient provenanceClient;
    private final ChainOfCustodyMapper chainOfCustodyMapper;
    private final NotificationClient notificationClient;

    @Override
    public ChainOfCustodyResponseDTO transferCustody(Long sampleId, ChainOfCustodyRequestDTO requestDTO) {

        Sample sample = sampleRepository.findById(sampleId)
                .orElseThrow(() -> new SampleNotFoundException(sampleId));

        if (requestDTO.getFromUser().trim().equalsIgnoreCase(requestDTO.getToUser().trim())) {
            throw new IllegalArgumentException(
                    "From user and To user must be different. Cannot transfer custody to the same person.");
        }

        ChainOfCustody coc = ChainOfCustody.builder()
                .sample(sample)
                .fromUser(requestDTO.getFromUser())
                .toUser(requestDTO.getToUser())
                .transferAt(LocalDateTime.now())
                .fromLocation(requestDTO.getFromLocation())
                .toLocation(requestDTO.getToLocation())
                .notes(requestDTO.getNotes())
                .build();

        ChainOfCustody saved = chainOfCustodyRepository.save(coc);

        Map<String, Object> map = Map.of(
                "fromUser", saved.getFromUser(),
                "toUser", saved.getToUser(),
                "fromLocation", saved.getFromLocation(),
                "toLocation", saved.getToLocation(),
                "sampleId", sampleId
        );

        ProvenanceRequestDTO dto = new ProvenanceRequestDTO(

                "TRANSFER_CUSTODY",
                "chain_of_custody", UserUtil.getCurrentUserId(),
                saved.getCocId(),
                new ObjectMapper().writeValueAsString(map)
        );
        provenanceClient.recordProvenanceData(dto);

        try {
            NotificationRequestDTO notifDto = NotificationRequestDTO.builder()
                    .userId(UserUtil.getCurrentUserId())
                    .entityId(sampleId)
                    .message("Sample chain-of-custody transfer: Sample ID " + sampleId
                            + " transferred from User " + saved.getFromUser()
                            + " (" + saved.getFromLocation() + ")"
                            + " → User " + saved.getToUser()
                            + " (" + saved.getToLocation() + ")")
                    .category("SAMPLE")
                    .build();

            notificationClient.createNotification(notifDto);
            log.info("Notification sent for custody transfer, Sample ID: {}", sampleId);
        } catch (Exception e) {
            log.error("Failed to send notification for custody transfer: {}", e.getMessage());
        }

        return chainOfCustodyMapper.toResponseDTO(saved);
    }

    @Override
    public ChainOfCustodyResponseDTO getCustodyById(Long cocId) {

        ChainOfCustody coc = chainOfCustodyRepository.findById(cocId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Chain of Custody not found for id " + cocId
                ));


        return chainOfCustodyMapper.toResponseDTO(coc);
    }

    @Override
    public List<ChainOfCustodyResponseDTO> getCustodyBySampleId(Long sampleId) {

        sampleRepository.findById(sampleId)
                .orElseThrow(() -> new SampleNotFoundException(sampleId));

        List<ChainOfCustodyResponseDTO> list = chainOfCustodyRepository
                .findBySample_SampleId(sampleId)
                .stream()
                .map(chainOfCustodyMapper::toResponseDTO)
                .toList();

        return list;
    }

    @Override
    public ChainOfCustodyResponseDTO getLatestCustody(Long sampleId) {

        sampleRepository.findById(sampleId)
                .orElseThrow(() -> new SampleNotFoundException(sampleId));

        ChainOfCustody latest = chainOfCustodyRepository
                .findTopBySample_SampleIdOrderByTransferAtDescCocIdDesc(sampleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No custody history for sample ID : " + sampleId
                ));

        return chainOfCustodyMapper.toResponseDTO(latest);
    }
}
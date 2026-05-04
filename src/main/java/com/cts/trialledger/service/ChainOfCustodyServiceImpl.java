package com.cts.trialledger.service;

import com.cts.trialledger.dto.ChainOfCustodyRequestDTO;
import com.cts.trialledger.dto.ChainOfCustodyResponseDTO;
import com.cts.trialledger.entity.ChainOfCustody;
import com.cts.trialledger.entity.Sample;
import com.cts.trialledger.exception.ResourceNotFoundException;
import com.cts.trialledger.exception.SampleNotFoundException;
import com.cts.trialledger.mapper.ChainOfCustodyMapper;
import com.cts.trialledger.repository.ChainOfCustodyRepository;
import com.cts.trialledger.repository.SampleRepository;
//import com.cts.trialledger.service.AuditService;
//import com.cts.trialledger.util.AuthValidator;
//import com.cts.trialledger.util.ProvenanceRecordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChainOfCustodyServiceImpl implements ChainOfCustodyService {

    private final ChainOfCustodyRepository chainOfCustodyRepository;
    private final SampleRepository sampleRepository;
//    private final AuditService auditService;
//    private final ProvenanceRecordUtil provenanceRecordUtil;
    private final ChainOfCustodyMapper chainOfCustodyMapper;

    @Override
    public ChainOfCustodyResponseDTO transferCustody(Long sampleId, ChainOfCustodyRequestDTO requestDTO) {

        Sample sample = sampleRepository.findById(sampleId)
                .orElseThrow(() -> new SampleNotFoundException(sampleId));

        ChainOfCustody coc = ChainOfCustody.builder()
                .sample(sample)
                .fromUser(requestDTO.getFromUser())
                .toUser(requestDTO.getToUser())
                .transferAt(requestDTO.getTransferAt())
                .fromLocation(requestDTO.getFromLocation())
                .toLocation(requestDTO.getToLocation())
                .notes(requestDTO.getNotes())
                .build();

        ChainOfCustody saved = chainOfCustodyRepository.save(coc);

//        auditService.storeAudit(
//                "TRANSFER_CUSTODY",
//                "chain_of_custody",
//                "User ID: " + AuthValidator.getCurrentUserId()
//                        + " transferred sample of id: " + sampleId
//        );

        Map<String, Object> map = Map.of(
                "fromUser", saved.getFromUser(),
                "toUser", saved.getToUser(),
                "fromLocation", saved.getFromLocation(),
                "toLocation", saved.getToLocation(),
                "sampleId", sampleId
        );

//        provenanceRecordUtil.saveProvenanceRecord(
//                "TRANSFER_CUSTODY",
//                "chain_of_custody",
//                saved.getCocId(),
//                map
//        );

        return chainOfCustodyMapper.toResponseDTO(saved);
    }

    @Override
    public ChainOfCustodyResponseDTO getCustodyById(Long cocId) {

        ChainOfCustody coc = chainOfCustodyRepository.findById(cocId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Chain of Custody not found for id " + cocId
                ));

//        auditService.storeAudit(
//                "VIEW_CUSTODY",
//                "chain_of_custody",
//                "User ID: " + AuthValidator.getCurrentUserId()
//                        + " viewed custody by id: " + cocId
//        );

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

//        auditService.storeAudit(
//                "VIEW_CUSTODY",
//                "chain_of_custody",
//                "User ID: " + AuthValidator.getCurrentUserId()
//                        + " viewed custody by sample id: " + sampleId
//        );

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

//        auditService.storeAudit(
//                "VIEW_CUSTODY",
//                "chain_of_custody",
//                "User ID: " + AuthValidator.getCurrentUserId()
//                        + " viewed latest custody"
//        );

        return chainOfCustodyMapper.toResponseDTO(latest);
    }
}
package com.cts.trialledger.service;

import com.cts.trialledger.client.AdverseEventClient;
import com.cts.trialledger.client.ConsentClient;
import com.cts.trialledger.client.ProvenanceClient;
import com.cts.trialledger.client.SampleClient;
import com.cts.trialledger.dto.AdverseEventResponseDTO;
import com.cts.trialledger.dto.ConsentResponseDTO;
import com.cts.trialledger.dto.ProvenanceDTO;
import com.cts.trialledger.dto.SampleResponseDTO;
import com.cts.trialledger.dto.ReportRequestDTO;
import com.cts.trialledger.dto.ReportResponseDTO;
import com.cts.trialledger.entity.Report;
import com.cts.trialledger.exception.ReportNotFoundException;
import com.cts.trialledger.exception.ResourceNotFoundException;
import com.cts.trialledger.mapper.ReportMapper;
import com.cts.trialledger.model.ReportScope;
import com.cts.trialledger.repository.ReportRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final ConsentClient consentClient;
    private final SampleClient sampleClient;
    private final AdverseEventClient adverseEventClient;
    private final ProvenanceClient provenanceClient;
    private final ReportMapper reportMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ReportResponseDTO generateReport(ReportRequestDTO dto) {

        String metricsJson = switch (dto.getScope()) {

            case ENROLLMENT -> buildEnrollmentMetrics(dto.getStudyId());
            case SAMPLES -> buildSampleMetrics(dto.getStudyId());
            case AE -> buildAdverseEventMetrics(dto.getStudyId());
            case PROVENANCE -> buildProvenanceMetrics(dto.getStudyId());

            default -> throw new IllegalArgumentException("Unsupported report scope: " + dto.getScope());
        };

        Report report = Report.builder()
                .studyId(dto.getStudyId())
                .scope(dto.getScope())
                .parametersJson(dto.getParametersJson())
                .metricsJson(metricsJson)
                .generatedAt(LocalDateTime.now())
                .reportUri("/reports/" + System.currentTimeMillis() + ".pdf")
                .build();

        return reportMapper.toResponse(reportRepository.save(report));
    }

    private String buildEnrollmentMetrics(Long studyId) {
        ConsentResponseDTO stats = consentClient.getEnrollmentStats(studyId);
        Map<String, Object> metrics = Map.of(
                "participants", Map.of(
                        "total", stats.getTotalParticipants(),
                        "enrolled", stats.getEnrolledCount(),
                        "withdrawn", stats.getWithdrawnCount()
                ));
        return toJson(metrics);
    }

    private String buildSampleMetrics(Long studyId) {
        SampleResponseDTO stats = sampleClient.getSampleStats(studyId);
        Map<String, Object> metrics = Map.of(
                "samples", Map.of(
                        "total", stats.getTotalSamples(),
                        "byStatus", Map.of(
                                "COLLECTED", stats.getCollectedCount(),
                                "IN_ANALYSIS", stats.getInAnalysisCount(),
                                "COMPLETED", stats.getCompletedCount()
                        )
                )
        );
        return toJson(metrics);
    }

    private String buildAdverseEventMetrics(Long studyId) {
        AdverseEventResponseDTO stats = adverseEventClient.getAdverseEventStats(studyId);
        Map<String, Object> metrics = Map.of(
                "adverseEvents", Map.of(
                        "total", stats.getTotalEvents(),
                        "bySeverity", Map.of(
                                "MILD", stats.getMildCount(),
                                "MODERATE", stats.getModerateCount(),
                                "SEVERE", stats.getSevereCount(),
                                "LIFE_THREATENING", stats.getLifeThreatenigCount()
                        )
                )
        );
        return toJson(metrics);
    }

    private String buildProvenanceMetrics(Long studyId) {
        SampleResponseDTO sampleStats = sampleClient.getSampleStats(studyId);
        ProvenanceDTO provenanceStats = provenanceClient.getProvenanceStats(studyId);
        Map<String, Object> metrics = Map.of(
                "provenance", Map.of(
                        "custodyEvents", sampleStats.getCustodyEventCount(),
                        "assayRuns", sampleStats.getAssayRunCount(),
                        "provenanceRecords", provenanceStats.getProvenanceRecordCount()
                ));
        return toJson(metrics);
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize metrics JSON", e);
        }
    }

    @Override
    public ReportResponseDTO getReportById(Long reportId) {
        return reportMapper.toResponse(
                reportRepository.findById(reportId)
                        .orElseThrow(() -> new ReportNotFoundException(reportId)));
    }

    @Override
    public List<ReportResponseDTO> getReportsByStudy(Long studyId) {
        return reportRepository.findByStudyId(studyId).stream()
                .map(reportMapper::toResponse)
                .toList();
    }

    @Override
    public List<ReportResponseDTO> getReportsByScope(ReportScope scope) {
        List<ReportResponseDTO> reports = reportRepository.findByScope(scope).stream()
                .map(reportMapper::toResponse).toList();

        if (reports.isEmpty()) {
            throw new ResourceNotFoundException("No reports found for scope: " + scope);
        }
        return reports;
    }

    @Override
    public List<ReportResponseDTO> getAllReports() {
        return reportRepository.findAll()
                .stream()
                .map(reportMapper::toResponse)
                .toList();
    }
}
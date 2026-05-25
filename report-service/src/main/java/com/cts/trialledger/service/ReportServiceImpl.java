package com.cts.trialledger.service;

import com.cts.trialledger.client.AdverseEventClient;
import com.cts.trialledger.client.ConsentClient;
import com.cts.trialledger.client.ProvenanceClient;
import com.cts.trialledger.client.SampleClient;
import com.cts.trialledger.client.dto.AdverseEventStatsDTO;
import com.cts.trialledger.client.dto.EnrollmentStatsDTO;
import com.cts.trialledger.client.dto.ProvenanceStatsDTO;
import com.cts.trialledger.client.dto.SampleStatsDTO;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final ConsentClient consentClient;
    private final SampleClient sampleClient;
    private final AdverseEventClient adverseEventClient;
    private final ProvenanceClient provenanceClient;
    private final ReportMapper reportMapper;

    @Value("${report.storage-path:./reports}")
    private String storagePath;

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
                .reportUri(null)
                .build();

        Report saved = reportRepository.save(report);

        try {
            Path dir = Paths.get(storagePath);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }

            Map<String, Object> reportContent = new LinkedHashMap<>();
            reportContent.put("reportId", saved.getReportId());
            reportContent.put("studyId", saved.getStudyId());
            reportContent.put("scope", saved.getScope().name());
            reportContent.put("reportingPeriod", dto.getReportingPeriod());
            reportContent.put("parametersJson",  dto.getParametersJson());
            reportContent.put("generatedAt", saved.getGeneratedAt().toString());
            reportContent.put("metrics", objectMapper.readValue(metricsJson, Object.class));

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            String fileName = "report-" + saved.getReportId()
                    + "-study-" + saved.getStudyId()
                    + "-" + saved.getScope().name().toLowerCase()
                    + "-" + timestamp + ".json";

            Path filePath = dir.resolve(fileName);
            Files.writeString(filePath,
                    objectMapper.writerWithDefaultPrettyPrinter()
                            .writeValueAsString(reportContent));

            saved.setReportUri(filePath.toString());
            saved = reportRepository.save(saved);

            log.info("[ReportService] Report file saved | reportId={} path={}",
                    saved.getReportId(), filePath);

        } catch (Exception e) {
            log.error("[ReportService] Failed to save report file | reportId={} error={}",
                    saved.getReportId(), e.getMessage());
            saved.setReportUri("/reports/" + System.currentTimeMillis() + ".json");
            saved = reportRepository.save(saved);
        }

        return reportMapper.toResponse(saved);
    }

    private String buildEnrollmentMetrics(Long studyId) {
        EnrollmentStatsDTO stats = consentClient.getEnrollmentStats(studyId);
        Map<String, Object> metrics = Map.of(
                "participants", Map.of(
                        "total", stats.getTotalParticipants(),
                        "enrolled", stats.getEnrolledCount(),
                        "withdrawn", stats.getWithdrawnCount()
                ));
        return toJson(metrics);
    }

    private String buildSampleMetrics(Long studyId) {
        SampleStatsDTO stats = sampleClient.getSampleStats(studyId);
        Map<String, Object> metrics = Map.of(
                "samples", Map.of(
                        "total", stats.getTotalSamples(),
                        "byStatus", Map.of(
                                "COLLECTED", stats.getCollectedCount(),
                                "IN_ANALYSIS", stats.getInAnalysisCount(),
                                "COMPLETED", stats.getCompletedCount()
                        ),
                        "custodyEvents", stats.getCustodyEventCount(),
                        "assayRuns", stats.getAssayRunCount()
                )
        );
        return toJson(metrics);
    }

    private String buildAdverseEventMetrics(Long studyId) {
        AdverseEventStatsDTO stats = adverseEventClient.getAdverseEventStats(studyId);
        Map<String, Object> metrics = Map.of(
                "adverseEvents", Map.of(
                        "total", stats.getTotalEvents(),
                        "bySeverity", Map.of(
                                "MILD", stats.getMildCount(),
                                "MODERATE", stats.getModerateCount(),
                                "SEVERE", stats.getSevereCount()
                        )
                )
        );
        return toJson(metrics);
    }

    private String buildProvenanceMetrics(Long studyId) {
        SampleStatsDTO sampleStats = sampleClient.getSampleStats(studyId);
        ProvenanceStatsDTO provenanceStats = provenanceClient.getProvenanceStats(studyId);
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
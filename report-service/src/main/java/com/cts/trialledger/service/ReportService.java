package com.cts.trialledger.service;
import com.cts.trialledger.dto.ReportRequestDTO;
import com.cts.trialledger.dto.ReportResponseDTO;
import com.cts.trialledger.model.ReportScope;

import java.util.List;

public interface ReportService {

        ReportResponseDTO generateReport(ReportRequestDTO dto);

        ReportResponseDTO getReportById(Long reportId);

        List<ReportResponseDTO> getReportsByStudy(Long studyId);

        List<ReportResponseDTO> getReportsByScope(ReportScope scope);

        List<ReportResponseDTO> getAllReports();
}
package com.cts.trialledger.client;

import com.cts.trialledger.dto.KPIResponseDTO;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class KPIClientFallback implements KPIClient {
    @Override
    public void refreshKPIValue(Long kpiId, Long studyId) {
        // No-op
    }

    @Override
    public List<KPIResponseDTO> getKPIsByPeriod(String period) {
        return Collections.emptyList();
    }

    @Override
    public void refreshAllKPIsForStudy(Long studyId) {
        // No-op
    }
}

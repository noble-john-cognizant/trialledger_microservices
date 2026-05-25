package com.cts.trialledger.provenance.client;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class SampleClientFallback implements SampleClient {
    @Override
    public List<Map<String, Object>> getSamplesByStudy(Long studyId) {
        return Collections.emptyList();
    }
}

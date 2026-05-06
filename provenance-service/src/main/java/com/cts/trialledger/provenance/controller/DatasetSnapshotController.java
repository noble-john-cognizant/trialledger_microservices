package com.cts.trialledger.provenance.controller;


import com.cts.trialledger.provenance.entity.DatasetSnapshot;
import com.cts.trialledger.provenance.service.DatasetSnapshotService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dataset-snapshot")
public class DatasetSnapshotController {

    private final DatasetSnapshotService datasetSnapshotService;

    public DatasetSnapshotController(DatasetSnapshotService datasetSnapshotService) {
        this.datasetSnapshotService = datasetSnapshotService;
    }

    @PreAuthorize("hasAnyRole('DATA_MANAGER','PI','ADMIN')")
    @PostMapping
    public DatasetSnapshot createDatasetSnapshot(@RequestParam Long studyId) throws Exception {
        return datasetSnapshotService.createStudySnapshot(studyId);
    }
    @PreAuthorize("hasAnyRole('COMPLIANCE','ADMIN','PI', 'DATA_MANAGER', 'AUDITOR')")
    @GetMapping
    public List<DatasetSnapshot> getDatasetSnapshot(@RequestParam Long studyId) {
        return datasetSnapshotService.findAllSnapshotByStudyId(studyId);
    }
}

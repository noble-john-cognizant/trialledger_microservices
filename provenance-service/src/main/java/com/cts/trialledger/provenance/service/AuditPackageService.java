package com.cts.trialledger.provenance.service;


import com.cts.trialledger.provenance.entity.AuditPackage;
import com.cts.trialledger.provenance.entity.DatasetSnapshot;
import com.cts.trialledger.provenance.entity.ProvenanceRecord;
import com.cts.trialledger.provenance.repository.AuditPackageRepo;
import com.cts.trialledger.provenance.repository.DatasetSnapshotRepo;
import com.cts.trialledger.provenance.repository.ProvenanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class AuditPackageService {

    private final AuditPackageRepo auditPackageRepo;
    private final ProvenanceRepository provenanceRepository;
    private final DatasetSnapshotRepo datasetSnapshotRepo;
    @Value("${EXTERNAL_STORAGE_PATH}")
    private String LOCATION;

    public AuditPackage generatePackage(Long studyId, LocalDate start, LocalDate end) throws Exception {

        // 1. Fetch relevant logs for the period (consent records, provenance records, sample manifests, assay metadata)
        List<Map<String, Object>> snapshotVerification = datasetSnapshotRepo.findByStudyIdAndSnapshotDateGreaterThanEqualAndSnapshotDateLessThan(studyId, start.atStartOfDay(), end.atStartOfDay()).stream()
                .map(s -> {
                    Map<String, Object> entry = new LinkedHashMap<>();
                    entry.put("snapshotId", s.getSnapshotId());
                    entry.put("snapshotDate", s.getSnapshotDate().toString());
                    entry.put("snapshotUri", s.getSnapshotUri());
                    entry.put("hash", s.getHash()); // regulator can re-verify this
                    return entry;
                })
                .toList();
        List<ProvenanceRecord> provenanceList = provenanceRepository.findByPerformedAtBetween(start.atStartOfDay(), end.atTime(LocalTime.MAX));
        ObjectMapper mapper = new ObjectMapper();
        String snapshotJson = mapper.writeValueAsString(snapshotVerification);
        String provenanceJson = mapper.writeValueAsString(provenanceList);

        // 2. Create the ZIP File
        String zipFileName = "Audit_Study_" + studyId + "_" + System.currentTimeMillis() + ".zip";
        Path zipPath = Paths.get(LOCATION+"/storage/audit_exports/" + zipFileName);
        Files.createDirectories(zipPath.getParent());

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath.toFile()))) {
            // Add provenance logs
            ZipEntry entry = new ZipEntry("provenance_logs.json");
            zos.putNextEntry(entry);
            zos.write(provenanceJson.getBytes());
            zos.closeEntry();

            // Add dataset snapshot
            ZipEntry snapshotFile = new ZipEntry("study_snapshot.json");
            zos.putNextEntry(snapshotFile);
            zos.write(snapshotJson.getBytes());
            zos.closeEntry();
        }

        // 3. Save the record to the database
        AuditPackage pkg = new AuditPackage();
        pkg.setStudyId(studyId);
        pkg.setPeriodStart(start);
        pkg.setPeriodEnd(end);
        pkg.setPackageUri(zipPath.toAbsolutePath().toString());
        pkg.setGeneratedAt(LocalDateTime.now());

        List<String> files = List.of("provenance_logs.json", "study_snapshot.json");
        pkg.setContentsJSON(mapper.writeValueAsString(Map.of("files", files))); //file name saved

        return auditPackageRepo.save(pkg);
    }

    public List<AuditPackage> getAllAuditPackages(Long studyId) {
        return auditPackageRepo.findByStudyId(studyId);
    }

    public Resource getAuditPackage(Long auditPackageId) {
        AuditPackage auditPackage = auditPackageRepo.findById(auditPackageId).orElseThrow();
        Path path = Paths.get(auditPackage.getPackageUri());

        return new FileSystemResource(path);
    }
}

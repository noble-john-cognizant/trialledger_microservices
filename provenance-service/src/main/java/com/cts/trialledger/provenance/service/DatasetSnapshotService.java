package com.cts.trialledger.provenance.service;


import com.cts.trialledger.provenance.client.*;
import com.cts.trialledger.provenance.entity.DatasetSnapshot;
import com.cts.trialledger.provenance.repository.DatasetSnapshotRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DatasetSnapshotService {

    private final DatasetSnapshotRepo snapshotRepo;
    private final ConsentClient consentClient;
    private final SampleClient sampleClient;
    private final AdverseEventClient adverseEventClient;
    private final VisitClient visitClient;
    @Value("${EXTERNAL_STORAGE_PATH}")
    private String LOCATION;

    public List<DatasetSnapshot> findAllSnapshotByStudyId(Long studyId) {
        return snapshotRepo.findByStudyId(studyId);
    }

    public DatasetSnapshot createStudySnapshot(Long studyId) throws Exception {
        // 1. Gather Data (participants, consent records, samples, adverse events, visits)
        List<Map<String, Object>> data = gatherData(studyId);

        // 2. Convert to JSON String in a single file
        ObjectMapper mapper = new ObjectMapper();
        String jsonContent = mapper.writeValueAsString(data.getFirst());

        // 3. Define Storage Path
        String fileName = "snapshot_study_" + studyId + "_" + System.currentTimeMillis() + ".json";
        Path path = Paths.get(LOCATION + "/storage/snapshots/" + fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, jsonContent.getBytes());

        // 4. Generate SHA-256 Hash
        String fileHash = generateSha256(path);

        // 5. Save Metadata to Database
        DatasetSnapshot snapshot = new DatasetSnapshot();
        snapshot.setStudyId(studyId);
        snapshot.setSnapshotUri(path.toAbsolutePath().toString());
        snapshot.setHash(fileHash);
        snapshot.setIncludedEntitiesJson(mapper.writeValueAsString(data.getLast()));

        return snapshotRepo.save(snapshot);
    }

    public List<Map<String, Object>> gatherData(Long studyId) {
        List<Map<String, Object>> participantList = consentClient.getParticipantsByStudy(studyId);
        List<Map<String, Object>> consentRecordList = consentClient.getConsentRecordsByStudy(studyId);
        List<Map<String, Object>> sampleList = sampleClient.getSamplesByStudy(studyId);
        List<Map<String, Object>> adverseEventList = adverseEventClient.getAdverseEventByStudy(studyId);
        List<Map<String, Object>> visitList = visitClient.getVisitsByStudy(studyId);

        Map<String, Object> fullSnapshot = new LinkedHashMap<>();
        fullSnapshot.put("studyId", studyId);
        fullSnapshot.put("capturedAt", LocalDateTime.now().toString());
        fullSnapshot.put("participants", participantList);
        fullSnapshot.put("consentRecords", consentRecordList);
        fullSnapshot.put("samples", sampleList);
        fullSnapshot.put("adverseEvents", adverseEventList);
        fullSnapshot.put("visits", visitList);

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("participantCount", participantList.size());
        summary.put("consentCount", consentRecordList.size());
        summary.put("sampleCount", sampleList.size());
        summary.put("visitCount", visitList.size());
        summary.put("adverseEventCount", adverseEventList.size());


        return List.of(fullSnapshot, summary);
    }

    private String generateSha256(Path path) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(Files.readAllBytes(path));
        return HexFormat.of().withLowerCase().formatHex(hash);
    }
}

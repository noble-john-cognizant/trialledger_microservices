package com.cts.trialledger.provenance.service;


import com.cts.trialledger.provenance.client.*;
import com.cts.trialledger.provenance.dto.ApiResponseDto;
import com.cts.trialledger.provenance.entity.DatasetSnapshot;
import com.cts.trialledger.provenance.repository.DatasetSnapshotRepo;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
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

        // Fetch each domain independently — one failure won't cascade
        List<Map<String, Object>> participantList = fetchSafely(() -> consentClient.getParticipantsByStudy(studyId), "participants");
        List<Map<String, Object>> consentRecordList = fetchSafely(() -> consentClient.getConsentRecordsByStudy(studyId), "consentRecords");
        List<Map<String, Object>> sampleList = fetchSafely(() -> sampleClient.getSamplesByStudy(studyId), "samples");
        List<Map<String, Object>> adverseEventList = fetchSafely(() -> adverseEventClient.getAdverseEventByStudy(studyId)
                .getBody(), "adverseEvents");
        List<Map<String, Object>> visitList = fetchVisitsSafely(studyId);


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

    private List<Map<String, Object>> fetchSafely(
            Supplier<List<Map<String, Object>>> supplier,
            String domain) {
        try {
            List<Map<String, Object>> result = supplier.get();
            return result != null ? result : Collections.emptyList();
        } catch (FeignException.ServiceUnavailable e) {
            log.error("[Snapshot] {} service unavailable for studyId fetch: {}", domain, e.getMessage());
            return Collections.emptyList();
        } catch (FeignException e) {
            log.error("[Snapshot] Feign error fetching {} — status {}: {}", domain, e.status(), e.getMessage());
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("[Snapshot] Unexpected error fetching {}: {}", domain, e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<Map<String, Object>> fetchVisitsSafely(Long studyId) {
        try {
            ResponseEntity<ApiResponseDto<List<Map<String, Object>>>> response =
                    visitClient.getVisitsByStudy(studyId);

            if (response == null || response.getBody() == null) {
                log.warn("[Snapshot] visitClient returned null body for studyId {}", studyId);
                return Collections.emptyList();
            }

            List<Map<String, Object>> data = response.getBody().getData();
            return data != null ? data : Collections.emptyList();

        } catch (FeignException e) {
            log.error("[Snapshot] Feign error fetching visits — status {}: {}", e.status(), e.getMessage());
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("[Snapshot] Unexpected error fetching visits: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}

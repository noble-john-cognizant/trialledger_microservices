# TrialLedger — Resilience Implementation

Circuit breaker + retry added to all 8 services with Feign clients. Frontend excluded per request.

## What Was Changed (and What Wasn't)

### Changed
For each of the 8 services with Feign clients:
1. **`pom.xml`** — added 3 dependencies (auto-versioned via Spring Cloud BOM):
   - `spring-cloud-starter-circuitbreaker-resilience4j`
   - `spring-boot-starter-aop`
   - `spring-boot-starter-actuator`
2. **Each `@FeignClient` annotation** — added `fallback = XxxFallback.class`
3. **New fallback class** per Feign client (29 total)
4. **`application.properties`** — appended resilience4j config

### NOT Changed
- Spring Boot versions — each service kept its own (some on 3.3.4, some on 4.0.6, etc.)
- Spring Cloud versions — kept each service's existing setting
- Jackson imports — `tools.jackson.databind` left as-is in SB 4 services, `com.fasterxml.jackson.databind` left as-is in SB 3 services
- All service / controller / repository / DTO code

## Fallback Inventory by Service

| Service | Fallback Classes |
|---|---|
| adverse-event-service | StudyClientFallback, ParticipantClientFallback, ProvenanceClientFallback, NotificationClientFallback |
| consent-service | StudyClientFallback, ProtocolClientFallback, AuthClientFallback, ProvenanceClientFallback, NotificationClientFallback |
| notification-service | StudyClientFallback, UserClientFallback |
| provenance-service | ConsentClientFallback, SampleClientFallback, AdverseEventClientFallback, VisitClientFallback |
| report-service | ConsentClientFallback, SampleClientFallback, AdverseEventClientFallback, ProvenanceClientFallback |
| sample-service | StudyClientFallback, ParticipantClientFallback, ProvenanceClientFallback, NotificationClientFallback, KPIClientFallback |
| study-service | ProvenanceClientFallback |
| visit-service | StudyClientFallback, ParticipantClientFallback, ProvenanceClientFallback, NotificationClientFallback |
| **Total** | **29 fallback classes** |

## Fallback Behavior

| Return Type | Fallback Returns |
|---|---|
| Object DTO | `null` |
| `void` | no-op (silent) |
| `List<X>` | empty list |
| `ResponseEntity<String>` | `503 Service Unavailable` body |
| `ResponseEntity<List<X>>` | `200 OK` empty list |

## Configuration Defaults (in each `application.properties`)

```properties
spring.cloud.openfeign.circuitbreaker.enabled=true

# Circuit breaker
resilience4j.circuitbreaker.configs.default.sliding-window-size=10
resilience4j.circuitbreaker.configs.default.minimum-number-of-calls=5
resilience4j.circuitbreaker.configs.default.failure-rate-threshold=50
resilience4j.circuitbreaker.configs.default.wait-duration-in-open-state=10s
resilience4j.circuitbreaker.configs.default.permitted-number-of-calls-in-half-open-state=3
resilience4j.circuitbreaker.configs.default.automatic-transition-from-open-to-half-open-enabled=true

# Retry
resilience4j.retry.configs.default.max-attempts=3
resilience4j.retry.configs.default.wait-duration=500ms

# Monitoring
management.endpoints.web.exposure.include=health,info,circuitbreakers,circuitbreakerevents,retries,retryevents
```

## Services NOT Modified (no Feign calls)

- **auth-service** — leaf service, no outbound calls
- **api-gateway** — uses gateway-level routing (different mechanism)
- **service-registry** — Eureka server, no Feign

## Testing the Circuit Breaker

After starting any service, check breaker status at:
```
GET http://localhost:<port>/actuator/circuitbreakers
```

Demo flow:
1. Start everything
2. Hit `GET http://localhost:8086/api/adverse-events/1/full` — returns full data
3. Stop study-service
4. Hit it 5+ times — see `study: null` in response
5. Check actuator — STUDY-SERVICE breaker shows `state: OPEN`
6. Restart study-service, wait 10 seconds
7. Hit endpoint again — breaker closes, full data returns

## Notes

- **Sample-service is on Spring Boot 4.0.6** (legitimate version, released April 2026). Its `tools.jackson.databind` imports are correct for that version.
- **Consent-service** has `resilience4j-spring-boot3` already in pom but doesn't use it (commented config). I added the Spring Cloud OpenFeign integration alongside it — they coexist fine.
- **Notification-service** same situation as consent-service.
- All 11 pom.xml files validated as well-formed XML after changes.

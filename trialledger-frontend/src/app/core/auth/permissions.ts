import { Role } from '../models/user.models';

/**
 * Single source of truth that mirrors the @PreAuthorize annotations
 * on the Spring Boot backend controllers. Used by:
 *  - route guards (block direct URL access)
 *  - components (hide buttons / menu items the user can't use)
 *
 * Keep this aligned with the backend @PreAuthorize rules.
 */
export const PERMISSIONS = {
  // Users
  USER_REGISTER:           ['ADMIN'],
  USER_VIEW_ONE:           ['ADMIN', 'PI'],
  USER_LIST:               ['ADMIN'],
  USER_UPDATE:             ['ADMIN'],

  // Audit
  AUDIT_VIEW:              ['ADMIN', 'COMPLIANCE', 'AUDITOR'],

  // Studies
  STUDY_LIST:              ['ADMIN', 'PI', 'COORDINATOR', 'AUDITOR', 'COMPLIANCE', 'DATA_MANAGER'],
  STUDY_VIEW:              ['ADMIN', 'PI', 'TECHNICIAN', 'AUDITOR', 'COMPLIANCE', 'DATA_MANAGER', 'COORDINATOR'],
  STUDY_CREATE:            ['ADMIN', 'PI', 'COORDINATOR'],
  STUDY_MANAGE:            ['ADMIN', 'PI'],
  STUDY_DELETE:            ['ADMIN', 'PI'],

  // Protocols
  PROTOCOL_VIEW:           ['ADMIN', 'PI', 'TECHNICIAN', 'AUDITOR', 'COMPLIANCE', 'COORDINATOR'],
  PROTOCOL_CREATE:         ['ADMIN', 'PI'],
  PROTOCOL_APPROVE:        ['ADMIN', 'PI', 'COMPLIANCE'],
  PROTOCOL_MANAGE:         ['ADMIN', 'PI', 'COMPLIANCE'],

  // Participants — ADMIN is read-only (per product decision)
  PARTICIPANT_LIST:        ['ADMIN', 'PI', 'COORDINATOR', 'COMPLIANCE', 'DATA_MANAGER'],
  PARTICIPANT_VIEW:        ['ADMIN', 'PI', 'COORDINATOR', 'COMPLIANCE', 'TECHNICIAN', 'DATA_MANAGER', 'AUDITOR', 'PARTICIPANT'],
  PARTICIPANT_CREATE:      ['PI', 'COORDINATOR'],
  PARTICIPANT_UPDATE:      ['PI', 'COORDINATOR'],
  PARTICIPANT_DELETE:      ['COORDINATOR', 'PARTICIPANT'],

  // Consents
  CONSENT_CREATE:          ['ADMIN', 'COORDINATOR', 'PARTICIPANT'],
  CONSENT_VIEW:            ['ADMIN', 'PI', 'COORDINATOR', 'TECHNICIAN', 'COMPLIANCE', 'PARTICIPANT', 'AUDITOR'],
  CONSENT_WITHDRAW:        ['ADMIN', 'PI', 'COORDINATOR', 'PARTICIPANT'],
  CONSENT_VERIFY:          ['ADMIN', 'PI', 'COORDINATOR', 'COMPLIANCE', 'AUDITOR'],

  // Visits
  VISIT_SCHEDULE:          ['ADMIN', 'PI', 'COORDINATOR'],
  VISIT_VIEW:              ['ADMIN', 'PI', 'COORDINATOR', 'COMPLIANCE', 'DATA_MANAGER', 'PARTICIPANT'],
  VISIT_UPDATE:            ['ADMIN', 'PI', 'COORDINATOR'],
  VISIT_DELETE:            ['ADMIN', 'PI', 'COORDINATOR', 'PARTICIPANT'],

  // Source data
  SOURCE_CREATE:           ['ADMIN', 'TECHNICIAN', 'COORDINATOR'],
  SOURCE_VIEW:             ['ADMIN', 'PI', 'COORDINATOR', 'COMPLIANCE', 'DATA_MANAGER', 'AUDITOR'],
  SOURCE_VERIFY:           ['ADMIN', 'PI', 'COORDINATOR'],

  // Adverse events
  AE_VIEW:                 ['ADMIN', 'PI', 'COORDINATOR', 'COMPLIANCE', 'DATA_MANAGER', 'AUDITOR'],
  AE_CREATE:               ['ADMIN', 'PI', 'COORDINATOR', 'TECHNICIAN'],
  AE_MANAGE:               ['ADMIN', 'PI', 'COMPLIANCE'],
  AE_DELETE:               ['ADMIN', 'PI', 'COORDINATOR', 'TECHNICIAN'],
  AE_FOLLOWUP_VIEW:        ['ADMIN', 'PI', 'COMPLIANCE', 'AUDITOR'],
  AE_FOLLOWUP_CREATE:      ['ADMIN', 'PI', 'COORDINATOR', 'COMPLIANCE'],
  AE_FOLLOWUP_DELETE:      ['ADMIN', 'PI', 'COORDINATOR', 'COMPLIANCE'],

  // Samples
  SAMPLE_VIEW:             ['ADMIN', 'PI', 'TECHNICIAN', 'AUDITOR', 'COMPLIANCE', 'DATA_MANAGER'],
  SAMPLE_VIEW_BY_STUDY:    ['ADMIN', 'PI', 'COORDINATOR', 'TECHNICIAN', 'DATA_MANAGER'],
  SAMPLE_CREATE:           ['ADMIN', 'COORDINATOR', 'TECHNICIAN'],
  CUSTODY_CREATE:          ['ADMIN', 'TECHNICIAN'],
  CUSTODY_VIEW:            ['ADMIN', 'PI', 'TECHNICIAN', 'AUDITOR', 'COMPLIANCE', 'DATA_MANAGER'],
  ASSAY_CREATE:            ['ADMIN', 'TECHNICIAN'],
  ASSAY_VIEW:              ['ADMIN', 'PI', 'TECHNICIAN', 'AUDITOR', 'DATA_MANAGER'],
  STORAGE_CREATE:          ['ADMIN', 'TECHNICIAN'],
  STORAGE_VIEW:            ['ADMIN', 'PI', 'TECHNICIAN', 'DATA_MANAGER'],

  // Provenance
  PROVENANCE_VIEW:         ['ADMIN', 'PI', 'COMPLIANCE', 'DATA_MANAGER', 'AUDITOR'],
  SNAPSHOT_CREATE:         ['ADMIN', 'PI', 'DATA_MANAGER'],
  SNAPSHOT_VIEW:           ['ADMIN', 'PI', 'COMPLIANCE', 'DATA_MANAGER', 'AUDITOR'],
  PACKAGE_CREATE:          ['ADMIN', 'COMPLIANCE'],
  PACKAGE_VIEW:            ['ADMIN', 'COMPLIANCE', 'AUDITOR'],
  PACKAGE_DOWNLOAD:        ['ADMIN', 'COMPLIANCE', 'AUDITOR'],

  // Reports & KPIs
  REPORT_CREATE:           ['ADMIN', 'PI', 'COMPLIANCE', 'DATA_MANAGER'],
  REPORT_VIEW:             ['ADMIN', 'PI', 'COORDINATOR', 'AUDITOR', 'COMPLIANCE', 'DATA_MANAGER'],
  KPI_CREATE:              ['ADMIN', 'PI'],
  KPI_VIEW:                ['ADMIN', 'PI', 'COMPLIANCE', 'DATA_MANAGER'],

  // Notifications & Alerts
  NOTIFICATIONS_OWN:       ['ADMIN', 'PI', 'COORDINATOR', 'TECHNICIAN', 'COMPLIANCE', 'DATA_MANAGER', 'AUDITOR', 'PARTICIPANT'],
  NOTIFICATIONS_ALL:       ['ADMIN', 'COMPLIANCE'],
  NOTIFICATION_DELETE:     ['ADMIN'],
  ALERT_VIEW:              ['ADMIN', 'COMPLIANCE'],
  ALERT_MANAGE:            ['ADMIN'],
} as const;

export type PermissionKey = keyof typeof PERMISSIONS;

/** Pure check helper — no Angular dependencies, easy to test. */
export function isAllowed(role: Role | null | undefined, key: PermissionKey): boolean {
  if (!role) return false;
  const allowed = PERMISSIONS[key] as readonly string[];
  return allowed.includes(role);
}

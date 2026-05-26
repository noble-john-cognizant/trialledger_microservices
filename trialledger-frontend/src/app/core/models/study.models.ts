export type StudyStatus =
  | 'PLANNED' | 'ACTIVE' | 'COMPLETED' | 'SUSPENDED' | 'TERMINATED';

export const ALL_STUDY_STATUSES: StudyStatus[] = [
  'PLANNED', 'ACTIVE', 'COMPLETED', 'SUSPENDED', 'TERMINATED'
];

export type ProtocolStatus =
  | 'DRAFT' | 'UNDER_REVIEW' | 'APPROVED' | 'ACTIVE' | 'SUPERSEDED' | 'ARCHIVED';

export const ALL_PROTOCOL_STATUSES: ProtocolStatus[] = [
  'DRAFT', 'UNDER_REVIEW', 'APPROVED', 'ACTIVE', 'SUPERSEDED', 'ARCHIVED'
];

export interface StudyRequestDto {
  title: string;
  sponsor: string;
  protocolNumber: string;
  startDate: string;
  endDate: string;
}

export interface StudyResponseDto {
  studyId: number;
  title: string;
  sponsor: string;
  protocolNumber: string;
  startDate: string;
  endDate: string;
  status: StudyStatus;
  isDeleted: boolean;
}

export interface ProtocolVersionRequestDto {
  versionNumber: string;
  documentUrl: string;
  effectiveDate: string;
}

export interface ProtocolVersionResponseDto {
  protocolId: number;
  studyId: number;
  versionNumber: string;
  documentUrl: string;
  effectiveDate: string;
  approvedById: number;
  status: ProtocolStatus;
  isDeleted: boolean;
}

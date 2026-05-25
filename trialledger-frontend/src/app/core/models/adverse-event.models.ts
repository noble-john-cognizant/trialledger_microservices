export type AEStatus = 'OPEN' | 'UNDER_REVIEW' | 'RESOLVED' | 'REPORTED';
export const ALL_AE_STATUSES: AEStatus[] = ['OPEN', 'UNDER_REVIEW', 'RESOLVED', 'REPORTED'];

export type Severity = 'MILD' | 'MODERATE' | 'SEVERE';
export const ALL_SEVERITIES: Severity[] = ['MILD', 'MODERATE', 'SEVERE'];

export interface AdverseEventRequestDto {
  participantId: number;
  studyId: number;
  severity: Severity;
  description: string;
  reportedById: number;
}

export interface AdverseEventResponseDto {
  aeId: number;
  participantId: number;
  studyId: number;
  reportedAt: string;
  severity: Severity;
  description: string;
  reportedById: number;
  status: AEStatus;
  isDeleted: boolean;
}

export interface AEFollowUpRequestDto {
  actionTaken: string;
  performedById: number;
  notes: string;
}

export interface AEFollowUpResponseDto {
  followUpId: number;
  aeId: number;
  actionTaken: string;
  performedById: number;
  performedAt: string;
  notes: string;
  isDeleted: boolean;
}

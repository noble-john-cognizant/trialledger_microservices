export type VisitStatus =
  | 'SCHEDULED' | 'COMPLETED' | 'MISSED' | 'CANCELLED';

export const ALL_VISIT_STATUSES: VisitStatus[] = [
  'SCHEDULED', 'COMPLETED', 'MISSED', 'CANCELLED'
];

export type VisitType =
  | 'SCREENING' | 'BASELINE' | 'FOLLOW_UP'
  | 'TREATMENT' | 'END_OF_STUDY' | 'UNSCHEDULED';

export const ALL_VISIT_TYPES: VisitType[] = [
  'SCREENING', 'BASELINE', 'FOLLOW_UP', 'TREATMENT', 'END_OF_STUDY', 'UNSCHEDULED'
];

export interface VisitRequestDto {
  participantId: number;
  studyId: number;
  visitType: VisitType;
  scheduledAt: string;
}

export interface VisitResponseDto {
  visitId: number;
  participantId: number;
  studyId: number;
  visitType: VisitType;
  scheduledAt: string;
  performedAt: string;
  status: VisitStatus;
}

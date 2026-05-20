export type EnrollmentStatus =
  | 'ENROLLED' | 'PENDING' | 'WITHDRAWN' | 'COMPLETED';

export const ALL_ENROLLMENT_STATUSES: EnrollmentStatus[] = [
  'ENROLLED', 'PENDING', 'WITHDRAWN', 'COMPLETED'
];

export interface ParticipantRequestDTO {
  studyId: number;
  externalId: string;
  name: string;
  dob: string;
  phone: string;
  email: string;
}

export interface ParticipantResponseDTO {
  participantId: number;
  studyId: number;
  externalId: string;
  name: string;
  dob: string;
  contactInfo: string;
  enrollmentStatus: EnrollmentStatus;
}

export interface EnrollmentStatsDTO {
  studyId: number;
  totalParticipants: number;
  enrolledCount: number;
  withdrawnCount: number;
}

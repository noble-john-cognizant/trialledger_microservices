export type SampleStatus = 'COLLECTED' | 'IN_ANALYSIS' | 'COMPLETED';
export const ALL_SAMPLE_STATUSES: SampleStatus[] = ['COLLECTED', 'IN_ANALYSIS', 'COMPLETED'];

export type SampleType =
  | 'BLOOD' | 'URINE' | 'SALIVA' | 'TISSUE' | 'SERUM' | 'PLASMA';

export const ALL_SAMPLE_TYPES: SampleType[] =
  ['BLOOD', 'URINE', 'SALIVA', 'TISSUE', 'SERUM', 'PLASMA'];

export interface SampleRequestDTO {
  participantId: number;
  studyId: number;
  sampleType: SampleType;
  collectedBy: string;
  initialLocation: string;
  status: SampleStatus;
}

export interface SampleResponseDTO {
  sampleId: number;
  participantId: number;
  studyId: number;
  sampleType: SampleType;
  collectedAt: string;
  collectedBy: string;
  initialLocation: string;
  status: SampleStatus;
}

export interface ChainOfCustodyRequestDTO {
  sampleId: number;
  fromUser: string;
  toUser: string;
  fromLocation: string;
  toLocation: string;
  notes: string;
}

export interface ChainOfCustodyResponseDTO {
  cocId: number;
  sampleId: number;
  fromUser: string;
  toUser: string;
  transferAt: string;
  fromLocation: string;
  toLocation: string;
  notes: string;
}

export interface AssayRunRequestDTO {
  sampleId: number;
  instrumentId: number;
  operatorId: number;
  protocolRef: string;
  metadataJson: string;
}

export interface AssayRunResponseDTO {
  assayId: number;
  sampleId: number;
  instrumentId: number;
  operatorId: number;
  runDate: string;
  protocolRef: string;
  resultUri: string;
  metadataJson: string;
}

export interface SampleStorageRequestDTO {
  freezerId: number;
  shelf: string;
  box: string;
  position: string;
}

export interface SampleStorageResponseDTO {
  storageId: number;
  sampleId: number;
  freezerId: number;
  shelf: string;
  box: string;
  position: string;
  storedAt: string;
  retrievedAt: string;
}

export interface SampleStatsDTO {
  studyId: number;
  totalSamples: number;
  collectedCount: number;
  inAnalysisCount: number;
  completedCount: number;
  custodyEventCount: number;
  assayRunCount: number;
}

export interface ProvenanceDTO {
  provId: number;
  entityType: string;
  entityId: number;
  action: string;
  performedBy: number;
  performedAt: string;
  metadataJson: Record<string, any>;
}

export interface ProvenanceRequestDTO {
  action: string;
  entityType: string;
  performedBy: number;
  entityId: number;
  metadata: string;
}

export interface DatasetSnapshot {
  snapshotId: number;
  studyId: number;
  snapshotDate: string;
  snapshotUri: string;
  hash: string;
  includedEntitiesJson: string;
}

export interface AuditPackageDTO {
  studyId: number;
  startDate: string;
  endDate: string;
}

export interface AuditPackage {
  packageId: number;
  studyId: number;
  periodStart: string;
  periodEnd: string;
  packageUri: string;
  generatedAt: string;
  contentsJSON: string;
}

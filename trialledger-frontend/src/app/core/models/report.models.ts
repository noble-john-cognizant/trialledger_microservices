export type ReportScope = 'ENROLLMENT' | 'SAMPLES' | 'AE' | 'PROVENANCE';
export const ALL_REPORT_SCOPES: ReportScope[] = ['ENROLLMENT', 'SAMPLES', 'AE', 'PROVENANCE'];

export interface ReportRequestDTO {
  studyId: number;
  scope: ReportScope;
  parametersJson: string;
  reportingPeriod: string;
}

export interface ReportResponseDTO {
  reportId: number;
  studyId: number;
  scope: ReportScope;
  metricsJson: any;
  generatedAt: string;
  reportUri: string;
}

export interface KPIRequestDTO {
  name: string;
  definition: string;
  target: number;
  reportingPeriod: string;
}

export interface KPIResponseDTO {
  kpiId: number;
  name: string;
  definition: string;
  target: number;
  currentValue: number;
  reportingPeriod: string;
}

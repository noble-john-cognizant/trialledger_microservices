export interface SourceDataRequestDto {
  visitId: number;
  collectedBy: number;
  dataType: string;
  dataUri: string;
  collectedAt: string;
}

export interface SourceDataResponseDto {
  sourceId: number;
  visitId: number;
  collectedBy: number;
  dataType: string;
  dataUri: string;
  collectedAt: string;
  hash: string;
}

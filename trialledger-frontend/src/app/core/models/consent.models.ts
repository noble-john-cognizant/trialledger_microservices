export type ConsentMethod = 'IN_PERSON' | 'ELECTRONIC';
export type ConsentStatus = 'ACTIVE' | 'WITHDRAWN' | 'EXPIRED';

export interface ConsentRequestDTO {
  participantId: number;
  protocolId: number;
  versionNumber: string;
  consentMethod: ConsentMethod;
  signedDocumentUri: string;
}

export interface ConsentResponseDTO {
  consentId: number;
  participantId: number;
  protocolId: number;
  versionNumber: string;
  status: ConsentStatus;
  consentDate: string;
  consentMethod: ConsentMethod;
  signedDocumentUri: string;
}

export interface ConsentWithdrawalDTO {
  consentId: number;
  withdrawnBy: number;
  reason: string;
  effectOnData: string;
}

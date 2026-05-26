export interface AuditLogDTO {
  auditId: number;
  userId: number;
  action: string;
  resource: string;
  timestamp: string;
  details: Record<string, any>;
}

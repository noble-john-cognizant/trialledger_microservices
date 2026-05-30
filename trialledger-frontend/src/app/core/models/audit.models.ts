export interface AuditLogDTO {
  auditId: number;
  userId: number;
  action: string;
  resource: string;
  timestamp: string;
  details: Record<string, any>;
}

/** Subset of Spring's Page<T> JSON envelope that the UI actually reads. */
export interface PageResponse<T> {
  content: T[];
  number: number;        // current page (0-indexed)
  size: number;          // page size
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

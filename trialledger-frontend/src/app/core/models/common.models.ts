/** Standard envelope returned by visit-service */
export interface ApiResponseDto<T> {
  status: string;
  message: string;
  data: T;
}

/** Envelope returned by adverse-event-service POST endpoints */
export interface ApiMessage {
  message: string;
  timestamp: string;
}

/** Spring Data Page<T> shape */
export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}

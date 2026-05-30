import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuditLogDTO, PageResponse } from '../models/audit.models';

@Injectable({ providedIn: 'root' })
export class AuditService {
  private http = inject(HttpClient);
  private base = `${environment.apiBase}/api/audit`;

  /** Backend returns a Spring Page<AuditLogDTO> envelope sorted by timestamp DESC. */
  list(page = 0, size = 20): Observable<PageResponse<AuditLogDTO>> {
    return this.http.get<PageResponse<AuditLogDTO>>(this.base, {
      params: new HttpParams().set('page', page).set('size', size)
    });
  }
  byAction(action: string, page = 0, size = 20): Observable<PageResponse<AuditLogDTO>> {
    return this.http.get<PageResponse<AuditLogDTO>>(`${this.base}/action/${action}`, {
      params: new HttpParams().set('page', page).set('size', size)
    });
  }
  byUser(userId: number, page = 0, size = 20): Observable<PageResponse<AuditLogDTO>> {
    return this.http.get<PageResponse<AuditLogDTO>>(`${this.base}/userId/${userId}`, {
      params: new HttpParams().set('page', page).set('size', size)
    });
  }
  byRange(from: string, to: string, page = 0, size = 20): Observable<PageResponse<AuditLogDTO>> {
    return this.http.get<PageResponse<AuditLogDTO>>(`${this.base}/find-by-range`, {
      params: new HttpParams()
        .set('from', from).set('to', to)
        .set('page', page).set('size', size)
    });
  }
}

import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuditLogDTO } from '../models/audit.models';

@Injectable({ providedIn: 'root' })
export class AuditService {
  private http = inject(HttpClient);
  private base = `${environment.apiBase}/api/audit`;

  list(): Observable<AuditLogDTO[]> {
    return this.http.get<AuditLogDTO[]>(this.base);
  }
  byAction(action: string): Observable<AuditLogDTO[]> {
    return this.http.get<AuditLogDTO[]>(`${this.base}/action/${action}`);
  }
  byUser(userId: number): Observable<AuditLogDTO[]> {
    return this.http.get<AuditLogDTO[]>(`${this.base}/userId/${userId}`);
  }
  byRange(from: string, to: string): Observable<AuditLogDTO[]> {
    return this.http.get<AuditLogDTO[]>(`${this.base}/find-by-range`, {
      params: new HttpParams().set('from', from).set('to', to)
    });
  }
}

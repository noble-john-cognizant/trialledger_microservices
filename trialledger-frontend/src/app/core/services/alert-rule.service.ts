import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AlertRuleRequestDTO, AlertRuleResponseDTO, NotificationCategory } from '../models/notification.models';

@Injectable({ providedIn: 'root' })
export class AlertRuleService {
  private http = inject(HttpClient);
  private base = `${environment.apiBase}/api/alerts`;

  list(): Observable<AlertRuleResponseDTO[]> {
    return this.http.get<AlertRuleResponseDTO[]>(this.base);
  }
  get(id: number): Observable<AlertRuleResponseDTO> {
    return this.http.get<AlertRuleResponseDTO>(`${this.base}/${id}`);
  }
  create(dto: AlertRuleRequestDTO): Observable<AlertRuleResponseDTO> {
    return this.http.post<AlertRuleResponseDTO>(this.base, dto);
  }
  update(id: number, dto: AlertRuleRequestDTO): Observable<AlertRuleResponseDTO> {
    return this.http.put<AlertRuleResponseDTO>(`${this.base}/${id}`, dto);
  }
  toggle(id: number): Observable<AlertRuleResponseDTO> {
    return this.http.put<AlertRuleResponseDTO>(`${this.base}/${id}/toggle`, null);
  }
  delete(id: number): Observable<string> {
    return this.http.delete(`${this.base}/${id}`, { responseType: 'text' });
  }
  trigger(userId: number, entityId: number, message: string, category: NotificationCategory): Observable<string> {
    return this.http.post(`${this.base}/trigger`, null, {
      params: new HttpParams()
        .set('userId', userId)
        .set('entityId', entityId)
        .set('message', message)
        .set('category', category),
      responseType: 'text'
    });
  }
}

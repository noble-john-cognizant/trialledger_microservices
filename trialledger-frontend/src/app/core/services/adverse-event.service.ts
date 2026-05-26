import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiMessage } from '../models/common.models';
import {
  AdverseEventRequestDto, AdverseEventResponseDto, AEStatus, Severity,
  AEFollowUpRequestDto, AEFollowUpResponseDto
} from '../models/adverse-event.models';

@Injectable({ providedIn: 'root' })
export class AdverseEventService {
  private http = inject(HttpClient);
  private base = `${environment.apiBase}/api/adverse-events`;

  list(): Observable<AdverseEventResponseDto[]> {
    return this.http.get<AdverseEventResponseDto[]>(this.base);
  }
  get(id: number): Observable<AdverseEventResponseDto> {
    return this.http.get<AdverseEventResponseDto>(`${this.base}/${id}`);
  }
  byStudy(studyId: number): Observable<AdverseEventResponseDto[]> {
    return this.http.get<AdverseEventResponseDto[]>(`${this.base}/study/${studyId}`);
  }
  byParticipant(pid: number): Observable<AdverseEventResponseDto[]> {
    return this.http.get<AdverseEventResponseDto[]>(`${this.base}/participant/${pid}`);
  }
  create(dto: AdverseEventRequestDto): Observable<ApiMessage> {
    return this.http.post<ApiMessage>(this.base, dto);
  }
  updateStatus(id: number, status: AEStatus): Observable<AdverseEventResponseDto> {
    return this.http.patch<AdverseEventResponseDto>(`${this.base}/${id}/status`, null, {
      params: new HttpParams().set('status', status)
    });
  }
  updateSeverity(id: number, severity: Severity): Observable<AdverseEventResponseDto> {
    return this.http.patch<AdverseEventResponseDto>(`${this.base}/${id}/severity`, null, {
      params: new HttpParams().set('severity', severity)
    });
  }
  delete(id: number): Observable<string> {
    return this.http.delete(`${this.base}/${id}`, { responseType: 'text' });
  }
  fullDetails(id: number): Observable<any> {
    return this.http.get(`${this.base}/${id}/full`);
  }
  followUps(aeId: number): Observable<AEFollowUpResponseDto[]> {
    return this.http.get<AEFollowUpResponseDto[]>(`${this.base}/${aeId}/follow-ups`);
  }
  addFollowUp(aeId: number, dto: AEFollowUpRequestDto): Observable<ApiMessage> {
    return this.http.post<ApiMessage>(`${this.base}/${aeId}/follow-ups`, dto);
  }
  deleteFollowUp(followUpId: number): Observable<string> {
    return this.http.delete(`${this.base}/follow-ups/${followUpId}`, { responseType: 'text' });
  }
}

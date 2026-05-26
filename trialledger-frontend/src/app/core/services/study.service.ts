import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  StudyRequestDto, StudyResponseDto, StudyStatus,
  ProtocolVersionRequestDto, ProtocolVersionResponseDto, ProtocolStatus
} from '../models/study.models';

@Injectable({ providedIn: 'root' })
export class StudyService {
  private http = inject(HttpClient);
  private base = `${environment.apiBase}/api/studies`;

  list(): Observable<StudyResponseDto[]> {
    return this.http.get<StudyResponseDto[]>(this.base);
  }
  get(id: number): Observable<StudyResponseDto> {
    return this.http.get<StudyResponseDto>(`${this.base}/${id}`);
  }
  create(dto: StudyRequestDto): Observable<StudyResponseDto> {
    return this.http.post<StudyResponseDto>(this.base, dto);
  }
  updateStatus(id: number, status: StudyStatus): Observable<string> {
    return this.http.patch(`${this.base}/${id}/status`, null, {
      params: new HttpParams().set('status', status),
      responseType: 'text'
    });
  }
  delete(id: number): Observable<string> {
    return this.http.delete(`${this.base}/${id}`, { responseType: 'text' });
  }

  // ---- Protocols (same controller path tree) ----
  listProtocols(): Observable<ProtocolVersionResponseDto[]> {
    return this.http.get<ProtocolVersionResponseDto[]>(`${this.base}/protocols`);
  }
  studyProtocols(studyId: number): Observable<ProtocolVersionResponseDto[]> {
    return this.http.get<ProtocolVersionResponseDto[]>(`${this.base}/${studyId}/protocols`);
  }
  getProtocol(protocolId: number): Observable<ProtocolVersionResponseDto> {
    return this.http.get<ProtocolVersionResponseDto>(`${this.base}/protocols/${protocolId}`);
  }
  addProtocol(studyId: number, dto: ProtocolVersionRequestDto): Observable<ProtocolVersionResponseDto> {
    return this.http.post<ProtocolVersionResponseDto>(`${this.base}/${studyId}/protocols`, dto);
  }
  updateProtocolStatus(protocolId: number, status: ProtocolStatus): Observable<string> {
    return this.http.patch(`${this.base}/protocols/${protocolId}/status`, null, {
      params: new HttpParams().set('protocolStatus', status),
      responseType: 'text'
    });
  }
  approveProtocol(protocolId: number): Observable<string> {
    return this.http.patch(`${this.base}/protocols/${protocolId}/approve`, null,
      { responseType: 'text' });
  }
  deleteProtocol(studyId: number, protocolId: number): Observable<string> {
    return this.http.delete(`${this.base}/${studyId}/protocols/${protocolId}`,
      { responseType: 'text' });
  }
}

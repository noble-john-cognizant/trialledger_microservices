import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  ParticipantRequestDTO, ParticipantResponseDTO,
  EnrollmentStatsDTO, EnrollmentStatus
} from '../models/participant.models';

@Injectable({ providedIn: 'root' })
export class ParticipantService {
  private http = inject(HttpClient);
  private base = `${environment.apiBase}/api/participants`;

  list(): Observable<ParticipantResponseDTO[]> {
    return this.http.get<ParticipantResponseDTO[]>(this.base);
  }
  get(id: number): Observable<ParticipantResponseDTO> {
    return this.http.get<ParticipantResponseDTO>(`${this.base}/${id}`);
  }
  byStudy(studyId: number): Observable<ParticipantResponseDTO[]> {
    return this.http.get<ParticipantResponseDTO[]>(`${this.base}/study/${studyId}`);
  }
  stats(studyId: number): Observable<EnrollmentStatsDTO> {
    return this.http.get<EnrollmentStatsDTO>(`${this.base}/stats/${studyId}`);
  }
  create(dto: ParticipantRequestDTO): Observable<ParticipantResponseDTO> {
    return this.http.post<ParticipantResponseDTO>(this.base, dto);
  }
  updateStatus(participantId: number, status: EnrollmentStatus): Observable<ParticipantResponseDTO> {
    return this.http.patch<ParticipantResponseDTO>(`${this.base}/${participantId}/status`, null, {
      params: new HttpParams().set('status', status)
    });
  }
  delete(participantId: number): Observable<ParticipantResponseDTO> {
    return this.http.delete<ParticipantResponseDTO>(`${this.base}/${participantId}`);
  }
}

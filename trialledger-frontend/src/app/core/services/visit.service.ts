import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponseDto } from '../models/common.models';
import { VisitRequestDto, VisitResponseDto, VisitStatus } from '../models/visit.models';

@Injectable({ providedIn: 'root' })
export class VisitService {
  private http = inject(HttpClient);
  private base = `${environment.apiBase}/api/visits`;

  schedule(dto: VisitRequestDto): Observable<VisitResponseDto> {
    return this.http.post<ApiResponseDto<VisitResponseDto>>(`${this.base}/schedule`, dto)
      .pipe(map(r => r.data));
  }
  byParticipant(id: number): Observable<VisitResponseDto[]> {
    return this.http.get<ApiResponseDto<VisitResponseDto[]>>(`${this.base}/participant/${id}`)
      .pipe(map(r => r.data));
  }
  byStudy(studyId: number): Observable<VisitResponseDto[]> {
    return this.http.get<ApiResponseDto<VisitResponseDto[]>>(`${this.base}/study/${studyId}`)
      .pipe(map(r => r.data));
  }
  get(id: number): Observable<VisitResponseDto> {
    return this.http.get<ApiResponseDto<VisitResponseDto>>(`${this.base}/${id}`)
      .pipe(map(r => r.data));
  }
  /**
   * Update a visit's status. When marking it COMPLETED, an optional
   * `performedAt` ISO-8601 datetime is sent so the backend records when
   * the visit actually took place. For other statuses the timestamp is
   * ignored by the server.
   */
  updateStatus(id: number, status: VisitStatus, performedAt?: string): Observable<VisitResponseDto> {
    let params = new HttpParams().set('status', status);
    if (performedAt) params = params.set('performedAt', performedAt);
    return this.http.put<ApiResponseDto<VisitResponseDto>>(`${this.base}/${id}/status`, null, { params })
      .pipe(map(r => r.data));
  }
  delete(id: number): Observable<VisitResponseDto> {
    return this.http.delete<ApiResponseDto<VisitResponseDto>>(`${this.base}/${id}`)
      .pipe(map(r => r.data));
  }
}

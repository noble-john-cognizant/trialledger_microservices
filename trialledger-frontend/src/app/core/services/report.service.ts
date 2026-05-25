import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ReportRequestDTO, ReportResponseDTO, ReportScope } from '../models/report.models';

@Injectable({ providedIn: 'root' })
export class ReportService {
  private http = inject(HttpClient);
  private base = `${environment.apiBase}/api/reports`;

  list(): Observable<ReportResponseDTO[]> {
    return this.http.get<ReportResponseDTO[]>(this.base);
  }
  get(id: number): Observable<ReportResponseDTO> {
    return this.http.get<ReportResponseDTO>(`${this.base}/${id}`);
  }
  byStudy(studyId: number): Observable<ReportResponseDTO[]> {
    return this.http.get<ReportResponseDTO[]>(`${this.base}/study/${studyId}`);
  }
  byScope(scope: ReportScope): Observable<ReportResponseDTO[]> {
    return this.http.get<ReportResponseDTO[]>(`${this.base}/scope/${scope}`);
  }
  create(dto: ReportRequestDTO): Observable<ReportResponseDTO> {
    return this.http.post<ReportResponseDTO>(this.base, dto);
  }
}

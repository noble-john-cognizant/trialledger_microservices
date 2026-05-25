import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { KPIRequestDTO, KPIResponseDTO } from '../models/kpi.models';

@Injectable({ providedIn: 'root' })
export class KpiService {
  private http = inject(HttpClient);
  private base = `${environment.apiBase}/api/kpis`;

  list(): Observable<KPIResponseDTO[]> {
    return this.http.get<KPIResponseDTO[]>(this.base);
  }
  get(id: number): Observable<KPIResponseDTO> {
    return this.http.get<KPIResponseDTO>(`${this.base}/${id}`);
  }
  byPeriod(period: string): Observable<KPIResponseDTO[]> {
    return this.http.get<KPIResponseDTO[]>(`${this.base}/period/${period}`);
  }
  create(dto: KPIRequestDTO): Observable<KPIResponseDTO> {
    return this.http.post<KPIResponseDTO>(this.base, dto);
  }
}

import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  ConsentRequestDTO, ConsentResponseDTO, ConsentWithdrawalDTO
} from '../models/consent.models';

@Injectable({ providedIn: 'root' })
export class ConsentService {
  private http = inject(HttpClient);
  private base = `${environment.apiBase}/api/consents`;

  create(dto: ConsentRequestDTO): Observable<ConsentResponseDTO> {
    return this.http.post<ConsentResponseDTO>(this.base, dto);
  }
  byParticipant(id: number): Observable<ConsentResponseDTO[]> {
    return this.http.get<ConsentResponseDTO[]>(`${this.base}/participant/${id}`);
  }
  byStudy(studyId: number): Observable<ConsentResponseDTO[]> {
    return this.http.get<ConsentResponseDTO[]>(`${this.base}/study/${studyId}`);
  }
  withdraw(id: number, dto: ConsentWithdrawalDTO): Observable<string> {
    return this.http.post(`${this.base}/${id}/withdraw`, dto, { responseType: 'text' });
  }
  verify(id: number): Observable<string> {
    return this.http.get(`${this.base}/${id}/verify`, { responseType: 'text' });
  }
}

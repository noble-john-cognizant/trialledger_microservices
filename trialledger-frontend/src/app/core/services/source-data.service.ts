import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponseDto } from '../models/common.models';
import { SourceDataRequestDto, SourceDataResponseDto } from '../models/source-data.models';

@Injectable({ providedIn: 'root' })
export class SourceDataService {
  private http = inject(HttpClient);
  private base = `${environment.apiBase}/api/sourcedata`;

  create(dto: SourceDataRequestDto): Observable<SourceDataResponseDto> {
    return this.http.post<ApiResponseDto<SourceDataResponseDto>>(`${this.base}/visit`, dto)
      .pipe(map(r => r.data));
  }
  get(id: number): Observable<SourceDataResponseDto> {
    return this.http.get<ApiResponseDto<SourceDataResponseDto>>(`${this.base}/${id}`)
      .pipe(map(r => r.data));
  }
  verify(id: number): Observable<boolean> {
    return this.http.get<ApiResponseDto<boolean>>(`${this.base}/verify/${id}`)
      .pipe(map(r => r.data));
  }
  byVisit(visitId: number): Observable<SourceDataResponseDto[]> {
    return this.http.get<ApiResponseDto<SourceDataResponseDto[]>>(`${this.base}/byVisit/${visitId}`)
      .pipe(map(r => r.data));
  }
}

import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Page } from '../models/common.models';
import {
  ProvenanceDTO, ProvenanceRequestDTO,
  DatasetSnapshot, AuditPackage, AuditPackageDTO
} from '../models/provenance.models';

@Injectable({ providedIn: 'root' })
export class ProvenanceService {
  private http = inject(HttpClient);
  private base = environment.apiBase;

  page(pageNumber = 0, pageSize = 20): Observable<Page<ProvenanceDTO>> {
    return this.http.get<Page<ProvenanceDTO>>(`${this.base}/api/provenance`, {
      params: new HttpParams().set('pageNumber', pageNumber).set('pageSize', pageSize)
    });
  }
  // create(dto: ProvenanceRequestDTO): Observable<string> {
  //   return this.http.post(`${this.base}/api/provenance`, dto, { responseType: 'text' });
  // }

  // snapshots
  snapshots(studyId: number): Observable<DatasetSnapshot[]> {
    return this.http.get<DatasetSnapshot[]>(`${this.base}/api/dataset-snapshot`, {
      params: new HttpParams().set('studyId', studyId)
    });
  }
  createSnapshot(studyId: number): Observable<DatasetSnapshot> {
    return this.http.post<DatasetSnapshot>(`${this.base}/api/dataset-snapshot`, null, {
      params: new HttpParams().set('studyId', studyId)
    });
  }

  // audit packages
  packages(studyId: number): Observable<AuditPackage[]> {
    return this.http.get<AuditPackage[]>(`${this.base}/api/audit-packages`, {
      params: new HttpParams().set('studyId', studyId)
    });
  }
  createPackage(dto: AuditPackageDTO): Observable<AuditPackage> {
    return this.http.post<AuditPackage>(`${this.base}/api/audit-packages`, dto);
  }
  downloadPackage(id: number): Observable<Blob> {
    return this.http.get(`${this.base}/api/audit-packages/download/${id}`, { responseType: 'blob' });
  }
}

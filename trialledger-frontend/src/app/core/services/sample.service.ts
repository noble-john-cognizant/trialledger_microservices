import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  SampleRequestDTO, SampleResponseDTO, SampleStatus,
  ChainOfCustodyRequestDTO, ChainOfCustodyResponseDTO,
  AssayRunRequestDTO, AssayRunResponseDTO,
  SampleStorageRequestDTO, SampleStorageResponseDTO,
  SampleStatsDTO
} from '../models/sample.models';

@Injectable({ providedIn: 'root' })
export class SampleService {
  private http = inject(HttpClient);
  private base = `${environment.apiBase}/api/samples`;

  list(): Observable<SampleResponseDTO[]> { return this.http.get<SampleResponseDTO[]>(this.base); }
  get(id: number): Observable<SampleResponseDTO> { return this.http.get<SampleResponseDTO>(`${this.base}/${id}`); }
  full(id: number): Observable<any> { return this.http.get(`${this.base}/${id}/full`); }
  byParticipant(id: number): Observable<SampleResponseDTO[]> {
    return this.http.get<SampleResponseDTO[]>(`${this.base}/participant/${id}`);
  }
  byStudy(studyId: number): Observable<SampleResponseDTO[]> {
    return this.http.get<SampleResponseDTO[]>(`${this.base}/study/${studyId}`);
  }
  byStatus(status: SampleStatus): Observable<SampleResponseDTO[]> {
    return this.http.get<SampleResponseDTO[]>(`${this.base}/status/${status}`);
  }
  stats(studyId: number): Observable<SampleStatsDTO> {
    return this.http.get<SampleStatsDTO>(`${this.base}/stats/${studyId}`);
  }
  create(dto: SampleRequestDTO): Observable<SampleResponseDTO> {
    return this.http.post<SampleResponseDTO>(this.base, dto);
  }
  updateStatus(sampleId: number, status: SampleStatus): Observable<SampleResponseDTO> {
    return this.http.patch<SampleResponseDTO>(`${this.base}/${sampleId}/status`, null, {
      params: { status }
    });
  }

  // chain of custody
  custodyForSample(sampleId: number): Observable<ChainOfCustodyResponseDTO[]> {
    return this.http.get<ChainOfCustodyResponseDTO[]>(`${this.base}/custody/sample/${sampleId}`);
  }
  latestCustody(sampleId: number): Observable<ChainOfCustodyResponseDTO> {
    return this.http.get<ChainOfCustodyResponseDTO>(`${this.base}/custody/sample/${sampleId}/custody/latest`);
  }
  addCustody(dto: ChainOfCustodyRequestDTO): Observable<ChainOfCustodyResponseDTO> {
    return this.http.post<ChainOfCustodyResponseDTO>(`${this.base}/custody`, dto);
  }

  // assays
  assays(): Observable<AssayRunResponseDTO[]> { return this.http.get<AssayRunResponseDTO[]>(`${this.base}/assays`); }
  assaysForSample(id: number): Observable<AssayRunResponseDTO[]> {
    return this.http.get<AssayRunResponseDTO[]>(`${this.base}/assays/sample/${id}`);
  }
  createAssay(dto: AssayRunRequestDTO): Observable<AssayRunResponseDTO> {
    return this.http.post<AssayRunResponseDTO>(`${this.base}/assays`, dto);
  }

  downloadAssayResult(assayId: number): Observable<Blob> {
    return this.http.get(`${this.base}/assays/${assayId}/result`, {
      responseType: 'blob'
    });
  }

  // storage
  storageHistory(sampleId: number): Observable<SampleStorageResponseDTO[]> {
    return this.http.get<SampleStorageResponseDTO[]>(`${this.base}/${sampleId}/storage`);
  }
  storeSample(sampleId: number, dto: SampleStorageRequestDTO): Observable<SampleStorageResponseDTO> {
    return this.http.post<SampleStorageResponseDTO>(`${this.base}/${sampleId}/storage`, dto);
  }
  retrieveStorage(storageId: number): Observable<SampleStorageResponseDTO> {
    return this.http.get<SampleStorageResponseDTO>(`${this.base}/storage/${storageId}/retrieve`);
  }
}

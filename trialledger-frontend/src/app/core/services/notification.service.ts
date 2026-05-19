import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  NotificationRequestDTO, NotificationResponseDTO, NotificationCategory
} from '../models/notification.models';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private http = inject(HttpClient);
  private base = `${environment.apiBase}/api/notifications`;

  list(): Observable<NotificationResponseDTO[]> {
    return this.http.get<NotificationResponseDTO[]>(this.base);
  }
  get(id: number): Observable<NotificationResponseDTO> {
    return this.http.get<NotificationResponseDTO>(`${this.base}/${id}`);
  }
  byUser(userId: number): Observable<NotificationResponseDTO[]> {
    return this.http.get<NotificationResponseDTO[]>(`${this.base}/user/${userId}`);
  }
  unreadForUser(userId: number): Observable<NotificationResponseDTO[]> {
    return this.http.get<NotificationResponseDTO[]>(`${this.base}/user/${userId}/unread`);
  }
  byCategory(category: NotificationCategory): Observable<NotificationResponseDTO[]> {
    return this.http.get<NotificationResponseDTO[]>(`${this.base}/category/${category}`);
  }
  create(dto: NotificationRequestDTO): Observable<NotificationResponseDTO> {
    return this.http.post<NotificationResponseDTO>(this.base, dto);
  }
  markRead(id: number): Observable<NotificationResponseDTO> {
    return this.http.put<NotificationResponseDTO>(`${this.base}/${id}/read`, null);
  }
  markAllRead(userId: number): Observable<string> {
    return this.http.put(`${this.base}/user/${userId}/read-all`, null, { responseType: 'text' });
  }
  delete(id: number): Observable<string> {
    return this.http.delete(`${this.base}/${id}`, { responseType: 'text' });
  }
}

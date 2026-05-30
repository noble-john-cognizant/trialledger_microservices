import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthService } from '../auth/auth.service';
import { NotificationResponseDTO } from '../models/notification.models';

/**
 * Single source of truth for notifications.
 *
 *  - HTTP methods talk to the backend.
 *  - Signals (`list`, `loading`, `unreadCount`) drive the topbar badge
 *    and the notifications page.
 *  - `refresh()` is called explicitly wherever notifications are shown
 *    (shell on mount, notifications page on open). No polling.
 */
@Injectable({ providedIn: 'root' })
export class NotificationService {
  private http = inject(HttpClient);
  private auth = inject(AuthService);
  private base = `${environment.apiBase}/api/notifications`;

  list = signal<NotificationResponseDTO[]>([]);
  loading = signal(false);
  unreadCount = computed(() => this.list().filter(n => n.status === 'UNREAD').length);

  // ---------- HTTP ----------
  byUser(userId: number): Observable<NotificationResponseDTO[]> {
    return this.http.get<NotificationResponseDTO[]>(`${this.base}/user/${userId}`);
  }

  listNotification(): Observable<NotificationResponseDTO[]> {
    return this.http.get<NotificationResponseDTO[]>(this.base);
  }

  get(id: number): Observable<NotificationResponseDTO> {
    return this.http.get<NotificationResponseDTO>(`${this.base}/${id}`);
  }
  // ---------- Reactive state ----------
  refresh() {
    const u = this.auth.user();
    if (!u) { this.list.set([]); return; }
    this.loading.set(true);

    const role = this.auth.role();
    const isAdminOrCompliance = role === 'ADMIN' || role === 'COMPLIANCE';
    const fetch$ = isAdminOrCompliance ? this.listNotification() : 
    this.byUser(u.userId);
    fetch$.subscribe({
      next: v => { this.list.set(v ?? []);
         this.loading.set(false); },
      error: () => this.loading.set(false)
    });
  }

  markRead(id: number) {
    this.list.update(arr =>
      arr.map(n => n.notificationId === id ? { ...n, status: 'READ' } : n)
    );
    this.http.put(`${this.base}/${id}/read`, null).subscribe({ error: () => this.refresh() });
  }

  markAllRead() {
    const u = this.auth.user();
    if (!u) return;
    this.list.update(arr => arr.map(n => ({ ...n, status: 'READ' as const })));
    this.http.put(`${this.base}/user/${u.userId}/read-all`, null, { responseType: 'text' })
      .subscribe({ error: () => this.refresh() });
  }
}

import { Injectable, computed, inject, signal } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { NotificationService } from './notification.service';
import { NotificationResponseDTO } from '../models/notification.models';

/**
 * App-wide reactive state for notifications. Both the topbar badge and the
 * notifications page subscribe to the same signals here so a "mark as read"
 * in one place instantly updates the badge in the other. A polling loop
 * also refreshes from the server every minute so newly-created notifications
 * surface without a page reload.
 */
@Injectable({ providedIn: 'root' })
export class NotificationStateService {
  private auth = inject(AuthService);
  private api = inject(NotificationService);

  /** All notifications for the current user. */
  list = signal<NotificationResponseDTO[]>([]);

  /** Derived unread count — drives the topbar bubble. */
  unreadCount = computed(() => this.list().filter(n => n.status === 'UNREAD').length);

  private pollHandle: ReturnType<typeof setInterval> | null = null;

  /** Load + start polling for the currently logged-in user. */
  start(intervalMs = 60_000) {
    this.stop();
    this.refresh();
    this.pollHandle = setInterval(() => this.refresh(), intervalMs);
  }

  stop() {
    if (this.pollHandle) {
      clearInterval(this.pollHandle);
      this.pollHandle = null;
    }
  }

  /** Force a fresh fetch — call after creating an entity that may trigger an alert. */
  refresh() {
    const u = this.auth.user();
    if (!u) { this.list.set([]); return; }
    this.api.byUser(u.userId).subscribe({
      next: v => this.list.set(v ?? []),
      error: () => { /* leave existing list intact on transient failure */ }
    });
  }

  /** Optimistically flip a single notification to READ then sync with server. */
  markRead(notificationId: number) {
    this.list.update(arr =>
      arr.map(n => n.notificationId === notificationId ? { ...n, status: 'READ' } : n)
    );
    this.api.markRead(notificationId).subscribe({ error: () => this.refresh() });
  }

  /** Mark every notification read for the current user, server + local. */
  markAllRead() {
    const u = this.auth.user();
    if (!u) return;
    this.list.update(arr => arr.map(n => ({ ...n, status: 'READ' as const })));
    this.api.markAllRead(u.userId).subscribe({ error: () => this.refresh() });
  }

  reset() {
    this.stop();
    this.list.set([]);
  }
}

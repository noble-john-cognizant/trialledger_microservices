import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { NotificationService } from '../../../core/services/notification.service';
import { NotificationStateService } from '../../../core/services/notification-state.service';
import { AuthService } from '../../../core/auth/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import {
  NotificationCategory, NotificationResponseDTO, ALL_NOTIFICATION_CATEGORIES
} from '../../../core/models/notification.models';
import { EmptyStateComponent } from '../../../shared/empty-state/empty-state.component';

@Component({
  selector: 'tl-notifications-list',
  standalone: true,
  imports: [CommonModule, DatePipe, EmptyStateComponent],
  templateUrl: './notifications-list.component.html',
  styleUrls: ['./notifications-list.component.css']
})
export class NotificationsListComponent implements OnInit {
  private api = inject(NotificationService);
  private state = inject(NotificationStateService);
  private toast = inject(ToastService);
  private auth = inject(AuthService);

  categories = ALL_NOTIFICATION_CATEGORIES;
  categoryFilter = signal<NotificationCategory | ''>('');

  /** When ADMIN/COMPLIANCE views this page we additionally show the
   * full system-wide list under a separate signal. */
  systemList = signal<NotificationResponseDTO[]>([]);

  /** Pull from the shared state for "my notifications". */
  myList = this.state.list;

  /** Either my list or the system-wide list, depending on role. */
  displayList = computed(() =>
    this.isAdminOrCompliance ? this.systemList() : this.myList()
  );

  filtered = computed(() => {
    const c = this.categoryFilter();
    const list = this.displayList();
    return c ? list.filter(n => n.category === c) : list;
  });
  unreadCount = computed(() => this.displayList().filter(n => n.status === 'UNREAD').length);

  private get isAdminOrCompliance(): boolean {
    const role = this.auth.role();
    return role === 'ADMIN' || role === 'COMPLIANCE';
  }

  ngOnInit() {
    if (this.isAdminOrCompliance) {
      this.api.list().subscribe({
        next: v => this.systemList.set(v ?? []),
        error: () => this.systemList.set([])
      });
    } else {
      this.state.refresh();
    }
  }

  markRead(n: NotificationResponseDTO) {
    if (this.isAdminOrCompliance) {
      // System-wide view — update local copy and call the API directly.
      this.systemList.update(arr =>
        arr.map(x => x.notificationId === n.notificationId ? { ...x, status: 'READ' } : x)
      );
      this.api.markRead(n.notificationId).subscribe({
        error: () => this.api.list().subscribe(v => this.systemList.set(v ?? []))
      });
    } else {
      // Personal view — shared state handles the topbar badge automatically.
      this.state.markRead(n.notificationId);
    }
  }

  markAll() {
    const u = this.auth.user();
    if (!u) return;

    if (this.isAdminOrCompliance) {
      this.systemList.update(arr => arr.map(n => ({ ...n, status: 'READ' as const })));
      this.api.markAllRead(u.userId).subscribe({
        next: () => this.toast.success('All marked read'),
        error: () => this.api.list().subscribe(v => this.systemList.set(v ?? []))
      });
    } else {
      this.state.markAllRead();
      this.toast.success('All marked read');
    }
  }

  catBadge(c: string): string {
    switch (c) {
      case 'CONSENT': return 'bg-soft-primary';
      case 'SAMPLE':  return 'bg-soft-warning';
      case 'VISIT':   return 'bg-soft-success';
      case 'AE':      return 'bg-soft-danger';
      default:        return 'bg-soft-secondary';
    }
  }
}

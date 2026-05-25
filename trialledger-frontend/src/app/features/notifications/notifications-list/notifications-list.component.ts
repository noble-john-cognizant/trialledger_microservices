// import { Component, OnInit, computed, inject, signal } from '@angular/core';
// import { CommonModule, DatePipe } from '@angular/common';
// import { NotificationService } from '../../../core/services/notification.service';
// import { AuthService } from '../../../core/auth/auth.service';
// import { ToastService } from '../../../core/services/toast.service';
// import {
//   NotificationCategory, NotificationResponseDTO, ALL_NOTIFICATION_CATEGORIES
// } from '../../../core/models/notification.models';
// import { EmptyStateComponent } from '../../../shared/empty-state/empty-state.component';
//
// @Component({
//   selector: 'tl-notifications-list',
//   standalone: true,
//   imports: [CommonModule, DatePipe, EmptyStateComponent],
//   templateUrl: './notifications-list.component.html',
//   styleUrls: ['./notifications-list.component.css']
// })
// export class NotificationsListComponent implements OnInit {
//   private api = inject(NotificationService);
//   private toast = inject(ToastService);
//   private auth = inject(AuthService);
//
//   categories = ALL_NOTIFICATION_CATEGORIES;
//   list = signal<NotificationResponseDTO[]>([]);
//   categoryFilter = signal<NotificationCategory | ''>('');
//
//   filtered = computed(() => {
//     const c = this.categoryFilter();
//     return c ? this.list().filter(n => n.category === c) : this.list();
//   });
//   unreadCount = computed(() => this.list().filter(n => n.status === 'UNREAD').length);
//
//   ngOnInit() { this.load(); }
//
//   load() {
//     const u = this.auth.user(); if (!u) return;
//     this.api.byUser(u.userId).subscribe(v => this.list.set(v ?? []));
//   }
//
//   markRead(n: NotificationResponseDTO) {
//     this.api.markRead(n.notificationId).subscribe({
//       next: () => this.list.update(arr =>
//         arr.map(x => x.notificationId === n.notificationId ? { ...x, status: 'READ' } : x))
//     });
//   }
//   markAll() {
//     const u = this.auth.user(); if (!u) return;
//     this.api.markAllRead(u.userId).subscribe({
//       next: () => { this.toast.success('All marked read'); this.load(); }
//     });
//   }
//
//   catBadge(c: string): string {
//     switch (c) {
//       case 'CONSENT': return 'bg-soft-primary';
//       case 'SAMPLE':  return 'bg-soft-warning';
//       case 'VISIT':   return 'bg-soft-success';
//       case 'AE':      return 'bg-soft-danger';
//       default:        return 'bg-soft-secondary';
//     }
//   }
// }








import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { NotificationService } from '../../../core/services/notification.service';
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
  private toast = inject(ToastService);
  private auth = inject(AuthService);

  categories = ALL_NOTIFICATION_CATEGORIES;
  list = signal<NotificationResponseDTO[]>([]);
  categoryFilter = signal<NotificationCategory | ''>('');

  filtered = computed(() => {
    const c = this.categoryFilter();
    return c ? this.list().filter(n => n.category === c) : this.list();
  });
  unreadCount = computed(() => this.list().filter(n => n.status === 'UNREAD').length);

  /** True for roles that should see ALL notifications system-wide */
  private get isAdminOrCompliance(): boolean {
    const role = this.auth.role();
    return role === 'ADMIN' || role === 'COMPLIANCE';
  }

  ngOnInit() { this.load(); }

  load() {
    const u = this.auth.user();
    if (!u) return;

    if (this.isAdminOrCompliance) {
      // ADMIN and COMPLIANCE see all notifications system-wide
      this.api.list().subscribe(v => this.list.set(v ?? []));
    } else {
      // All other roles (COORDINATOR, PARTICIPANT, PI, etc.) see only their own
      this.api.byUser(u.userId).subscribe(v => this.list.set(v ?? []));
    }
  }

  markRead(n: NotificationResponseDTO) {
    this.api.markRead(n.notificationId).subscribe({
      next: () => this.list.update(arr =>
        arr.map(x => x.notificationId === n.notificationId ? { ...x, status: 'READ' } : x))
    });
  }

  markAll() {
    const u = this.auth.user();
    if (!u) return;

    if (this.isAdminOrCompliance) {
      // For admin/compliance viewing all notifications, mark all as read via bulk load
      this.api.markAllRead(u.userId).subscribe({
        next: () => { this.toast.success('All marked read'); this.load(); }
      });
    } else {
      this.api.markAllRead(u.userId).subscribe({
        next: () => { this.toast.success('All marked read'); this.load(); }
      });
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

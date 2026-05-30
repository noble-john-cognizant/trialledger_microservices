import { Component, OnInit, inject } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { NotificationService } from '../../../core/services/notification.service';
import { ToastService } from '../../../core/services/toast.service';
import { NotificationResponseDTO } from '../../../core/models/notification.models';
import { SpinnerComponent } from '../../../shared/spinner/spinner.component';

@Component({
  selector: 'tl-notifications-list',
  standalone: true,
  imports: [CommonModule, DatePipe, SpinnerComponent],
  templateUrl: './notifications-list.component.html',
  styleUrls: ['./notifications-list.component.css']
})
export class NotificationsListComponent implements OnInit {
  private notif = inject(NotificationService);
  private toast = inject(ToastService);

  list = this.notif.list;
  loading = this.notif.loading;
  unreadCount = this.notif.unreadCount;

  ngOnInit() {
    this.notif.refresh();
  }

  markRead(n: NotificationResponseDTO) {
    if (n.status === 'READ') return;
    this.notif.markRead(n.notificationId);
  }

  markAll() {
    this.notif.markAllRead();
    this.toast.success('All marked read');
  }
}

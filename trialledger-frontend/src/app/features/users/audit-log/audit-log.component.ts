import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule, DatePipe, JsonPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuditService } from '../../../core/services/audit.service';
import { AuditLogDTO } from '../../../core/models/audit.models';
import { ToastService } from '../../../core/services/toast.service';
import { EmptyStateComponent } from '../../../shared/empty-state/empty-state.component';

@Component({
  selector: 'tl-audit-log',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe, JsonPipe, EmptyStateComponent],
  templateUrl: './audit-log.component.html',
  styleUrls: ['./audit-log.component.css']
})
export class AuditLogComponent implements OnInit {
  private api = inject(AuditService);
  private toast = inject(ToastService);

  mode = signal<'all' | 'action' | 'user' | 'range'>('all');
  actionFilter = signal('');
  userIdFilter = signal<number | null>(null);
  from = signal('');
  to = signal('');

  entries = signal<AuditLogDTO[]>([]);

  ngOnInit() { this.run(); }

  run() {
    const m = this.mode();
    const ok = (v: AuditLogDTO[]) => this.entries.set(v ?? []);
    const err = () => this.toast.error('Failed to load audit log');
    if (m === 'all') this.api.list().subscribe({ next: ok, error: err });
    else if (m === 'action' && this.actionFilter()) this.api.byAction(this.actionFilter()).subscribe({ next: ok, error: err });
    else if (m === 'user' && this.userIdFilter()) this.api.byUser(this.userIdFilter()!).subscribe({ next: ok, error: err });
    else if (m === 'range' && this.from() && this.to()) this.api.byRange(this.from(), this.to()).subscribe({ next: ok, error: err });
  }
}

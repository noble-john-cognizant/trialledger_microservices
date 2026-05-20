import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule, DatePipe, JsonPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuditService } from '../../../core/services/audit.service';
import { AuditLogDTO } from '../../../core/models/audit.models';
import { ToastService } from '../../../core/services/toast.service';
import { EmptyStateComponent } from '../../../shared/empty-state/empty-state.component';

const PAGE_SIZE = 20;

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

  /** Full result set returned by the chosen endpoint */
  all = signal<AuditLogDTO[]>([]);

  /** Current page (0-indexed) */
  page = signal(0);
  pageSize = PAGE_SIZE;

  totalPages = computed(() => Math.max(1, Math.ceil(this.all().length / this.pageSize)));

  /** Items visible on the current page */
  pageItems = computed(() => {
    const start = this.page() * this.pageSize;
    return this.all().slice(start, start + this.pageSize);
  });

  /** Page numbers to render (windowed if many pages) */
  pageNumbers = computed(() => {
    const tp = this.totalPages();
    const cur = this.page();
    if (tp <= 7) return Array.from({ length: tp }, (_, i) => i);
    const window: number[] = [];
    const start = Math.max(0, Math.min(cur - 2, tp - 5));
    for (let i = start; i < start + 5; i++) window.push(i);
    return window;
  });

  ngOnInit() { this.run(); }

  /** Reset to page 0 whenever the query changes */
  run() {
    this.page.set(0);
    const m = this.mode();
    const ok = (v: AuditLogDTO[]) => this.all.set(this.sortDesc(v ?? []));
    const err = () => { this.all.set([]); this.toast.error('Failed to load audit log'); };

    if (m === 'all') this.api.list().subscribe({ next: ok, error: err });
    else if (m === 'action' && this.actionFilter()) this.api.byAction(this.actionFilter()).subscribe({ next: ok, error: err });
    else if (m === 'user' && this.userIdFilter()) this.api.byUser(this.userIdFilter()!).subscribe({ next: ok, error: err });
    else if (m === 'range' && this.from() && this.to()) this.api.byRange(this.from(), this.to()).subscribe({ next: ok, error: err });
  }

  private sortDesc(items: AuditLogDTO[]): AuditLogDTO[] {
    return [...items].sort((a, b) =>
      new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime()
    );
  }

  go(p: number) {
    if (p < 0 || p >= this.totalPages()) return;
    this.page.set(p);
  }
}

import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule, DatePipe, JsonPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuditService } from '../../../core/services/audit.service';
import { AuditLogDTO, PageResponse } from '../../../core/models/audit.models';
import { SpinnerComponent } from '../../../shared/spinner/spinner.component';
import { extractErrorMessage } from '../../../core/utils/error-message';

const PAGE_SIZE = 20;

/**
 * Server-paginated audit log. The backend returns a Spring Page<AuditLogDTO>
 * envelope; we just bind the `content` array and re-fetch when the page
 * number or filter changes. No client-side slicing or sorting.
 */
@Component({
  selector: 'tl-audit-log',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe, JsonPipe, SpinnerComponent],
  templateUrl: './audit-log.component.html',
  styleUrls: ['./audit-log.component.css']
})
export class AuditLogComponent implements OnInit {
  private api = inject(AuditService);

  mode = signal<'all' | 'action' | 'user' | 'range'>('all');
  actionFilter = signal('');
  userIdFilter = signal<number | null>(null);
  from = signal('');
  to = signal('');

  /** The page envelope from the server. Reset on every fetch. */
  page = signal<PageResponse<AuditLogDTO> | null>(null);
  pageSize = PAGE_SIZE;

  loading = signal(true);
  error = signal<string | null>(null);

  items       = computed(() => this.page()?.content ?? []);
  pageNumber  = computed(() => this.page()?.number ?? 0);
  totalPages  = computed(() => this.page()?.totalPages ?? 0);
  totalCount  = computed(() => this.page()?.totalElements ?? 0);

  /** Page numbers to render (windowed when there are many). */
  pageNumbers = computed(() => {
    const tp = this.totalPages();
    const cur = this.pageNumber();
    if (tp <= 7) return Array.from({ length: tp }, (_, i) => i);
    const start = Math.max(0, Math.min(cur - 2, tp - 5));
    return Array.from({ length: 5 }, (_, i) => start + i);
  });

  ngOnInit() { this.fetch(0); }

  /** Reset to page 0 whenever the query changes. */
  run() { this.fetch(0); }

  /** Pagination click — re-fetches that page from the server. */
  go(p: number) {
    if (p < 0 || p >= this.totalPages()) return;
    this.fetch(p);
  }

  load() { this.fetch(this.pageNumber()); }

  private fetch(p: number) {
    this.loading.set(true);
    this.error.set(null);

    const ok = (res: PageResponse<AuditLogDTO>) => { this.page.set(res); this.loading.set(false); };
    const err = (e: unknown) => {
      this.page.set(null);
      this.error.set(extractErrorMessage(e, 'Could not load audit log.'));
      this.loading.set(false);
    };

    const m = this.mode();
    if (m === 'all') {
      this.api.list(p, this.pageSize).subscribe({ next: ok, error: err });
    } else if (m === 'action' && this.actionFilter()) {
      this.api.byAction(this.actionFilter(), p, this.pageSize).subscribe({ next: ok, error: err });
    } else if (m === 'user' && this.userIdFilter()) {
      this.api.byUser(this.userIdFilter()!, p, this.pageSize).subscribe({ next: ok, error: err });
    } else if (m === 'range' && this.from() && this.to()) {
      this.api.byRange(this.from(), this.to(), p, this.pageSize).subscribe({ next: ok, error: err });
    } else {
      this.loading.set(false);
    }
  }
}

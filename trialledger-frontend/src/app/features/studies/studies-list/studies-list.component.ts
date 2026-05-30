import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { StudyService } from '../../../core/services/study.service';
import { ToastService } from '../../../core/services/toast.service';
import { AuthService } from '../../../core/auth/auth.service';
import { extractErrorMessage } from '../../../core/utils/error-message';
import {
  StudyResponseDto, StudyStatus, ALL_STUDY_STATUSES
} from '../../../core/models/study.models';
import { StatusBadgeComponent } from '../../../shared/status-badge/status-badge.component';
import { ModalComponent } from '../../../shared/modal/modal.component';
import { SpinnerComponent } from '../../../shared/spinner/spinner.component';

@Component({
  selector: 'tl-studies-list',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule, DatePipe,
    StatusBadgeComponent, ModalComponent, SpinnerComponent],
  templateUrl: './studies-list.component.html',
  styleUrls: ['./studies-list.component.css']
})
export class StudiesListComponent implements OnInit {
  private api = inject(StudyService);
  private toast = inject(ToastService);
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);

  statuses = ALL_STUDY_STATUSES;
  list = signal<StudyResponseDto[]>([]);
  search = signal('');
  statusFilter = signal('');
  createOpen = signal(false);
  loading = signal(true);
  error = signal<string | null>(null);

  canCreate = computed(() => this.auth.can('STUDY_CREATE'));
  canManage = computed(() => this.auth.can('STUDY_MANAGE'));
  canDelete = computed(() => this.auth.can('STUDY_DELETE'));

  filtered = computed(() => {
    const s = this.search().toLowerCase();
    const st = this.statusFilter();
    return this.list().filter(x =>
      (!st || x.status === st) &&
      (!s || x.title.toLowerCase().includes(s) || x.sponsor.toLowerCase().includes(s))
    );
  });

  form = this.fb.nonNullable.group({
    title: ['', Validators.required],
    sponsor: ['', Validators.required],
    protocolNumber: ['', Validators.required],
    startDate: ['', Validators.required],
    endDate: ['', Validators.required]
  });

  ngOnInit() { this.load(); }
  load() {
    this.loading.set(true);
    this.error.set(null);
    this.api.list().subscribe({
      next: v => { this.list.set(v ?? []); this.loading.set(false); },
      error: e => { this.error.set(extractErrorMessage(e, 'Could not load studies.')); this.loading.set(false); }
    });
  }

  openCreate() {
    this.form.reset({ title: '', sponsor: '', protocolNumber: '', startDate: '', endDate: '' });
    this.createOpen.set(true);
  }

  submit() {
    if (this.form.invalid) return;
    this.api.create(this.form.getRawValue()).subscribe({
      next: () => { this.toast.success('Study created'); this.createOpen.set(false); this.load(); }
    });
  }

  onStatusChange(s: StudyResponseDto, status: string) {
    if (!status) return;
    this.api.updateStatus(s.studyId, status as StudyStatus).subscribe({
      next: () => { this.toast.success('Status updated'); this.load(); }
    });
  }

  del(s: StudyResponseDto) {
    if (!confirm(`Delete "${s.title}"? This cannot be undone.`)) return;
    this.api.delete(s.studyId).subscribe({
      next: () => { this.toast.success('Deleted'); this.load(); }
    });
  }
}

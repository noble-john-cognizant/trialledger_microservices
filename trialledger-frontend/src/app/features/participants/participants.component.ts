import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ParticipantService } from '../../core/services/participant.service';
import { StudyService } from '../../core/services/study.service';
import { AuthService } from '../../core/auth/auth.service';
import { ToastService } from '../../core/services/toast.service';
import {
  EnrollmentStatus, ParticipantResponseDTO, ALL_ENROLLMENT_STATUSES
} from '../../core/models/participant.models';
import { StudyResponseDto } from '../../core/models/study.models';
import { StatusBadgeComponent } from '../../shared/status-badge/status-badge.component';
import { ModalComponent } from '../../shared/modal/modal.component';
import { EmptyStateComponent } from '../../shared/empty-state/empty-state.component';

@Component({
  selector: 'tl-participants',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule, DatePipe, StatusBadgeComponent, ModalComponent, EmptyStateComponent],
  templateUrl: './participants.component.html',
  styleUrls: ['./participants.component.css']
})
export class ParticipantsComponent implements OnInit {
  private api = inject(ParticipantService);
  private studyApi = inject(StudyService);
  private toast = inject(ToastService);
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);

  statuses = ALL_ENROLLMENT_STATUSES;
  list = signal<ParticipantResponseDTO[]>([]);
  studies = signal<StudyResponseDto[]>([]);
  search = signal('');
  statusFilter = signal('');
  studyFilter = signal('');
  open = signal(false);

  canCreate = computed(() => this.auth.can('PARTICIPANT_CREATE'));
  canUpdate = computed(() => this.auth.can('PARTICIPANT_UPDATE'));
  canViewConsent = computed(() => this.auth.can('CONSENT_VIEW'));
  canViewVisits = computed(() => this.auth.can('VISIT_VIEW'));

  filtered = computed(() => {
    const s = this.search().toLowerCase();
    const st = this.statusFilter();
    const sd = this.studyFilter();
    return this.list().filter(p =>
      (!st || p.enrollmentStatus === st) &&
      (!sd || String(p.studyId) === sd) &&
      (!s || p.name.toLowerCase().includes(s) || p.externalId.toLowerCase().includes(s))
    );
  });

  form = this.fb.nonNullable.group({
    studyId: this.fb.control<number | null>(null, { validators: [Validators.required] }),
    externalId: ['', Validators.required],
    name: ['', Validators.required],
    dob: ['', Validators.required],
    contactInfo: ['', Validators.required]
  });

  ngOnInit() {
    this.load();
    if (this.auth.can('STUDY_LIST')) {
      this.studyApi.list().subscribe(s => this.studies.set(s ?? []));
    }
  }

  load() { this.api.list().subscribe({ next: v => this.list.set(v ?? []) }); }
  studyTitle(id: number) { return this.studies().find(s => s.studyId === id)?.title || `Study #${id}`; }

  openCreate() {
    this.form.reset({ studyId: null, externalId: '', name: '', dob: '', contactInfo: '' });
    this.open.set(true);
  }

  submit() {
    if (this.form.invalid) return;
    this.api.create(this.form.getRawValue() as any).subscribe({
      next: () => { this.toast.success('Enrolled'); this.open.set(false); this.load(); },
      error: e => this.toast.error(e?.error?.message ?? 'Failed')
    });
  }

  updateStatus(p: ParticipantResponseDTO, status: string) {
    if (!status) return;
    this.api.updateStatus(p.participantId, status as EnrollmentStatus).subscribe({
      next: () => { this.toast.success('Updated'); this.load(); }
    });
  }
}

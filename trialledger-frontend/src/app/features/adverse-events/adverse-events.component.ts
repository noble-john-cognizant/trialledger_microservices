import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AdverseEventService } from '../../core/services/adverse-event.service';
import { ParticipantService } from '../../core/services/participant.service';
import { StudyService } from '../../core/services/study.service';
import { AuthService } from '../../core/auth/auth.service';
import { ToastService } from '../../core/services/toast.service';
import {
  AdverseEventResponseDto, AEStatus, Severity,
  AEFollowUpResponseDto, ALL_SEVERITIES, ALL_AE_STATUSES
} from '../../core/models/adverse-event.models';
import { ParticipantResponseDTO } from '../../core/models/participant.models';
import { StudyResponseDto } from '../../core/models/study.models';
import { StatusBadgeComponent } from '../../shared/status-badge/status-badge.component';
import { ModalComponent } from '../../shared/modal/modal.component';
import { EmptyStateComponent } from '../../shared/empty-state/empty-state.component';

@Component({
  selector: 'tl-adverse-events',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, DatePipe, StatusBadgeComponent, ModalComponent, EmptyStateComponent],
  templateUrl: './adverse-events.component.html',
  styleUrls: ['./adverse-events.component.css']
})
export class AdverseEventsComponent implements OnInit {
  private api = inject(AdverseEventService);
  private partApi = inject(ParticipantService);
  private studyApi = inject(StudyService);
  private toast = inject(ToastService);
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);

  severities = ALL_SEVERITIES;
  statuses = ALL_AE_STATUSES;
  list = signal<AdverseEventResponseDto[]>([]);
  participants = signal<ParticipantResponseDTO[]>([]);
  studies = signal<StudyResponseDto[]>([]);

  search = signal('');
  severityFilter = signal('');
  statusFilter = signal('');

  reportOpen = signal(false);
  detailOpen = signal(false);
  followOpen = signal(false);

  selected = signal<AdverseEventResponseDto | null>(null);
  followUps = signal<AEFollowUpResponseDto[]>([]);

  canReport = computed(() => this.auth.can('AE_CREATE'));
  canManage = computed(() => this.auth.can('AE_MANAGE'));
  canDelete = computed(() => this.auth.can('AE_DELETE'));
  canFollowUp = computed(() => this.auth.can('AE_FOLLOWUP_CREATE'));
  canViewFollowUp = computed(() => this.auth.can('AE_FOLLOWUP_VIEW'));

  filtered = computed(() => {
    const q = this.search().toLowerCase();
    return this.list().filter(ae =>
      (!this.severityFilter() || ae.severity === this.severityFilter()) &&
      (!this.statusFilter() || ae.status === this.statusFilter()) &&
      (!q || ae.description.toLowerCase().includes(q))
    );
  });

  form = this.fb.nonNullable.group({
    participantId: this.fb.control<number | null>(null, { validators: [Validators.required] }),
    studyId: this.fb.control<number | null>(null, { validators: [Validators.required] }),
    severity: ['MODERATE' as Severity, Validators.required],
    description: ['', Validators.required],
    reportedById: [this.auth.user()?.userId ?? 0]
  });
  followForm = this.fb.nonNullable.group({
    actionTaken: ['', Validators.required],
    notes: [''],
    performedById: [this.auth.user()?.userId ?? 0]
  });

  ngOnInit() {
    this.api.list().subscribe(v => this.list.set(v ?? []));
    if (this.auth.can('PARTICIPANT_LIST')) this.partApi.list().subscribe(v => this.participants.set(v ?? []));
    if (this.auth.can('STUDY_LIST')) this.studyApi.list().subscribe(v => this.studies.set(v ?? []));
  }

  openReport() {
    this.form.reset({
      participantId: null, studyId: null,
      severity: 'MODERATE', description: '',
      reportedById: this.auth.user()?.userId ?? 0
    });
    this.reportOpen.set(true);
  }
  submit() {
    if (this.form.invalid) return;
    this.api.create(this.form.getRawValue() as any).subscribe({
      next: () => {
        this.toast.success('Reported'); this.reportOpen.set(false);
        this.api.list().subscribe(v => this.list.set(v ?? []));
      },
      error: e => this.toast.error(e?.error?.message ?? 'Failed')
    });
  }

  openDetails(ae: AdverseEventResponseDto) {
    this.selected.set(ae);
    this.detailOpen.set(true);
    if (this.canViewFollowUp()) {
      this.api.followUps(ae.aeId).subscribe(v => this.followUps.set(v ?? []));
    }
  }

  updateSeverity(s: string) {
    const ae = this.selected(); if (!ae || !s) return;
    this.api.updateSeverity(ae.aeId, s as Severity).subscribe({
      next: u => {
        this.toast.success('Severity updated'); this.selected.set(u);
        this.api.list().subscribe(v => this.list.set(v ?? []));
      }
    });
  }
  updateStatus(s: string) {
    const ae = this.selected(); if (!ae || !s) return;
    this.api.updateStatus(ae.aeId, s as AEStatus).subscribe({
      next: u => {
        this.toast.success('Status updated'); this.selected.set(u);
        this.api.list().subscribe(v => this.list.set(v ?? []));
      }
    });
  }
  del() {
    const ae = this.selected(); if (!ae) return;
    if (!confirm('Delete this event? It will be soft-deleted.')) return;
    this.api.delete(ae.aeId).subscribe({
      next: () => {
        this.toast.success('Deleted'); this.detailOpen.set(false);
        this.api.list().subscribe(v => this.list.set(v ?? []));
      }
    });
  }

  submitFollow() {
    const ae = this.selected(); if (!ae || this.followForm.invalid) return;
    this.api.addFollowUp(ae.aeId, this.followForm.getRawValue() as any).subscribe({
      next: () => {
        this.toast.success('Follow-up added'); this.followOpen.set(false);
        this.api.followUps(ae.aeId).subscribe(v => this.followUps.set(v ?? []));
      },
      error: e => this.toast.error(e?.error?.message ?? 'Failed')
    });
  }
}

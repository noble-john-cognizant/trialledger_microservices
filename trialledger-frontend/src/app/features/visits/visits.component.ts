import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { VisitService } from '../../core/services/visit.service';
import { SourceDataService } from '../../core/services/source-data.service';
import { ParticipantService } from '../../core/services/participant.service';
import { StudyService } from '../../core/services/study.service';
import { AuthService } from '../../core/auth/auth.service';
import { ToastService } from '../../core/services/toast.service';
import { extractErrorMessage } from '../../core/utils/error-message';
import {
  VisitResponseDto, VisitStatus, VisitType,
  ALL_VISIT_STATUSES, ALL_VISIT_TYPES
} from '../../core/models/visit.models';
import { SourceDataResponseDto } from '../../core/models/source-data.models';
import { ParticipantResponseDTO } from '../../core/models/participant.models';
import { StudyResponseDto } from '../../core/models/study.models';
import { StatusBadgeComponent } from '../../shared/status-badge/status-badge.component';
import { ModalComponent } from '../../shared/modal/modal.component';
import { EmptyStateComponent } from '../../shared/empty-state/empty-state.component';
import { SearchSelectComponent, SearchOption } from '../../shared/search-select/search-select.component';

@Component({
  selector: 'tl-visits',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, DatePipe,
    StatusBadgeComponent, ModalComponent, EmptyStateComponent, SearchSelectComponent],
  templateUrl: './visits.component.html',
  styleUrls: ['./visits.component.css']
})
export class VisitsComponent implements OnInit {
  private api = inject(VisitService);
  private srcApi = inject(SourceDataService);
  private partApi = inject(ParticipantService);
  private studyApi = inject(StudyService);
  private toast = inject(ToastService);
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);

  statuses = ALL_VISIT_STATUSES;
  types = ALL_VISIT_TYPES;

  scope = signal<'participant' | 'study'>('participant');
  participantId = signal<number | null>(null);
  studyId = signal<number | null>(null);

  list = signal<VisitResponseDto[]>([]);
  participants = signal<ParticipantResponseDTO[]>([]);
  studies = signal<StudyResponseDto[]>([]);

  schedOpen = signal(false);
  sourceOpen = signal(false);
  captureOpen = signal(false);

  visit = signal<VisitResponseDto | null>(null);
  sourceData = signal<SourceDataResponseDto[]>([]);

  canSchedule = computed(() => this.auth.can('VISIT_SCHEDULE'));
  canUpdate = computed(() => this.auth.can('VISIT_UPDATE'));
  canDelete = computed(() => this.auth.can('VISIT_DELETE'));
  canCapture = computed(() => this.auth.can('SOURCE_CREATE'));
  canVerify = computed(() => this.auth.can('SOURCE_VERIFY'));
  canViewSource = computed(() => this.auth.can('SOURCE_VIEW'));
  canListParticipants = computed(() => this.auth.can('PARTICIPANT_LIST'));
  canListStudy = computed(() => this.auth.can('STUDY_LIST'));

  participantOptions = computed<SearchOption[]>(() =>
    this.participants().map(p => ({
      id: p.participantId,
      label: p.name,
      subtitle: `${p.externalId} · Study #${p.studyId}`
    }))
  );
  studyOptions = computed<SearchOption[]>(() =>
    this.studies().map(s => ({
      id: s.studyId, label: s.title,
      subtitle: `${s.sponsor} · #${s.protocolNumber}`
    }))
  );

  setParticipant(id: number | null) { this.form.patchValue({ participantId: id }); }
  setStudy(id: number | null)       { this.form.patchValue({ studyId: id }); }

  form = this.fb.nonNullable.group({
    participantId: this.fb.control<number | null>(null, { validators: [Validators.required] }),
    studyId: this.fb.control<number | null>(null, { validators: [Validators.required] }),
    visitType: ['SCREENING' as VisitType, Validators.required],
    scheduledAt: ['', Validators.required]
  });
  sourceForm = this.fb.nonNullable.group({
    dataType: ['', Validators.required],
    dataUri: ['', Validators.required],
    collectedAt: ['', Validators.required]
  });

  ngOnInit() {
    if (this.canListParticipants()) this.partApi.list().subscribe(v => this.participants.set(v ?? []));
    if (this.canListStudy()) this.studyApi.list().subscribe(v => this.studies.set(v ?? []));
    const url = new URL(window.location.href);
    const pid = url.searchParams.get('participantId');
    if (pid) { this.scope.set('participant'); this.participantId.set(+pid); this.load(); }
  }

  participantName(id: number) {
    return this.participants().find(p => p.participantId === id)?.name || `#${id}`;
  }

  load() {
    if (this.scope() === 'participant' && this.participantId()) {
      this.api.byParticipant(this.participantId()!).subscribe({ next: v => this.list.set(v ?? []) });
    } else if (this.scope() === 'study' && this.studyId()) {
      this.api.byStudy(this.studyId()!).subscribe({ next: v => this.list.set(v ?? []) });
    }
  }

  openSchedule() {
    this.form.reset({
      participantId: this.participantId(),
      studyId: this.studyId(),
      visitType: 'SCREENING',
      scheduledAt: ''
    });
    this.schedOpen.set(true);
  }
  submit() {
    if (this.form.invalid) return;
    this.api.schedule(this.form.getRawValue() as any).subscribe({
      next: () => { this.toast.success('Scheduled'); this.schedOpen.set(false); this.load(); },
      error: e => this.toast.error(extractErrorMessage(e, 'Failed'))
    });
  }

  updateStatus(v: VisitResponseDto, status: string) {
    if (!status) return;
    this.api.updateStatus(v.visitId, status as VisitStatus).subscribe({
      next: () => { this.toast.success('Status updated'); this.load(); }
    });
  }
  del(v: VisitResponseDto) {
    if (!confirm('Delete this visit?')) return;
    this.api.delete(v.visitId).subscribe({ next: () => { this.toast.success('Deleted'); this.load(); } });
  }

  openSource(v: VisitResponseDto) {
    this.visit.set(v);
    this.sourceOpen.set(true);
    if (this.canViewSource()) {
      this.srcApi.byVisit(v.visitId).subscribe(s => this.sourceData.set(s ?? []));
    }
  }
  submitSource() {
    const v = this.visit(); if (!v) return;
    const me = this.auth.user();
    this.srcApi.create({
      visitId: v.visitId,
      collectedBy: me?.userId ?? 0,
      ...this.sourceForm.getRawValue()
    }).subscribe({
      next: () => {
        this.toast.success('Captured'); this.captureOpen.set(false);
        this.srcApi.byVisit(v.visitId).subscribe(s => this.sourceData.set(s ?? []));
      },
      error: e => this.toast.error(extractErrorMessage(e, 'Failed'))
    });
  }
  verify(sd: SourceDataResponseDto) {
    this.srcApi.verify(sd.sourceId).subscribe({
      next: ok => ok ? this.toast.success('Hash verified ✓') : this.toast.error('Hash mismatch'),
      error: () => this.toast.error('Verification error')
    });
  }
}

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
  VisitResponseDto,
  VisitStatus,
  VisitType,
  ALL_VISIT_STATUSES,
  ALL_VISIT_TYPES,
} from '../../core/models/visit.models';
import { SourceDataResponseDto } from '../../core/models/source-data.models';
import { ParticipantResponseDTO } from '../../core/models/participant.models';
import { StudyResponseDto } from '../../core/models/study.models';
import { StatusBadgeComponent } from '../../shared/status-badge/status-badge.component';
import { ModalComponent } from '../../shared/modal/modal.component';
import { SpinnerComponent } from '../../shared/spinner/spinner.component';
import { NotificationService } from '../../core/services/notification.service';
import { UserService } from '../../core/services/user.service';

@Component({
  selector: 'tl-visits',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    DatePipe,
    StatusBadgeComponent,
    ModalComponent,
    SpinnerComponent,
  ],
  templateUrl: './visits.component.html',

})
export class VisitsComponent implements OnInit {
  private api = inject(VisitService);
  private srcApi = inject(SourceDataService);
  private partApi = inject(ParticipantService);
  private studyApi = inject(StudyService);
  private toast = inject(ToastService);
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private userApi = inject(UserService);
  private notification = inject(NotificationService);

  statuses = ALL_VISIT_STATUSES;
  types = ALL_VISIT_TYPES;
  isParticipant = computed(() => this.auth.hasRole('PARTICIPANT'));
  scope = signal<'participant' | 'study'>('participant');
  participantId = signal<number | null>(null);
  studyId = signal<number | null>(null);

  list = signal<VisitResponseDto[]>([]);
  participants = signal<ParticipantResponseDTO[]>([]);
  studies = signal<StudyResponseDto[]>([]);
  loading = signal(false);
  error = signal<string | null>(null);

  // ── Modal open flags ──────────────────────────────────────────────────────
  schedOpen = signal(false);
  /** Visit detail panel (click row → show visit details + source data) */
  detailOpen = signal(false);
  captureOpen = signal(false);
  statusOpen = signal(false);

  /** The visit currently shown in the detail panel */
  selectedVisit = signal<VisitResponseDto | null>(null);
  /** Full visit detail fetched from GET /api/visits/{id} */
  visitDetail = signal<VisitResponseDto | null>(null);
  /** Source data for the selected visit */
  sourceData = signal<SourceDataResponseDto[]>([]);
  /** Pending visit for status-update modal */
  pendingVisit = signal<VisitResponseDto | null>(null);

  // ── Permission signals ────────────────────────────────────────────────────
  canSchedule = computed(() => this.auth.can('VISIT_SCHEDULE'));
  canUpdate = computed(() => this.auth.can('VISIT_UPDATE'));
  canDelete = computed(() => this.auth.can('VISIT_DELETE'));
  canCapture = computed(() => this.auth.can('SOURCE_CREATE'));
  canVerify = computed(() => this.auth.can('SOURCE_VERIFY'));
  canViewSource = computed(() => this.auth.can('SOURCE_VIEW'));
  canListParticipants = computed(() => this.auth.can('PARTICIPANT_LIST'));
  canListStudy = computed(() => this.auth.can('STUDY_LIST'));

  setParticipant(id: number | null) {
    this.form.patchValue({ participantId: id });
  }
  setStudy(id: number | null) {
    this.form.patchValue({ studyId: id });
  }

  // ── Schedule form ─────────────────────────────────────────────────────────
  form = this.fb.nonNullable.group({
    participantId: this.fb.control<number | null>(null, { validators: [Validators.required] }),
    studyId: this.fb.control<number | null>(null, { validators: [Validators.required] }),
    visitType: ['SCREENING' as VisitType, Validators.required],
    scheduledAt: ['', Validators.required],
  });

  // ── Source-data capture form ──────────────────────────────────────────────
  sourceForm = this.fb.nonNullable.group({
    dataType: ['', Validators.required],
    dataUri: ['', Validators.required],
    collectedAt: ['', Validators.required],
  });

  // ── Status-update form ────────────────────────────────────────────────────
  statusForm = this.fb.nonNullable.group({
    status: ['' as '' | VisitStatus, Validators.required],
    performedAt: [''],
  });

  // ── Lifecycle ─────────────────────────────────────────────────────────────
  ngOnInit() {
    if (this.canListParticipants())
      this.partApi.list().subscribe((v) => this.participants.set(v ?? []));
    if (this.canListStudy()) this.studyApi.list().subscribe((v) => this.studies.set(v ?? []));
    const url = new URL(window.location.href);
    const pid = url.searchParams.get('participantId');
    if (pid) {
      this.scope.set('participant');
      this.participantId.set(+pid);
      this.load();
    }
    if (this.isParticipant() && this.auth.participant()) {
      // this.userApi.get(this.auth.user()?.userId ?? 0).subscribe({
      //   next: full => {
      //     this.partApi.byPhone(this.auth..phone).subscribe({
      //       next: p => {
              this.scope.set('participant');
              this.participantId.set(this.auth.participant()?.participantId ?? 0);
              this.load();
      //       }
      //     });
      //   }
      // })
    }
  }

  participantName(id: number) {
    return this.participants().find((p) => p.participantId === id)?.name || `#${id}`;
  }

  // ── Load list ─────────────────────────────────────────────────────────────
  load() {
    this.error.set(null);
    if (this.scope() === 'participant' && this.participantId()) {
      // GET /api/visits/participant/{participantId}
      this.loading.set(true);
      this.api
        .byParticipant(this.participantId()!)
        .subscribe({
          next: (v) => { this.list.set(v ?? []); this.loading.set(false); },
          error: (e) => { this.error.set(extractErrorMessage(e, 'Could not load visits.')); this.loading.set(false); },
        });
    } else if (this.scope() === 'study' && this.studyId()) {
      // GET /api/visits/study/{studyId}
      this.loading.set(true);
      this.api.byStudy(this.studyId()!).subscribe({
        next: (v) => { this.list.set(v ?? []); this.loading.set(false); },
        error: (e) => { this.error.set(extractErrorMessage(e, 'Could not load visits.')); this.loading.set(false); },
      });
    }
  }

  // ── Schedule ──────────────────────────────────────────────────────────────
  openSchedule() {
    this.form.reset({
      participantId: this.participantId(),
      studyId: this.studyId(),
      visitType: 'SCREENING',
      scheduledAt: '',
    });
    this.schedOpen.set(true);
  }
  /** POST /api/visits/schedule  (ROLE: PI, COORDINATOR) */
  submit() {
    if (this.form.invalid) return;
    this.api.schedule(this.form.getRawValue() as any).subscribe({
      next: () => {
        this.toast.success('Visit scheduled');
        this.schedOpen.set(false);
        this.load();
        this.notification.refresh();
      }
    });
  }

  // ── Visit detail (click row) ──────────────────────────────────────────────
  /**
   * Opens the detail panel for a visit.
   * Calls:
   *   GET /api/visits/{visitId}            → full visit record
   *   GET /api/sourcedata/byVisit/{visitId} → source data list (if permitted)
   */
  openDetail(v: VisitResponseDto) {
    this.selectedVisit.set(v);
    this.visitDetail.set(null);
    this.sourceData.set([]);
    this.detailOpen.set(true);

    // GET /api/visits/{visitId}
    this.api.get(v.visitId).subscribe({
      next: (detail) => this.visitDetail.set(detail)
    });

    // GET /api/sourcedata/byVisit/{visitId}
    if (this.canViewSource()) {
      this.srcApi.byVisit(v.visitId).subscribe({
        next: (sd) => this.sourceData.set(sd ?? []),
        error: () => { },
      });
    }
  }

  // ── Capture source data ───────────────────────────────────────────────────
  openCapture() {
    this.sourceForm.reset();
    this.captureOpen.set(true);
  }
  /**
   * POST /api/sourcedata/visit  (ROLE: TECHNICIAN, COORDINATOR)
   * Refreshes source-data list after capture.
   */
  submitSource() {
    const v = this.visitDetail() ?? this.selectedVisit();
    if (!v) return;
    const me = this.auth.user();
    this.srcApi
      .create({
        visitId: v.visitId,
        collectedBy: me?.userId ?? 0,
        ...this.sourceForm.getRawValue(),
      })
      .subscribe({
        next: () => {
          this.toast.success('Source data captured');
          this.captureOpen.set(false);
          // refresh  GET /api/sourcedata/byVisit/{visitId}
          this.srcApi.byVisit(v.visitId).subscribe((sd) => this.sourceData.set(sd ?? []));
        }
      });
  }

  // ── View source file ──────────────────────────────────────────────────────
  /**
   * GET /api/sourcedata/view/{sourceDataId}
   * Fetches the file as a Blob (JWT is attached by the interceptor automatically),
   * converts it to a temporary object URL, and opens it in a new browser tab.
   * The URL is revoked after 60 s to avoid memory leaks.
   */
  viewFile(sd: SourceDataResponseDto) {
    this.srcApi.viewFile(sd.sourceId).subscribe({
      next: (blob: Blob) => {
        const url = URL.createObjectURL(blob);
        window.open(url, '_blank');
        setTimeout(() => URL.revokeObjectURL(url), 60_000);
      }
    });
  }

  // ── Verify hash ───────────────────────────────────────────────────────────
  /** GET /api/sourcedata/verify/{sourceDataId}  (ROLE: ADMIN, PI, COORDINATOR) */
  verify(sd: SourceDataResponseDto) {
    this.srcApi.verify(sd.sourceId).subscribe({
      next: (ok) =>
        ok ? this.toast.success('Hash verified ✓') : this.toast.error('Hash mismatch ✗'),
      error: () => this.toast.error('Verification error'),
    });
  }

  // ── Update status ─────────────────────────────────────────────────────────
  openStatus(v: VisitResponseDto) {
    this.pendingVisit.set(v);
    this.statusForm.reset({
      status: v.status,
      performedAt: v.performedAt
        ? this.toLocalInput(v.performedAt)
        : this.toLocalInput(new Date().toISOString()),
    });
    this.statusOpen.set(true);
  }
  /**
   * PUT /api/visits/{visitId}/status?status=...&performedAt=...
   * (ROLE: PI, COORDINATOR)
   */
  submitStatus() {
    const v = this.pendingVisit();
    const raw = this.statusForm.getRawValue();
    if (!v || !raw.status) return;

    const newStatus = raw.status as VisitStatus;
    const performedAt = newStatus === 'COMPLETED' ? this.toIsoLocal(raw.performedAt) : undefined;

    if (newStatus === 'COMPLETED' && !performedAt) {
      this.toast.error('Please supply when the visit was performed.');
      return;
    }

    this.api.updateStatus(v.visitId, newStatus, performedAt).subscribe({
      next: (updated) => {
        this.toast.success('Status updated');
        this.statusOpen.set(false);
        this.load();
        this.notification.refresh();
        // keep detail panel in sync
        if (this.visitDetail()?.visitId === updated.visitId) {
          this.visitDetail.set(updated);
        }
      }
    });
  }

  // ── Cancel (soft-delete) ──────────────────────────────────────────────────
  /** DELETE /api/visits/{visitId}  (ROLE: PI, COORDINATOR, PARTICIPANT) */
  del(v: VisitResponseDto) {
    if (!confirm(`Cancel visit #${v.visitId}?`)) return;
    this.api.delete(v.visitId).subscribe({
      next: () => {
        this.toast.success('Visit cancelled');
        this.detailOpen.set(false);
        this.load();
      }
    });
  }

  // ── Helpers ───────────────────────────────────────────────────────────────
  private toLocalInput(iso: string): string {
    return (iso ?? '').slice(0, 16);
  }
  private toIsoLocal(local: string | null | undefined): string | undefined {
    if (!local) return undefined;
    return local.length === 16 ? `${local}:00` : local;
  }
}

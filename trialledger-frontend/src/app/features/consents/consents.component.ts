import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ConsentService } from '../../core/services/consent.service';
import { ParticipantService } from '../../core/services/participant.service';
import { StudyService } from '../../core/services/study.service';
import { AuthService } from '../../core/auth/auth.service';
import { ToastService } from '../../core/services/toast.service';
import { ConsentMethod, ConsentResponseDTO } from '../../core/models/consent.models';
import { ParticipantResponseDTO } from '../../core/models/participant.models';
import { StudyResponseDto } from '../../core/models/study.models';
import { StatusBadgeComponent } from '../../shared/status-badge/status-badge.component';
import { ModalComponent } from '../../shared/modal/modal.component';
import { SpinnerComponent } from '../../shared/spinner/spinner.component';
import { extractErrorMessage } from '../../core/utils/error-message';
import { NotificationService } from '../../core/services/notification.service';

@Component({
  selector: 'tl-consents',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, DatePipe,
    StatusBadgeComponent, ModalComponent, SpinnerComponent],
  templateUrl: './consents.component.html'
})
export class ConsentsComponent implements OnInit {
  private api = inject(ConsentService);
  private partApi = inject(ParticipantService);
  private studyApi = inject(StudyService);
  private toast = inject(ToastService);
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private notification = inject(NotificationService);

  scope = signal<'participant' | 'study'>('participant');
  participantId = signal<number | null>(null);
  studyId = signal<number | null>(null);

  list = signal<ConsentResponseDTO[]>([]);
  participants = signal<ParticipantResponseDTO[]>([]);
  studies = signal<StudyResponseDto[]>([]);
  recordOpen = signal(false);
  withdrawOpen = signal(false);
  selected = signal<ConsentResponseDTO | null>(null);
  loading = signal(false);
  error = signal<string | null>(null);

  /** Logged-in user has the PARTICIPANT role -> heavily restricted view. */
  isParticipant = computed(() => this.auth.role() === 'PARTICIPANT');

  canCreate = computed(() => this.auth.can('CONSENT_CREATE'));
  canWithdraw = computed(() => this.auth.can('CONSENT_WITHDRAW'));
  canVerify = computed(() => this.auth.can('CONSENT_VERIFY'));
  canListParticipants = computed(() => this.auth.can('PARTICIPANT_LIST'));

  /** Mirrors backend GET /api/consents/study/{studyId} access. */
  canViewByStudy = computed(() => this.auth.can('CONSENT_VIEW_BY_STUDY'));
  /** Mirrors backend GET /api/consents/participant/{id} access. */
  canViewByParticipant = computed(() => this.auth.can('CONSENT_VIEW_BY_PARTICIPANT'));

  /** Whether the study-id picker should be shown. */
  canListStudy = computed(() =>
    this.auth.can('STUDY_LIST') && this.canViewByStudy()
  );

  setRecordParticipant(id: number | null) { this.recordForm.patchValue({ participantId: id }); }

  recordForm = this.fb.nonNullable.group({
    participantId: this.fb.control<number | null>(null, { validators: [Validators.required] }),
    protocolId: [0, Validators.required],
    versionNumber: ['', Validators.required],
    consentMethod: ['ELECTRONIC' as ConsentMethod, Validators.required],
    signedDocumentUri: ['']
  });
  withdrawForm = this.fb.nonNullable.group({
    reason: ['', Validators.required],
    effectOnData: ['RETAIN', Validators.required]
  });

  ngOnInit() {
    // For staff roles we load the global participant + study dropdowns.
    if (this.canListParticipants()) this.partApi.list().subscribe(v => this.participants.set(v ?? []));
    if (this.canListStudy()) this.studyApi.list().subscribe(v => this.studies.set(v ?? []));

    // Default scope: if the user can only view by-participant, force it.
    if (!this.canViewByStudy() && this.canViewByParticipant()) {
      this.scope.set('participant');
    }

    if (this.isParticipant()) {
      // PARTICIPANT: lock scope to "by participant" and use the cached id.
      this.scope.set('participant');
      const me = this.auth.participant();
      if (me) {
        this.participantId.set(me.participantId);
        this.load();
      } else {
        this.toast.error("We couldn't find an enrollment for your account yet.");
      }
      return;
    }

       const url = new URL(window.location.href);
    const pid = url.searchParams.get('participantId');
    if (pid) { this.scope.set('participant'); this.participantId.set(+pid); this.load(); }
  }

  participantName(id: number) {
    return this.participants().find(p => p.participantId === id)?.name || `#${id}`;
  }

  load() {
    // Always reset before fetching so a 404/error clears stale results.
    this.list.set([]);
    this.error.set(null);
    if (this.scope() === 'participant' && this.participantId()) {
      this.loading.set(true);
      this.api.byParticipant(this.participantId()!).subscribe({
        next: v => { this.list.set(v ?? []); this.loading.set(false); },
        error: e => { this.list.set([]); this.error.set(extractErrorMessage(e, 'Could not load consents.')); this.loading.set(false); }
      });
    } else if (this.scope() === 'study' && this.studyId()) {
      this.loading.set(true);
      this.api.byStudy(this.studyId()!).subscribe({
        next: v => { this.list.set(v ?? []); this.loading.set(false); },
        error: e => { this.list.set([]); this.error.set(extractErrorMessage(e, 'Could not load consents.')); this.loading.set(false); }
      });
    }
  }

  openRecord() {
    this.recordForm.reset({ participantId: null, protocolId: 0, versionNumber: '', consentMethod: 'ELECTRONIC', signedDocumentUri: '' });
    this.recordOpen.set(true);
  }
  submitRecord() {
    if (this.recordForm.invalid) return;
    this.api.create(this.recordForm.getRawValue() as any).subscribe({
      next: () => {
         this.toast.success('Consent recorded');
          this.recordOpen.set(false); 
          this.load();
          if(this.isParticipant())
            this.partApi.get(this.participantId()!).subscribe(p => this.auth.persistParticipant(p!));
        this.notification.refresh()
       }
    });
  }
  openWithdraw(c: ConsentResponseDTO) {
    this.selected.set(c);
    this.withdrawForm.reset({ reason: '', effectOnData: 'RETAIN' });
    this.withdrawOpen.set(true);
  }
  submitWithdraw() {
    const c = this.selected(); if (!c) return;
    const me = this.auth.user();
    this.api.withdraw(c.consentId, {
      consentId: c.consentId,
      withdrawnBy: me?.userId ?? 0,
      ...this.withdrawForm.getRawValue()
    }).subscribe({
      next: () => { this.toast.success('Withdrawn'); 
        this.withdrawOpen.set(false); 
        this.load();
      this.notification.refresh() }
    });
  }
  verify(c: ConsentResponseDTO) {
    this.api.verify(c.consentId).subscribe({
      next: m => this.toast.success(m || 'Verified')
    });
  }
}

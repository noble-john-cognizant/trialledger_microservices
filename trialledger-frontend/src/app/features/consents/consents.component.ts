import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ConsentService } from '../../core/services/consent.service';
import { ParticipantService } from '../../core/services/participant.service';
import { StudyService } from '../../core/services/study.service';
import { UserService } from '../../core/services/user.service';
import { AuthService } from '../../core/auth/auth.service';
import { ToastService } from '../../core/services/toast.service';
import { ConsentMethod, ConsentResponseDTO } from '../../core/models/consent.models';
import { ParticipantResponseDTO } from '../../core/models/participant.models';
import { StudyResponseDto } from '../../core/models/study.models';
import { StatusBadgeComponent } from '../../shared/status-badge/status-badge.component';
import { ModalComponent } from '../../shared/modal/modal.component';
import { EmptyStateComponent } from '../../shared/empty-state/empty-state.component';
import { SearchSelectComponent, SearchOption } from '../../shared/search-select/search-select.component';
import { extractErrorMessage } from '../../core/utils/error-message';

@Component({
  selector: 'tl-consents',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, DatePipe,
    StatusBadgeComponent, ModalComponent, EmptyStateComponent, SearchSelectComponent],
  templateUrl: './consents.component.html',
  styleUrls: ['./consents.component.css']
})
export class ConsentsComponent implements OnInit {
  private api = inject(ConsentService);
  private partApi = inject(ParticipantService);
  private studyApi = inject(StudyService);
  private userApi = inject(UserService);
  private toast = inject(ToastService);
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);

  scope = signal<'participant' | 'study'>('participant');
  participantId = signal<number | null>(null);
  studyId = signal<number | null>(null);

  list = signal<ConsentResponseDTO[]>([]);
  participants = signal<ParticipantResponseDTO[]>([]);
  studies = signal<StudyResponseDto[]>([]);
  recordOpen = signal(false);
  withdrawOpen = signal(false);
  selected = signal<ConsentResponseDTO | null>(null);

  /** Logged-in user has the PARTICIPANT role -> heavily restricted view. */
  isParticipant = computed(() => this.auth.role() === 'PARTICIPANT');

  canCreate = computed(() => this.auth.can('CONSENT_CREATE'));
  canWithdraw = computed(() => this.auth.can('CONSENT_WITHDRAW'));
  canVerify = computed(() => this.auth.can('CONSENT_VERIFY'));
  canListParticipants = computed(() => this.auth.can('PARTICIPANT_LIST'));
  /**
   * Participants can never search by study id (product decision) — the
   * permission already excludes them server-side, and we hide it in the
   * UI for clarity.
   */
  canListStudy = computed(() =>
    this.auth.can('STUDY_LIST') && !this.isParticipant()
  );

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

    if (this.isParticipant()) {
      // PARTICIPANT: lock the scope to "by participant", auto-resolve THEIR
      // participantId via /users/{me} -> phone -> /participants/by-phone/{phone}
      // and load only their own consents. No way to view other participants
      // or search by study.
      this.scope.set('participant');
      this.autoResolveOwnParticipantId();
      return;
    }

    // For staff: support deep-linking via ?participantId=N
    const url = new URL(window.location.href);
    const pid = url.searchParams.get('participantId');
    if (pid) { this.scope.set('participant'); this.participantId.set(+pid); this.load(); }
  }

  /**
   * For a logged-in PARTICIPANT, look up their own participant record via
   * the by-phone endpoint we built earlier. Once we know their id, load
   * their consents.
   */
  private autoResolveOwnParticipantId() {
    const u = this.auth.user();
    if (!u) return;
    this.userApi.get(u.userId).subscribe({
      next: full => {
        if (!full?.phone) {
          this.toast.error('Your account is missing a phone number — contact your coordinator.');
          return;
        }
        this.partApi.byPhone(full.phone).subscribe({
          next: p => {
            this.participantId.set(p.participantId);
            this.load();
          },
          error: () => {
            this.toast.error("We couldn't find an enrollment for your account yet.");
          }
        });
      },
      error: e => this.toast.error(extractErrorMessage(e, 'Could not load your profile.'))
    });
  }

  participantName(id: number) {
    return this.participants().find(p => p.participantId === id)?.name || `#${id}`;
  }

  load() {
    // Always reset before fetching so a 404/error clears stale results.
    this.list.set([]);
    if (this.scope() === 'participant' && this.participantId()) {
      this.api.byParticipant(this.participantId()!).subscribe({
        next: v => this.list.set(v ?? []),
        error: () => this.list.set([])
      });
    } else if (this.scope() === 'study' && this.studyId()) {
      this.api.byStudy(this.studyId()!).subscribe({
        next: v => this.list.set(v ?? []),
        error: () => this.list.set([])
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
      next: () => { this.toast.success('Consent recorded'); this.recordOpen.set(false); this.load(); },
      error: e => this.toast.error(extractErrorMessage(e, 'Could not record consent.'))
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
      next: () => { this.toast.success('Withdrawn'); this.withdrawOpen.set(false); this.load(); },
      error: e => this.toast.error(extractErrorMessage(e, 'Could not withdraw consent.'))
    });
  }
  verify(c: ConsentResponseDTO) {
    this.api.verify(c.consentId).subscribe({
      next: m => this.toast.success(m || 'Verified'),
      error: e => this.toast.error(extractErrorMessage(e, 'Verification failed'))
    });
  }
}

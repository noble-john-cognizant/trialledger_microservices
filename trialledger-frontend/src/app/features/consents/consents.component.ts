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
import { EmptyStateComponent } from '../../shared/empty-state/empty-state.component';

@Component({
  selector: 'tl-consents',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, DatePipe, StatusBadgeComponent, ModalComponent, EmptyStateComponent],
  templateUrl: './consents.component.html',
  styleUrls: ['./consents.component.css']
})
export class ConsentsComponent implements OnInit {
  private api = inject(ConsentService);
  private partApi = inject(ParticipantService);
  private studyApi = inject(StudyService);
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

  canCreate = computed(() => this.auth.can('CONSENT_CREATE'));
  canWithdraw = computed(() => this.auth.can('CONSENT_WITHDRAW'));
  canVerify = computed(() => this.auth.can('CONSENT_VERIFY'));

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
    if (this.auth.can('PARTICIPANT_LIST')) this.partApi.list().subscribe(v => this.participants.set(v ?? []));
    if (this.auth.can('STUDY_LIST')) this.studyApi.list().subscribe(v => this.studies.set(v ?? []));
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

  openRecord() {
    this.recordForm.reset({ participantId: null, protocolId: 0, versionNumber: '', consentMethod: 'ELECTRONIC', signedDocumentUri: '' });
    this.recordOpen.set(true);
  }
  submitRecord() {
    if (this.recordForm.invalid) return;
    this.api.create(this.recordForm.getRawValue() as any).subscribe({
      next: () => { this.toast.success('Consent recorded'); this.recordOpen.set(false); this.load(); },
      error: e => this.toast.error(e?.error?.message ?? 'Failed')
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
      error: e => this.toast.error(e?.error?.message ?? 'Failed')
    });
  }
  verify(c: ConsentResponseDTO) {
    this.api.verify(c.consentId).subscribe({
      next: m => this.toast.success(m || 'Verified'),
      error: e => this.toast.error(e?.error ?? 'Verification failed')
    });
  }
}

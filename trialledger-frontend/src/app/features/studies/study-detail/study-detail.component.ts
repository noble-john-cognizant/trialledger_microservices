import { Component, Input, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { StudyService } from '../../../core/services/study.service';
import { ParticipantService } from '../../../core/services/participant.service';
import { SampleService } from '../../../core/services/sample.service';
import { AdverseEventService } from '../../../core/services/adverse-event.service';
import { AuthService } from '../../../core/auth/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { extractErrorMessage } from '../../../core/utils/error-message';
import {
  StudyResponseDto, ProtocolVersionResponseDto, ProtocolStatus, ALL_PROTOCOL_STATUSES
} from '../../../core/models/study.models';
import { ParticipantResponseDTO, EnrollmentStatsDTO } from '../../../core/models/participant.models';
import { SampleResponseDTO } from '../../../core/models/sample.models';
import { AdverseEventResponseDto } from '../../../core/models/adverse-event.models';
import { StatusBadgeComponent } from '../../../shared/status-badge/status-badge.component';
import { ModalComponent } from '../../../shared/modal/modal.component';
import { SpinnerComponent } from '../../../shared/spinner/spinner.component';

@Component({
  selector: 'tl-study-detail',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, DatePipe,
    StatusBadgeComponent, ModalComponent, SpinnerComponent],
  templateUrl: './study-detail.component.html'
})
export class StudyDetailComponent implements OnInit {
  @Input() id!: string;

  private studyApi = inject(StudyService);
  private partApi = inject(ParticipantService);
  private sampleApi = inject(SampleService);
  private aeApi = inject(AdverseEventService);
  private toast = inject(ToastService);
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);

  protocolStatuses = ALL_PROTOCOL_STATUSES;
  study = signal<StudyResponseDto | null>(null);
  loading = signal(true);
  error = signal<string | null>(null);
  protocols = signal<ProtocolVersionResponseDto[]>([]);
  participants = signal<ParticipantResponseDTO[]>([]);
  samples = signal<SampleResponseDTO[]>([]);
  adverseEvents = signal<AdverseEventResponseDto[]>([]);
  stats = signal<EnrollmentStatsDTO | null>(null);
  protOpen = signal(false);
  enrollOpen = signal(false);

  canCreateProtocol = computed(() => this.auth.can('PROTOCOL_CREATE'));
  canApproveProtocol = computed(() => this.auth.can('PROTOCOL_APPROVE'));
  canManageProtocol = computed(() => this.auth.can('PROTOCOL_MANAGE'));
  canViewParticipants = computed(() => this.auth.can('PARTICIPANT_LIST'));
  canEnroll = computed(() => this.auth.can('PARTICIPANT_CREATE'));
  canViewSamples = computed(() => this.auth.can('SAMPLE_VIEW'));

  protForm = this.fb.nonNullable.group({
    versionNumber: ['', Validators.required],
    documentUrl: ['', Validators.required],
    effectiveDate: ['', Validators.required]
  });

  /** Participant enrollment form (study is fixed to the current page's study). */
  enrollForm = this.fb.nonNullable.group({
    externalId: ['', Validators.required],
    name: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    phone: ['', Validators.required],
    dob: ['', Validators.required]
  });

  ngOnInit() {
    const sid = Number(this.id);
    this.loading.set(true);
    this.studyApi.get(sid).subscribe({
      next: v => { this.study.set(v); this.loading.set(false); },
      error: e => { this.error.set(extractErrorMessage(e, 'Could not load study.')); this.loading.set(false); }
    });
    this.studyApi.studyProtocols(sid).subscribe({ next: v => this.protocols.set(v ?? []) });
    if (this.canViewParticipants()) {
      this.partApi.byStudy(sid).subscribe({ next: v => this.participants.set(v ?? []) });
      this.partApi.stats(sid).subscribe({ next: v => this.stats.set(v) });
    }
    if (this.canViewSamples()) {
      this.sampleApi.byStudy(sid).subscribe({ next: v => this.samples.set(v ?? []) });
    }
    if (this.auth.can('AE_VIEW')) {
      this.aeApi.byStudy(sid).subscribe({ next: v => this.adverseEvents.set(v ?? []) });
    }
  }

  reloadProtocols() {
    this.studyApi.studyProtocols(Number(this.id)).subscribe(v => this.protocols.set(v ?? []));
  }

  reloadParticipants() {
    const sid = Number(this.id);
    this.partApi.byStudy(sid).subscribe({ next: v => this.participants.set(v ?? []) });
    this.partApi.stats(sid).subscribe({ next: v => this.stats.set(v) });
  }

  openEnroll() {
    this.enrollForm.reset({ externalId: '', name: '', email: '', phone: '', dob: '' });
    this.enrollOpen.set(true);
  }
  submitEnroll() {
    if (this.enrollForm.invalid) return;
    const v = this.enrollForm.getRawValue();
    this.partApi.create({
      studyId: Number(this.id),
      externalId: v.externalId,
      name: v.name,
      dob: v.dob,
      phone: v.phone,
      email: v.email
    }).subscribe({
      next: () => { this.toast.success('Participant enrolled'); this.enrollOpen.set(false); this.reloadParticipants(); }
    });
  }

  openProtocol() {
    this.protForm.reset({ versionNumber: '', documentUrl: '', effectiveDate: '' });
    this.protOpen.set(true);
  }
  submitProt() {
    if (this.protForm.invalid) return;
    this.studyApi.addProtocol(Number(this.id), this.protForm.getRawValue()).subscribe({
      next: () => { this.toast.success('Protocol version added'); this.protOpen.set(false); this.reloadProtocols(); }
    });
  }
  approve(p: ProtocolVersionResponseDto) {
    this.studyApi.approveProtocol(p.protocolId).subscribe({
      next: () => { this.toast.success('Approved'); this.reloadProtocols(); }
    });
  }
  changeProtocolStatus(p: ProtocolVersionResponseDto, status: string) {
    if (!status) return;
    this.studyApi.updateProtocolStatus(p.protocolId, status as ProtocolStatus).subscribe({
      next: () => { this.toast.success('Status changed'); this.reloadProtocols(); }
    });
  }
  delProtocol(p: ProtocolVersionResponseDto) {
    if (!confirm(`Delete protocol v${p.versionNumber}?`)) return;
    this.studyApi.deleteProtocol(Number(this.id), p.protocolId).subscribe({
      next: () => { this.toast.success('Deleted'); this.reloadProtocols(); }
    });
  }
}

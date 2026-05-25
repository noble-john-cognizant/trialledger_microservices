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
import {
  StudyResponseDto, ProtocolVersionResponseDto, ProtocolStatus, ALL_PROTOCOL_STATUSES
} from '../../../core/models/study.models';
import { ParticipantResponseDTO, EnrollmentStatsDTO } from '../../../core/models/participant.models';
import { SampleResponseDTO } from '../../../core/models/sample.models';
import { AdverseEventResponseDto } from '../../../core/models/adverse-event.models';
import { StatusBadgeComponent } from '../../../shared/status-badge/status-badge.component';
import { ModalComponent } from '../../../shared/modal/modal.component';
import { EmptyStateComponent } from '../../../shared/empty-state/empty-state.component';

@Component({
  selector: 'tl-study-detail',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, DatePipe, StatusBadgeComponent, ModalComponent, EmptyStateComponent],
  templateUrl: './study-detail.component.html',
  styleUrls: ['./study-detail.component.css']
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
  protocols = signal<ProtocolVersionResponseDto[]>([]);
  participants = signal<ParticipantResponseDTO[]>([]);
  samples = signal<SampleResponseDTO[]>([]);
  adverseEvents = signal<AdverseEventResponseDto[]>([]);
  stats = signal<EnrollmentStatsDTO | null>(null);
  protOpen = signal(false);

  canCreateProtocol = computed(() => this.auth.can('PROTOCOL_CREATE'));
  canApproveProtocol = computed(() => this.auth.can('PROTOCOL_APPROVE'));
  canManageProtocol = computed(() => this.auth.can('PROTOCOL_MANAGE'));
  canViewParticipants = computed(() => this.auth.can('PARTICIPANT_LIST'));
  canViewSamples = computed(() => this.auth.can('SAMPLE_VIEW'));

  protForm = this.fb.nonNullable.group({
    versionNumber: ['', Validators.required],
    documentUrl: ['', Validators.required],
    effectiveDate: ['', Validators.required]
  });

  ngOnInit() {
    const sid = Number(this.id);
    this.studyApi.get(sid).subscribe({ next: v => this.study.set(v) });
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

  openProtocol() {
    this.protForm.reset({ versionNumber: '', documentUrl: '', effectiveDate: '' });
    this.protOpen.set(true);
  }
  submitProt() {
    if (this.protForm.invalid) return;
    this.studyApi.addProtocol(Number(this.id), this.protForm.getRawValue()).subscribe({
      next: () => { this.toast.success('Protocol version added'); this.protOpen.set(false); this.reloadProtocols(); },
      error: e => this.toast.error(e?.error?.message ?? 'Failed')
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

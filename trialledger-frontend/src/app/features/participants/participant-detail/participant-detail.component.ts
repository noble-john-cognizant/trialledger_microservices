import { Component, Input, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ParticipantService } from '../../../core/services/participant.service';
import { ConsentService } from '../../../core/services/consent.service';
import { VisitService } from '../../../core/services/visit.service';
import { SampleService } from '../../../core/services/sample.service';
import { AdverseEventService } from '../../../core/services/adverse-event.service';
import { StudyService } from '../../../core/services/study.service';
import { AuthService } from '../../../core/auth/auth.service';
import { ParticipantResponseDTO } from '../../../core/models/participant.models';
import { ConsentResponseDTO } from '../../../core/models/consent.models';
import { VisitResponseDto } from '../../../core/models/visit.models';
import { SampleResponseDTO } from '../../../core/models/sample.models';
import { AdverseEventResponseDto } from '../../../core/models/adverse-event.models';
import { StudyResponseDto, ProtocolVersionResponseDto } from '../../../core/models/study.models';
import { StatusBadgeComponent } from '../../../shared/status-badge/status-badge.component';
import { EmptyStateComponent } from '../../../shared/empty-state/empty-state.component';

@Component({
  selector: 'tl-participant-detail',
  standalone: true,
  imports: [CommonModule, RouterLink, DatePipe, StatusBadgeComponent, EmptyStateComponent],
  templateUrl: './participant-detail.component.html',
  styleUrls: ['./participant-detail.component.css']
})
export class ParticipantDetailComponent implements OnInit {
  @Input() id!: string;

  private partApi = inject(ParticipantService);
  private consentApi = inject(ConsentService);
  private visitApi = inject(VisitService);
  private sampleApi = inject(SampleService);
  private aeApi = inject(AdverseEventService);
  private studyApi = inject(StudyService);
  private auth = inject(AuthService);

  participant = signal<ParticipantResponseDTO | null>(null);
  study = signal<StudyResponseDto | null>(null);
  protocols = signal<ProtocolVersionResponseDto[]>([]);
  consents = signal<ConsentResponseDTO[]>([]);
  visits = signal<VisitResponseDto[]>([]);
  samples = signal<SampleResponseDTO[]>([]);
  adverseEvents = signal<AdverseEventResponseDto[]>([]);

  loading = signal(true);

  canViewConsent = computed(() => this.auth.can('CONSENT_VIEW'));
  canViewVisits = computed(() => this.auth.can('VISIT_VIEW'));
  canViewSamples = computed(() => this.auth.can('SAMPLE_VIEW'));
  canViewAEs = computed(() => this.auth.can('AE_VIEW'));
  canEdit = computed(() => this.auth.can('PARTICIPANT_UPDATE'));
  canViewStudy = computed(() => this.auth.can('STUDY_VIEW'));
  canViewProtocols = computed(() => this.auth.can('PROTOCOL_VIEW'));

  /** Currently active protocol version (if loaded) */
  activeProtocol = computed(() => {
    const list = this.protocols();
    return list.find(p => p.status === 'ACTIVE')
        ?? list.find(p => p.status === 'APPROVED')
        ?? null;
  });

  /** Study lifecycle progress 0..100 based on calendar dates */
  studyProgress = computed(() => {
    const s = this.study();
    if (!s?.startDate || !s?.endDate) return 0;
    const start = new Date(s.startDate).getTime();
    const end   = new Date(s.endDate).getTime();
    const now   = Date.now();
    if (now <= start) return 0;
    if (now >= end)   return 100;
    return Math.round(((now - start) / (end - start)) * 100);
  });

  /** Active consent (if any) — first one with status ACTIVE */
  activeConsent = computed(() =>
    this.consents().find(c => c.status === 'ACTIVE') ?? null
  );

  /** Most recent visit */
  latestVisit = computed(() => {
    const v = [...this.visits()].sort((a, b) =>
      new Date(b.scheduledAt).getTime() - new Date(a.scheduledAt).getTime()
    );
    return v[0] ?? null;
  });

  upcomingVisits = computed(() =>
    this.visits().filter(v => v.status === 'SCHEDULED')
  );

  ngOnInit() {
    const pid = Number(this.id);
    if (!pid) { this.loading.set(false); return; }

    this.partApi.get(pid).subscribe({
      next: p => {
        this.participant.set(p);
        if (p && this.canViewStudy()) {
          this.studyApi.get(p.studyId).subscribe({ next: s => this.study.set(s) });
        }
        if (p && this.canViewProtocols()) {
          this.studyApi.studyProtocols(p.studyId).subscribe({
            next: v => this.protocols.set(v ?? []),
            error: () => this.protocols.set([])
          });
        }
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });

    if (this.canViewConsent()) {
      this.consentApi.byParticipant(pid).subscribe({ next: v => this.consents.set(v ?? []) });
    }
    if (this.canViewVisits()) {
      this.visitApi.byParticipant(pid).subscribe({ next: v => this.visits.set(v ?? []) });
    }
    if (this.canViewSamples()) {
      this.sampleApi.byParticipant(pid).subscribe({ next: v => this.samples.set(v ?? []) });
    }
    if (this.canViewAEs()) {
      this.aeApi.byParticipant(pid).subscribe({ next: v => this.adverseEvents.set(v ?? []) });
    }
  }

  initials(name: string | undefined): string {
    if (!name) return '?';
    return name.split(' ').map(s => s[0]).slice(0, 2).join('').toUpperCase();
  }
}

import { Component, Input, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { ParticipantService } from '../../../core/services/participant.service';
import { StudyService } from '../../../core/services/study.service';
import { ConsentService } from '../../../core/services/consent.service';
import { VisitService } from '../../../core/services/visit.service';
import { SampleService } from '../../../core/services/sample.service';
import { AdverseEventService } from '../../../core/services/adverse-event.service';
import { ParticipantResponseDTO } from '../../../core/models/participant.models';
import { StudyResponseDto, ProtocolVersionResponseDto } from '../../../core/models/study.models';
import { ConsentResponseDTO } from '../../../core/models/consent.models';
import { VisitResponseDto } from '../../../core/models/visit.models';
import { SampleResponseDTO } from '../../../core/models/sample.models';
import { AdverseEventResponseDto } from '../../../core/models/adverse-event.models';
import { StatusBadgeComponent } from '../../../shared/status-badge/status-badge.component';
import { SpinnerComponent } from '../../../shared/spinner/spinner.component';

/**
 * Single participant dashboard, used in two ways:
 *
 *   /participants/:id   →   staff opens any participant (id from route)
 *   /dashboard          →   logged-in PARTICIPANT auto-resolves their own id
 *                           via phone lookup, then shows the same view.
 *
 * Every section is rendered for every role — the backend decides what data
 * actually comes back. A logged-in PARTICIPANT sees their own study,
 * consents, visits, samples and adverse events; an ADMIN sees the same
 * sections about whoever they clicked.
 */
@Component({
  selector: 'tl-participant-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, DatePipe, StatusBadgeComponent, SpinnerComponent],
  templateUrl: './participant-dashboard.component.html',
  styleUrls: ['./participant-dashboard.component.css']
})
export class ParticipantDashboardComponent implements OnInit {
  /** Optional. When present, load that participant. Otherwise auto-resolve. */
  @Input() id?: string;

  private auth = inject(AuthService);
  private partApi = inject(ParticipantService);
  private studyApi = inject(StudyService);
  private consentApi = inject(ConsentService);
  private visitApi = inject(VisitService);
  private sampleApi = inject(SampleService);
  private aeApi = inject(AdverseEventService);

  participant = signal<ParticipantResponseDTO | null>(null);
  study = signal<StudyResponseDto | null>(null);
  protocols = signal<ProtocolVersionResponseDto[]>([]);
  consents = signal<ConsentResponseDTO[]>([]);
  visits = signal<VisitResponseDto[]>([]);
  samples = signal<SampleResponseDTO[]>([]);
  adverseEvents = signal<AdverseEventResponseDto[]>([]);
  isParticipant = computed(() => this.auth.hasRole('PARTICIPANT'));
  loading = signal(true);
  notFound = signal('');

  canViewSample = signal(true);
  /** True when the route gave us an id (staff opened the page). */
  showBackLink = computed(() => !!this.id);

  ngOnInit() {
    if (this.id) {
      this.loadById(Number(this.id));
    } else {
      this.autoResolve();
    }
  }

  initials(name: string | undefined) {
    if (!name) return '?';
    return name.split(' ').map(s => s[0]).slice(0, 2).join('').toUpperCase();
  }

  /** Staff path — id known. */
  private loadById(pid: number) {
    this.loading.set(true);
    this.partApi.get(pid).subscribe({
      next: p => {
        this.participant.set(p);
        this.loading.set(false);
        if (p) this.loadDependents(p);
      },
      error: () => { this.loading.set(false); this.notFound.set('Participant not found.'); }
    });
  }

  /** Participant path — read the row cached at login. No API call needed. */
  private autoResolve() {
    const p = this.auth.participant();
    if (!p) {
      this.loading.set(false);
      this.notFound.set("We couldn't find an enrollment for your account yet.");
      return;
    }
    this.participant.set(p);
    this.loading.set(false);
    this.loadDependents(p);
  }

  /**
   * Always fan out the six related fetches. Each call's @PreAuthorize on the
   * backend decides whether the caller is allowed and which rows come back.
   */
  private loadDependents(p: ParticipantResponseDTO) {
    this.studyApi.get(p.studyId).subscribe({ next: s => this.study.set(s) });
    this.studyApi.studyProtocols(p.studyId).subscribe({ next: v => this.protocols.set(v ?? []) });
    this.consentApi.byParticipant(p.participantId).subscribe({ next: v => this.consents.set(v ?? []) });
    this.visitApi.byParticipant(p.participantId).subscribe({ next: v => this.visits.set(v ?? []) });
    this.sampleApi.byParticipant(p.participantId).subscribe({
      next: v => this.samples.set(v ?? []), error: e => {
        if (e.status === 403) this.canViewSample.set(false);
      }
    });
    this.aeApi.byParticipant(p.participantId).subscribe({
      next: v => this.adverseEvents.set(v ?? [])
    });
  }
}

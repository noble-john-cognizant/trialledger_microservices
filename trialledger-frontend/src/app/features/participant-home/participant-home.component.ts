import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { ParticipantService } from '../../core/services/participant.service';
import { StudyService } from '../../core/services/study.service';
import { ConsentService } from '../../core/services/consent.service';
import { VisitService } from '../../core/services/visit.service';
import { ParticipantResponseDTO } from '../../core/models/participant.models';
import { StudyResponseDto, ProtocolVersionResponseDto } from '../../core/models/study.models';
import { ConsentResponseDTO } from '../../core/models/consent.models';
import { VisitResponseDto } from '../../core/models/visit.models';
import { StatusBadgeComponent } from '../../shared/status-badge/status-badge.component';
import { EmptyStateComponent } from '../../shared/empty-state/empty-state.component';

/**
 * localStorage namespace. The actual key is `${LINK_KEY_PREFIX}${userId}` so
 * each logged-in user has their own linkage and accounts can't leak across
 * each other on a shared browser.
 */
const LINK_KEY_PREFIX = 'tl_participant_id:';

/** Legacy unscoped key from earlier versions — wiped on startup so previously
 * linked IDs don't bleed across accounts. */
const LEGACY_LINK_KEY = 'tl_participant_id';

/**
 * Home page tailored to a PARTICIPANT-role user.
 *
 * A PARTICIPANT can call GET /api/participants/{id} for their own record, but
 * the backend doesn't expose a userId → participantId mapping endpoint.
 * The first time the participant visits this page they link their enrollment
 * by entering the participantId provided by their coordinator. We cache it
 * in localStorage under a key scoped by their userId so different participants
 * on the same browser keep their own linkages.
 *
 * Permission-gated sections:
 *   - Study card body shows full details only if STUDY_VIEW is allowed
 *     (otherwise we just show the study ID with a hint).
 *   - Protocol info is shown only if PROTOCOL_VIEW is allowed.
 */
@Component({
  selector: 'tl-participant-home',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, DatePipe,
    StatusBadgeComponent, EmptyStateComponent],
  templateUrl: './participant-home.component.html',
  styleUrls: ['./participant-home.component.css']
})
export class ParticipantHomeComponent implements OnInit {
  private auth = inject(AuthService);
  private partApi = inject(ParticipantService);
  private studyApi = inject(StudyService);
  private consentApi = inject(ConsentService);
  private visitApi = inject(VisitService);

  user = this.auth.user;

  constructor() {
    // One-time migration: drop the old unscoped key so a previously linked ID
    // from another account can't be re-read by a new participant.
    if (localStorage.getItem(LEGACY_LINK_KEY) !== null) {
      localStorage.removeItem(LEGACY_LINK_KEY);
    }
  }

  participantId = signal<number | null>(this.readLinkedId());
  participant   = signal<ParticipantResponseDTO | null>(null);
  study         = signal<StudyResponseDto | null>(null);
  protocols     = signal<ProtocolVersionResponseDto[]>([]);
  consents      = signal<ConsentResponseDTO[]>([]);
  visits        = signal<VisitResponseDto[]>([]);

  loading    = signal(false);
  linkInput  = signal<number | null>(null);
  linkError  = signal('');

  canViewStudy     = computed(() => this.auth.can('STUDY_VIEW'));
  canViewProtocols = computed(() => this.auth.can('PROTOCOL_VIEW'));
  canViewConsent   = computed(() => this.auth.can('CONSENT_VIEW'));
  canViewVisits    = computed(() => this.auth.can('VISIT_VIEW'));

  activeConsent = computed(() =>
    this.consents().find(c => c.status === 'ACTIVE') ?? null
  );
  upcomingVisits = computed(() =>
    this.visits().filter(v => v.status === 'SCHEDULED')
  );
  activeProtocol = computed(() => {
    const list = this.protocols();
    return list.find(p => p.status === 'ACTIVE')
        ?? list.find(p => p.status === 'APPROVED')
        ?? null;
  });

  ngOnInit() {
    if (this.participantId()) this.loadAll();
  }

  initials(name: string | undefined): string {
    if (!name) return '?';
    return name.split(' ').map(s => s[0]).slice(0, 2).join('').toUpperCase();
  }

  link() {
    this.linkError.set('');
    const id = this.linkInput();
    if (!id || id <= 0) {
      this.linkError.set('Enter a valid participant ID.');
      return;
    }
    const key = this.storageKey();
    if (!key) {
      this.linkError.set('You must be signed in to link an enrollment.');
      return;
    }
    this.loading.set(true);
    this.partApi.get(id).subscribe({
      next: p => {
        localStorage.setItem(key, String(id));
        this.participantId.set(id);
        this.participant.set(p);
        this.loading.set(false);
        this.loadDependents(id, p);
      },
      error: () => {
        this.loading.set(false);
        this.linkError.set('Participant not found or access denied. Check the ID with your coordinator.');
      }
    });
  }

  unlink() {
    const key = this.storageKey();
    if (key) localStorage.removeItem(key);
    this.participantId.set(null);
    this.participant.set(null);
    this.study.set(null);
    this.protocols.set([]);
    this.consents.set([]);
    this.visits.set([]);
    this.linkInput.set(null);
  }

  private loadAll() {
    const pid = this.participantId();
    if (!pid) return;
    this.loading.set(true);
    this.partApi.get(pid).subscribe({
      next: p => {
        this.participant.set(p);
        this.loading.set(false);
        this.loadDependents(pid, p);
      },
      error: () => {
        // stale localStorage — clear it and let the user re-link
        this.loading.set(false);
        this.linkError.set('Saved enrollment could not be loaded. Please re-enter your participant ID.');
        const key = this.storageKey();
        if (key) localStorage.removeItem(key);
        this.participantId.set(null);
      }
    });
  }

  private loadDependents(pid: number, p: ParticipantResponseDTO) {
    if (this.canViewStudy()) {
      this.studyApi.get(p.studyId).subscribe({
        next: s => this.study.set(s),
        error: () => this.study.set(null)
      });
    }
    if (this.canViewProtocols()) {
      this.studyApi.studyProtocols(p.studyId).subscribe({
        next: v => this.protocols.set(v ?? []),
        error: () => this.protocols.set([])
      });
    }
    if (this.canViewConsent()) {
      this.consentApi.byParticipant(pid).subscribe({
        next: v => this.consents.set(v ?? [])
      });
    }
    if (this.canViewVisits()) {
      this.visitApi.byParticipant(pid).subscribe({
        next: v => this.visits.set(v ?? [])
      });
    }
  }

  /** Per-user localStorage key, or null when no one is signed in. */
  private storageKey(): string | null {
    const uid = this.auth.user()?.userId;
    return uid != null ? `${LINK_KEY_PREFIX}${uid}` : null;
  }

  private readLinkedId(): number | null {
    const key = this.storageKey();
    if (!key) return null;
    const raw = localStorage.getItem(key);
    const n = raw ? Number(raw) : NaN;
    return Number.isFinite(n) && n > 0 ? n : null;
  }
}

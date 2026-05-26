import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { ParticipantService } from '../../core/services/participant.service';
import { UserService } from '../../core/services/user.service';
import { StudyService } from '../../core/services/study.service';
import { ConsentService } from '../../core/services/consent.service';
import { VisitService } from '../../core/services/visit.service';
import { ParticipantResponseDTO } from '../../core/models/participant.models';
import { StudyResponseDto, ProtocolVersionResponseDto } from '../../core/models/study.models';
import { ConsentResponseDTO } from '../../core/models/consent.models';
import { VisitResponseDto } from '../../core/models/visit.models';
import { StatusBadgeComponent } from '../../shared/status-badge/status-badge.component';
import { EmptyStateComponent } from '../../shared/empty-state/empty-state.component';
import { extractErrorMessage } from '../../core/utils/error-message';

/**
 * Home page for a logged-in PARTICIPANT.
 *
 * Auto-discovery flow:
 *   1. Fetch the logged-in user's record (/api/users/{id}) to read their phone
 *   2. Call /api/participants/by-phone/{phone} to find the matching enrollment
 *   3. Load consents, visits, study, protocols for that participant
 *
 * No manual ID entry is required — the linkage is derived from the phone
 * number captured at signup and stored uniquely on the Participant row.
 */
@Component({
  selector: 'tl-participant-home',
  standalone: true,
  imports: [CommonModule, RouterLink, DatePipe, StatusBadgeComponent, EmptyStateComponent],
  templateUrl: './participant-home.component.html',
  styleUrls: ['./participant-home.component.css']
})
export class ParticipantHomeComponent implements OnInit {
  private auth = inject(AuthService);
  private partApi = inject(ParticipantService);
  private userApi = inject(UserService);
  private studyApi = inject(StudyService);
  private consentApi = inject(ConsentService);
  private visitApi = inject(VisitService);

  user = this.auth.user;

  participant = signal<ParticipantResponseDTO | null>(null);
  study       = signal<StudyResponseDto | null>(null);
  protocols   = signal<ProtocolVersionResponseDto[]>([]);
  consents    = signal<ConsentResponseDTO[]>([]);
  visits      = signal<VisitResponseDto[]>([]);

  loading   = signal(true);
  lookupErr = signal('');

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
    this.autoLink();
  }

  /** Try once with whatever we have; expose retry to the user via the UI. */
  retry() { this.autoLink(); }

  initials(name: string | undefined): string {
    if (!name) return '?';
    return name.split(' ').map(s => s[0]).slice(0, 2).join('').toUpperCase();
  }

  private autoLink() {
    this.lookupErr.set('');
    const u = this.user();
    if (!u) {
      this.loading.set(false);
      this.lookupErr.set('You must be signed in to view this page.');
      return;
    }

    this.loading.set(true);

    // Step 1: get my user record to read the phone number.
    this.userApi.get(u.userId).subscribe({
      next: full => {
        if (!full?.phone) {
          this.loading.set(false);
          this.lookupErr.set('Your account has no phone number on file. Please contact your coordinator.');
          return;
        }
        // Step 2: look up my participant row by phone.
        this.partApi.byPhone(full.phone).subscribe({
          next: p => {
            this.participant.set(p);
            this.loading.set(false);
            this.loadDependents(p);
          },
          error: err => {
            this.loading.set(false);
            this.lookupErr.set(
              extractErrorMessage(err,
                "We couldn't find an enrollment matching your phone number. Your coordinator may not have enrolled you yet.")
            );
          }
        });
      },
      error: err => {
        this.loading.set(false);
        this.lookupErr.set(extractErrorMessage(err, 'Could not load your account details.'));
      }
    });
  }

  private loadDependents(p: ParticipantResponseDTO) {
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
      this.consentApi.byParticipant(p.participantId).subscribe({
        next: v => this.consents.set(v ?? []),
        error: () => this.consents.set([])
      });
    }
    if (this.canViewVisits()) {
      this.visitApi.byParticipant(p.participantId).subscribe({
        next: v => this.visits.set(v ?? []),
        error: () => this.visits.set([])
      });
    }
  }
}

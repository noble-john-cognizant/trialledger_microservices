import {
  Component, ElementRef, HostListener,
  computed, inject, signal
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { ParticipantService } from '../../core/services/participant.service';
import { StudyService } from '../../core/services/study.service';
import { SampleService } from '../../core/services/sample.service';
import { ParticipantResponseDTO } from '../../core/models/participant.models';
import { StudyResponseDto } from '../../core/models/study.models';
import { SampleResponseDTO } from '../../core/models/sample.models';

interface SearchHit {
  type: 'study' | 'participant' | 'sample';
  id: number;
  label: string;
  subtitle: string;
  route: any[];
  icon: string;
}

/**
 * Global search dropdown for the topbar. Pre-loads the entities the user
 * is allowed to list and filters them client-side on every keystroke.
 * Selecting a result navigates to the corresponding detail page.
 */
@Component({
  selector: 'tl-global-search',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './global-search.component.html',
  styleUrls: ['./global-search.component.css']
})
export class GlobalSearchComponent {
  private auth = inject(AuthService);
  private router = inject(Router);
  private partApi = inject(ParticipantService);
  private studyApi = inject(StudyService);
  private sampleApi = inject(SampleService);
  private host = inject(ElementRef<HTMLElement>);

  query = signal('');
  open  = signal(false);

  participants = signal<ParticipantResponseDTO[]>([]);
  studies      = signal<StudyResponseDto[]>([]);
  samples      = signal<SampleResponseDTO[]>([]);

  /** Has the user-permission gated initial load completed? */
  loaded = signal(false);

  results = computed<SearchHit[]>(() => {
    const q = this.query().toLowerCase().trim();
    if (!q || q.length < 1) return [];

    const hits: SearchHit[] = [];

    // ---- studies
    for (const s of this.studies()) {
      if (
        s.title.toLowerCase().includes(q) ||
        s.sponsor.toLowerCase().includes(q) ||
        s.protocolNumber.toLowerCase().includes(q) ||
        String(s.studyId).includes(q)
      ) {
        hits.push({
          type: 'study', id: s.studyId,
          label: s.title,
          subtitle: `${s.sponsor} · #${s.protocolNumber}`,
          route: ['/studies', s.studyId],
          icon: 'bi-journal-bookmark'
        });
      }
    }

    // ---- participants
    for (const p of this.participants()) {
      if (
        p.name.toLowerCase().includes(q) ||
        p.externalId.toLowerCase().includes(q) ||
        String(p.participantId).includes(q)
      ) {
        hits.push({
          type: 'participant', id: p.participantId,
          label: p.name,
          subtitle: `${p.externalId} · Study #${p.studyId}`,
          route: ['/participants', p.participantId],
          icon: 'bi-person-circle'
        });
      }
    }

    // ---- samples (typically searched by ID or type)
    for (const s of this.samples()) {
      if (
        String(s.sampleId).includes(q) ||
        s.sampleType.toLowerCase().includes(q) ||
        s.initialLocation.toLowerCase().includes(q)
      ) {
        hits.push({
          type: 'sample', id: s.sampleId,
          label: `${s.sampleType} – #${s.sampleId}`,
          subtitle: `Participant #${s.participantId} · ${s.initialLocation}`,
          route: ['/samples'],
          icon: 'bi-droplet'
        });
      }
    }

    return hits.slice(0, 12);
  });

  studyHits       = computed(() => this.results().filter(r => r.type === 'study'));
  participantHits = computed(() => this.results().filter(r => r.type === 'participant'));
  sampleHits      = computed(() => this.results().filter(r => r.type === 'sample'));

  loadIfNeeded() {
    if (this.loaded()) return;
    this.loaded.set(true);

    if (this.auth.can('STUDY_LIST')) {
      this.studyApi.list().subscribe({ next: v => this.studies.set(v ?? []), error: () => {} });
    }
    if (this.auth.can('PARTICIPANT_LIST')) {
      this.partApi.list().subscribe({ next: v => this.participants.set(v ?? []), error: () => {} });
    }
    if (this.auth.can('SAMPLE_VIEW')) {
      this.sampleApi.list().subscribe({ next: v => this.samples.set(v ?? []), error: () => {} });
    }
  }

  onFocus() {
    this.loadIfNeeded();
    this.open.set(true);
  }

  pick(h: SearchHit) {
    this.router.navigate(h.route);
    this.open.set(false);
    this.query.set('');
  }

  onEnter() {
    const list = this.results();
    if (list.length) this.pick(list[0]);
  }

  @HostListener('document:click', ['$event'])
  onDocClick(e: MouseEvent) {
    if (!this.host.nativeElement.contains(e.target as Node)) this.open.set(false);
  }
}

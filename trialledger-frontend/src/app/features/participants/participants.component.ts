import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ParticipantService } from '../../core/services/participant.service';
import { StudyService } from '../../core/services/study.service';
import { AuthService } from '../../core/auth/auth.service';
import { ToastService } from '../../core/services/toast.service';
import { extractErrorMessage } from '../../core/utils/error-message';
import {
  EnrollmentStatus, ParticipantResponseDTO, ALL_ENROLLMENT_STATUSES
} from '../../core/models/participant.models';
import { StudyResponseDto } from '../../core/models/study.models';
import { StatusBadgeComponent } from '../../shared/status-badge/status-badge.component';
import { SpinnerComponent } from '../../shared/spinner/spinner.component';

@Component({
  selector: 'tl-participants',
  standalone: true,
  imports: [
    CommonModule, RouterLink, DatePipe,
    StatusBadgeComponent, SpinnerComponent
  ],
  templateUrl: './participants.component.html',
  // styleUrls: ['./participants.component.css']
})
export class ParticipantsComponent implements OnInit {
  private api = inject(ParticipantService);
  private studyApi = inject(StudyService);
  private toast = inject(ToastService);
  private auth = inject(AuthService);

  statuses = ALL_ENROLLMENT_STATUSES;
  list = signal<ParticipantResponseDTO[]>([]);
  studies = signal<StudyResponseDto[]>([]);
  search = signal('');
  statusFilter = signal('');
  studyFilter = signal('');
  loading = signal(true);
  error = signal<string | null>(null);


  canCreate = computed(() => this.auth.can('PARTICIPANT_CREATE'));
  canUpdate = computed(() => this.auth.can('PARTICIPANT_UPDATE'));
  canViewConsent = computed(() => this.auth.can('CONSENT_VIEW'));
  canViewVisits = computed(() => this.auth.can('VISIT_VIEW'));
  canListStudy = computed(() => this.auth.can('STUDY_LIST'));

  filtered = computed(() => {
    const s = this.search().toLowerCase();
    const st = this.statusFilter();
    const sd = this.studyFilter();
    return this.list().filter(p =>
      (!st || p.enrollmentStatus === st) &&
      (!sd || String(p.studyId) === sd) &&
      (!s || p.name.toLowerCase().includes(s)
           || p.externalId.toLowerCase().includes(s)
           || String(p.participantId).includes(s))
    );
  });

  ngOnInit() {
    this.load();
    if (this.canListStudy()) {
      this.studyApi.list().subscribe(s => this.studies.set(s ?? []));
    }
  }

  load() {
    this.loading.set(true);
    this.error.set(null);
    this.api.list().subscribe({
      next: v => { this.list.set(v ?? []); this.loading.set(false); },
      error: e => { this.error.set(extractErrorMessage(e, 'Could not load participants.')); this.loading.set(false); }
    });
  }

  studyTitle(id: number) {
    return this.studies().find(s => s.studyId === id)?.title || `Study #${id}`;
  }

  updateStatus(p: ParticipantResponseDTO, status: string) {
    if (!status) return;
    this.api.updateStatus(p.participantId, status as EnrollmentStatus).subscribe({
      next: () => {
         this.toast.success('Updated'); this.load();
         }
    });
  }
}

import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { AuthService } from '../../../core/auth/auth.service';
import { StudyService } from '../../../core/services/study.service';
import { StudyResponseDto, ProtocolVersionResponseDto } from '../../../core/models/study.models';
import { StatusBadgeComponent } from '../../../shared/status-badge/status-badge.component';
import { SpinnerComponent } from '../../../shared/spinner/spinner.component';

@Component({
  selector: 'tl-my-study',
  standalone: true,
  imports: [CommonModule, DatePipe, StatusBadgeComponent, SpinnerComponent],
  template: `
    <h2 class="fw-bold mb-3">My study</h2>

    @if (!me) {
      <div class="alert alert-warning">We couldn't find your enrollment yet.</div>
    } @else if (loading()) {
      <tl-spinner block label="Loading…" />
    } @else if (study(); as s) {
      <div class="card mb-3">
        <div class="card-body">
          <div class="d-flex align-items-center gap-2 mb-2 flex-wrap">
            <span class="badge bg-soft-primary">#{{ s.studyId }}</span>
            <h5 class="mb-0 fw-bold">{{ s.title }}</h5>
            <tl-status [value]="s.status" />
          </div>
          <dl class="row mb-0 small">
            <dt class="col-sm-3 text-muted">Sponsor</dt>
            <dd class="col-sm-9">{{ s.sponsor }}</dd>
            <dt class="col-sm-3 text-muted">Protocol</dt>
            <dd class="col-sm-9 mono">#{{ s.protocolNumber }}</dd>
            <dt class="col-sm-3 text-muted">Dates</dt>
            <dd class="col-sm-9">{{ s.startDate | date:'mediumDate' }} — {{ s.endDate | date:'mediumDate' }}</dd>
          </dl>
        </div>
      </div>

      <div class="card">
        <div class="card-header bg-white"><h6 class="fw-bold mb-0">Protocol versions</h6></div>
        <div class="card-body p-0">
          @if (protocols().length === 0) {
            <div class="text-center text-muted py-4">No protocol versions.</div>
          } @else {
            <table class="table mb-0 align-middle">
              <thead><tr><th>ID</th><th>Version</th><th>Effective</th><th>Status</th></tr></thead>
              <tbody>
                @for (p of protocols(); track p.protocolId) {
                  <tr>
                    <td>#{{ p.protocolId }}</td>
                    <td>v{{ p.versionNumber }}</td>
                    <td><small class="text-muted">{{ p.effectiveDate | date:'mediumDate' }}</small></td>
                    <td><tl-status [value]="p.status" /></td>
                  </tr>
                }
              </tbody>
            </table>
          }
        </div>
      </div>
    }
  `
})
export class MyStudyComponent implements OnInit {
  private auth = inject(AuthService);
  private studyApi = inject(StudyService);

  /** Cached at login — no extra API call needed. */
  me = this.auth.participant();

  loading = signal(true);
  study = signal<StudyResponseDto | null>(null);
  protocols = signal<ProtocolVersionResponseDto[]>([]);

  ngOnInit() {
    if (!this.me) { this.loading.set(false); return; }
    this.studyApi.get(this.me.studyId).subscribe({
      next: s => { this.study.set(s); this.loading.set(false); },
      error: () => this.loading.set(false)
    });
    this.studyApi.studyProtocols(this.me.studyId).subscribe({ next: v => this.protocols.set(v ?? []) });
  }
}

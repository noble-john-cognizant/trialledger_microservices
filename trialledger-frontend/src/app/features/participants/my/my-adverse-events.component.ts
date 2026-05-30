import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { AuthService } from '../../../core/auth/auth.service';
import { AdverseEventService } from '../../../core/services/adverse-event.service';
import { AdverseEventResponseDto } from '../../../core/models/adverse-event.models';
import { StatusBadgeComponent } from '../../../shared/status-badge/status-badge.component';
import { SpinnerComponent } from '../../../shared/spinner/spinner.component';

@Component({
  selector: 'tl-my-adverse-events',
  standalone: true,
  imports: [CommonModule, DatePipe, StatusBadgeComponent, SpinnerComponent],
  template: `
    <h2 class="fw-bold mb-3">My adverse events</h2>

    @if (!me) {
      <div class="alert alert-warning">We couldn't find your enrollment yet.</div>
    } @else if (loading()) {
      <tl-spinner block label="Loading…" />
    } @else {
      <div class="card">
        <div class="card-body p-0">
          @if (events().length === 0) {
            <div class="text-center text-muted py-4">No adverse events reported.</div>
          } @else {
            <table class="table mb-0 align-middle">
              <thead><tr><th>ID</th><th>Description</th><th>Severity</th><th>Status</th><th>Reported</th></tr></thead>
              <tbody>
                @for (ae of events(); track ae.aeId) {
                  <tr>
                    <td>#{{ ae.aeId }}</td>
                    <td class="truncate">{{ ae.description }}</td>
                    <td><tl-status [value]="ae.severity" /></td>
                    <td><tl-status [value]="ae.status" /></td>
                    <td><small class="text-muted">{{ ae.reportedAt | date:'short' }}</small></td>
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
export class MyAdverseEventsComponent implements OnInit {
  private auth = inject(AuthService);
  private aeApi = inject(AdverseEventService);

  me = this.auth.participant();
  loading = signal(true);
  events = signal<AdverseEventResponseDto[]>([]);

  ngOnInit() {
    if (!this.me) { this.loading.set(false); return; }
    this.aeApi.byParticipant(this.me.participantId).subscribe({
      next: v => { this.events.set(v ?? []); this.loading.set(false); },
      error: () => this.loading.set(false)
    });
  }
}

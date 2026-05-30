import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { AuthService } from '../../../core/auth/auth.service';
import { SampleService } from '../../../core/services/sample.service';
import { SampleResponseDTO } from '../../../core/models/sample.models';
import { StatusBadgeComponent } from '../../../shared/status-badge/status-badge.component';
import { SpinnerComponent } from '../../../shared/spinner/spinner.component';

@Component({
  selector: 'tl-my-samples',
  standalone: true,
  imports: [CommonModule, DatePipe, StatusBadgeComponent, SpinnerComponent],
  template: `
    <h2 class="fw-bold mb-3">My samples</h2>

    @if (!me) {
      <div class="alert alert-warning">We couldn't find your enrollment yet.</div>
    } @else if (loading()) {
      <tl-spinner block label="Loading…" />
    } @else {
      <div class="card">
        <div class="card-body p-0">
          @if (samples().length === 0) {
            <div class="text-center text-muted py-4">No samples collected.</div>
          } @else {
            <table class="table mb-0 align-middle">
              <thead><tr><th>ID</th><th>Type</th><th>Collected</th><th>Location</th><th>Status</th></tr></thead>
              <tbody>
                @for (s of samples(); track s.sampleId) {
                  <tr>
                    <td>#{{ s.sampleId }}</td>
                    <td>{{ s.sampleType }}</td>
                    <td><small class="text-muted">{{ s.collectedAt | date:'short' }}</small></td>
                    <td class="mono small">{{ s.initialLocation }}</td>
                    <td><tl-status [value]="s.status" /></td>
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
export class MySamplesComponent implements OnInit {
  private auth = inject(AuthService);
  private sampleApi = inject(SampleService);

  me = this.auth.participant();
  loading = signal(true);
  samples = signal<SampleResponseDTO[]>([]);

  ngOnInit() {
    if (!this.me) { this.loading.set(false); return; }
    this.sampleApi.byParticipant(this.me.participantId).subscribe({
      next: v => { this.samples.set(v ?? []); this.loading.set(false); },
      error: () => this.loading.set(false)
    });
  }
}

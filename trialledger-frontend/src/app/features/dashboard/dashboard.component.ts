import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { StudyService } from '../../core/services/study.service';
import { ParticipantService } from '../../core/services/participant.service';
import { SampleService } from '../../core/services/sample.service';
import { AdverseEventService } from '../../core/services/adverse-event.service';
import { KpiService } from '../../core/services/kpi.service';
import { NotificationService } from '../../core/services/notification.service';
import {
  StudyResponseDto, ParticipantResponseDTO, SampleResponseDTO,
  AdverseEventResponseDto, KPIResponseDTO, NotificationResponseDTO
} from '../../core/models';
import { StatusBadgeComponent } from '../../shared/status-badge/status-badge.component';

@Component({
  selector: 'tl-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, DatePipe, StatusBadgeComponent],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  private auth = inject(AuthService);
  private studyApi = inject(StudyService);
  private participantApi = inject(ParticipantService);
  private sampleApi = inject(SampleService);
  private aeApi = inject(AdverseEventService);
  private kpiApi = inject(KpiService);
  private notifApi = inject(NotificationService);

  user = this.auth.user;
  studies = signal<StudyResponseDto[]>([]);
  participants = signal<ParticipantResponseDTO[]>([]);
  samples = signal<SampleResponseDTO[]>([]);
  adverseEvents = signal<AdverseEventResponseDto[]>([]);
  kpis = signal<KPIResponseDTO[]>([]);
  notifs = signal<NotificationResponseDTO[]>([]);

  // Visibility flags driven by permissions
  canStudies = computed(() => this.auth.can('STUDY_LIST'));
  canParticipants = computed(() => this.auth.can('PARTICIPANT_LIST'));
  canSamples = computed(() => this.auth.can('SAMPLE_VIEW'));
  canAEs = computed(() => this.auth.can('AE_VIEW'));
  canKpis = computed(() => this.auth.can('KPI_VIEW'));
  canReports = computed(() => this.auth.can('REPORT_VIEW'));

  ngOnInit() {
    if (this.canStudies())      this.studyApi.list().subscribe({ next: v => this.studies.set(v ?? []), error: () => {} });
    if (this.canParticipants()) this.participantApi.list().subscribe({ next: v => this.participants.set(v ?? []), error: () => {} });
    if (this.canSamples())      this.sampleApi.list().subscribe({ next: v => this.samples.set(v ?? []), error: () => {} });
    if (this.canAEs())          this.aeApi.list().subscribe({ next: v => this.adverseEvents.set(v ?? []), error: () => {} });
    if (this.canKpis())         this.kpiApi.list().subscribe({ next: v => this.kpis.set(v ?? []), error: () => {} });

    const u = this.user();
    if (u) this.notifApi.byUser(u.userId).subscribe({ next: v => this.notifs.set(v ?? []), error: () => {} });
  }

  activeStudies()     { return this.studies().filter(s => s.status === 'ACTIVE').length; }
  enrolledCount()     { return this.participants().filter(p => p.enrollmentStatus === 'ENROLLED').length; }
  inAnalysisSamples() { return this.samples().filter(s => s.status === 'IN_ANALYSIS').length; }
  openAEs()           { return this.adverseEvents().filter(a => a.status === 'OPEN').length; }

  recentStudies() { return [...this.studies()].sort((a, b) => b.studyId - a.studyId).slice(0, 5); }
  recentAEs()     { return [...this.adverseEvents()].sort((a, b) => b.aeId - a.aeId).slice(0, 5); }
  recentNotifs()  { return this.notifs().slice(0, 5); }

  meter(k: KPIResponseDTO) {
    if (!k.target) return 0;
    return Math.min(100, Math.round((k.currentValue / k.target) * 100));
  }
}

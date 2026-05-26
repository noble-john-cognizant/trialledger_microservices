import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule, DatePipe, JsonPipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ReportService } from '../../core/services/report.service';
import { KpiService } from '../../core/services/kpi.service';
import { StudyService } from '../../core/services/study.service';
import { AuthService } from '../../core/auth/auth.service';
import { ToastService } from '../../core/services/toast.service';
import { extractErrorMessage } from '../../core/utils/error-message';
import { ReportResponseDTO, ReportScope, ALL_REPORT_SCOPES } from '../../core/models/report.models';
import { KPIResponseDTO } from '../../core/models/kpi.models';
import { StudyResponseDto } from '../../core/models/study.models';
import { ModalComponent } from '../../shared/modal/modal.component';
import { EmptyStateComponent } from '../../shared/empty-state/empty-state.component';

@Component({
  selector: 'tl-reports',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, DatePipe, JsonPipe, ModalComponent, EmptyStateComponent],
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.css']
})
export class ReportsComponent implements OnInit {
  private rApi = inject(ReportService);
  private kApi = inject(KpiService);
  private studyApi = inject(StudyService);
  private toast = inject(ToastService);
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);

  scopes = ALL_REPORT_SCOPES;
  reports = signal<ReportResponseDTO[]>([]);
  kpis = signal<KPIResponseDTO[]>([]);
  studies = signal<StudyResponseDto[]>([]);
  scopeFilter = signal('');

  reportOpen = signal(false);
  kpiOpen = signal(false);

  canViewReports = computed(() => this.auth.can('REPORT_VIEW'));
  canCreateReport = computed(() => this.auth.can('REPORT_CREATE'));
  canViewKpis = computed(() => this.auth.can('KPI_VIEW'));
  canCreateKpi = computed(() => this.auth.can('KPI_CREATE'));

  reportForm = this.fb.nonNullable.group({
    studyId: this.fb.control<number | null>(null, { validators: [Validators.required] }),
    scope: ['ENROLLMENT' as ReportScope, Validators.required],
    parametersJson: ['{}'],
    reportingPeriod: ['', Validators.required]
  });

  kpiForm = this.fb.nonNullable.group({
    name: ['', Validators.required],
    definition: ['', Validators.required],
    target: [100, Validators.required],
    reportingPeriod: ['', Validators.required]
  });

  ngOnInit() {
    if (this.canViewReports()) this.loadReports();
    if (this.canViewKpis()) this.kApi.list().subscribe(v => this.kpis.set(v ?? []));
    if (this.auth.can('STUDY_LIST')) this.studyApi.list().subscribe(v => this.studies.set(v ?? []));
  }

  loadReports() {
    const sc = this.scopeFilter();
    if (sc) this.rApi.byScope(sc as ReportScope).subscribe(v => this.reports.set(v ?? []));
    else this.rApi.list().subscribe(v => this.reports.set(v ?? []));
  }

  openReport() {
    this.reportForm.reset({ studyId: null, scope: 'ENROLLMENT', parametersJson: '{}', reportingPeriod: '' });
    this.reportOpen.set(true);
  }
  submitReport() {
    if (this.reportForm.invalid) return;
    this.rApi.create(this.reportForm.getRawValue() as any).subscribe({
      next: () => { this.toast.success('Report created'); this.reportOpen.set(false); this.loadReports(); },
      error: e => this.toast.error(extractErrorMessage(e, 'Failed'))
    });
  }

  openKpi() {
    this.kpiForm.reset({ name: '', definition: '', target: 100, reportingPeriod: '' });
    this.kpiOpen.set(true);
  }
  submitKpi() {
    if (this.kpiForm.invalid) return;
    this.kApi.create(this.kpiForm.getRawValue() as any).subscribe({
      next: () => {
        this.toast.success('KPI created'); this.kpiOpen.set(false);
        this.kApi.list().subscribe(v => this.kpis.set(v ?? []));
      },
      error: e => this.toast.error(extractErrorMessage(e, 'Failed'))
    });
  }

  meter(k: KPIResponseDTO) {
    return k.target ? Math.min(100, Math.round((k.currentValue / k.target) * 100)) : 0;
  }

  fileName(uri: string): string {
    if (!uri) return '';
    return uri.split(/[/\\]/).pop() ?? uri;
  }

  downloadReport(reportId: number, filename: string) {
    this.rApi.downloadReport(reportId).subscribe({
      next: blob => {
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = filename || `report-${reportId}.json`;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(url);
        this.toast.success('Download started');
      },
      error: e => this.toast.error(extractErrorMessage(e, 'Report file could not be downloaded.'))
    });
  }
}

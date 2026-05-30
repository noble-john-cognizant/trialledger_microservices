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
import { SpinnerComponent } from '../../shared/spinner/spinner.component';

@Component({
  selector: 'tl-reports',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, DatePipe, JsonPipe, ModalComponent, SpinnerComponent],
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
  loading = signal(true);
  error = signal<string | null>(null);

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
    this.loading.set(true);
    this.error.set(null);
    const sc = this.scopeFilter();
    const obs = sc ? this.rApi.byScope(sc as ReportScope) : this.rApi.list();
    obs.subscribe({
      next: v => { this.reports.set(v ?? []); this.loading.set(false); },
      error: e => { this.error.set(extractErrorMessage(e, 'Could not load reports.')); this.loading.set(false); }
    });
  }

  openReport() {
    this.reportForm.reset({ studyId: null, scope: 'ENROLLMENT', parametersJson: '{}', reportingPeriod: '' });
    this.reportOpen.set(true);
  }
  submitReport() {
    if (this.reportForm.invalid) return;
    this.rApi.create(this.reportForm.getRawValue() as any).subscribe({
      next: () => { this.toast.success('Report created'); this.reportOpen.set(false); this.loadReports(); }
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
      }
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
      }
    });
  }
}

import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule, DatePipe, JsonPipe } from '@angular/common';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ProvenanceService } from '../../core/services/provenance.service';
import { StudyService } from '../../core/services/study.service';
import { AuthService } from '../../core/auth/auth.service';
import { ToastService } from '../../core/services/toast.service';
import { ProvenanceDTO, DatasetSnapshot, AuditPackage } from '../../core/models/provenance.models';
import { StudyResponseDto } from '../../core/models/study.models';
import { ModalComponent } from '../../shared/modal/modal.component';
import { EmptyStateComponent } from '../../shared/empty-state/empty-state.component';

@Component({
  selector: 'tl-provenance',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, DatePipe, JsonPipe, ModalComponent, EmptyStateComponent],
  templateUrl: './provenance.component.html',
  styleUrls: ['./provenance.component.css']
})
export class ProvenanceComponent implements OnInit {
  private api = inject(ProvenanceService);
  private studyApi = inject(StudyService);
  private toast = inject(ToastService);
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);

  tab = signal<'records' | 'snapshots' | 'packages'>('records');
  records = signal<ProvenanceDTO[]>([]);
  page = signal(0);
  totalPages = signal(1);

  studies = signal<StudyResponseDto[]>([]);
  snapshots = signal<DatasetSnapshot[]>([]);
  packages = signal<AuditPackage[]>([]);

  snapStudyId = signal<number | null>(null);
  pkgStudyId = signal<number | null>(null);

  snapshotOpen = signal(false);
  packageOpen = signal(false);
  newSnapshotStudy: number | null = null;

  canViewRecords = computed(() => this.auth.can('PROVENANCE_VIEW'));
  canViewSnapshots = computed(() => this.auth.can('SNAPSHOT_VIEW'));
  canCreateSnapshot = computed(() => this.auth.can('SNAPSHOT_CREATE'));
  canViewPackages = computed(() => this.auth.can('PACKAGE_VIEW'));
  canCreatePackage = computed(() => this.auth.can('PACKAGE_CREATE'));
  canDownloadPackage = computed(() => this.auth.can('PACKAGE_DOWNLOAD'));

  pkgForm = this.fb.nonNullable.group({
    studyId: this.fb.control<number | null>(null, { validators: [Validators.required] }),
    startDate: ['', Validators.required],
    endDate: ['', Validators.required]
  });

  ngOnInit() {
    if (this.canViewRecords()) this.loadRecords();
    else if (this.canViewSnapshots()) this.tab.set('snapshots');
    else if (this.canViewPackages()) this.tab.set('packages');

    if (this.auth.can('STUDY_LIST')) this.studyApi.list().subscribe(v => this.studies.set(v ?? []));
  }

  loadRecords() {
    this.api.page(this.page(), 20).subscribe({
      next: r => {
        this.records.set(r?.content ?? []);
        this.totalPages.set(r?.totalPages ?? 1);
      }
    });
  }
  changePage(p: number) { this.page.set(p); this.loadRecords(); }

  loadSnapshots() {
    if (this.snapStudyId()) this.api.snapshots(this.snapStudyId()!).subscribe(v => this.snapshots.set(v ?? []));
  }
  loadPackages() {
    if (this.pkgStudyId()) this.api.packages(this.pkgStudyId()!).subscribe(v => this.packages.set(v ?? []));
  }

  createSnapshot() {
    if (!this.newSnapshotStudy) return;
    this.api.createSnapshot(this.newSnapshotStudy).subscribe({
      next: () => {
        this.toast.success('Snapshot created'); this.snapshotOpen.set(false);
        this.snapStudyId.set(this.newSnapshotStudy);
        this.loadSnapshots(); this.tab.set('snapshots');
      }
    });
  }

  createPackage() {
    if (this.pkgForm.invalid) return;
    this.api.createPackage(this.pkgForm.getRawValue() as any).subscribe({
      next: () => {
        this.toast.success('Audit package generated'); this.packageOpen.set(false);
        this.pkgStudyId.set(this.pkgForm.value.studyId ?? null);
        this.loadPackages(); this.tab.set('packages');
      }
    });
  }

  download(p: AuditPackage) {
    this.api.downloadPackage(p.packageId).subscribe(blob => {
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url; a.download = `audit-package-${p.packageId}.zip`; a.click();
      URL.revokeObjectURL(url);
    });
  }
}

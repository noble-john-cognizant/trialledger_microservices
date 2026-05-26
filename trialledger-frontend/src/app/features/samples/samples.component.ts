// import { Component, OnInit, computed, inject, signal } from '@angular/core';
// import { CommonModule, DatePipe } from '@angular/common';
// import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidatorFn, Validators } from '@angular/forms';
// import { SampleService } from '../../core/services/sample.service';
// import { ParticipantService } from '../../core/services/participant.service';
// import { StudyService } from '../../core/services/study.service';
// import { AuthService } from '../../core/auth/auth.service';
// import { ToastService } from '../../core/services/toast.service';
// import { extractErrorMessage } from '../../core/utils/error-message';
// import {
//   SampleResponseDTO, SampleStatus, SampleType,
//   ChainOfCustodyResponseDTO, AssayRunResponseDTO, SampleStorageResponseDTO,
//   ALL_SAMPLE_STATUSES, ALL_SAMPLE_TYPES
// } from '../../core/models/sample.models';
// import { ParticipantResponseDTO } from '../../core/models/participant.models';
// import { StudyResponseDto } from '../../core/models/study.models';
// import { StatusBadgeComponent } from '../../shared/status-badge/status-badge.component';
// import { ModalComponent } from '../../shared/modal/modal.component';
// import { EmptyStateComponent } from '../../shared/empty-state/empty-state.component';
// import { SearchSelectComponent, SearchOption } from '../../shared/search-select/search-select.component';
// import { catchError, of } from 'rxjs';
//
// @Component({
//   selector: 'tl-samples',
//   standalone: true,
//   imports: [CommonModule, ReactiveFormsModule, DatePipe,
//     StatusBadgeComponent, ModalComponent, EmptyStateComponent, SearchSelectComponent],
//   templateUrl: './samples.component.html',
//   styleUrls: ['./samples.component.css']
// })
// export class SamplesComponent implements OnInit {
//   private api = inject(SampleService);
//   private partApi = inject(ParticipantService);
//   private studyApi = inject(StudyService);
//   private toast = inject(ToastService);
//   private fb = inject(FormBuilder);
//   private auth = inject(AuthService);
//
//   statuses = ALL_SAMPLE_STATUSES;
//   types = ALL_SAMPLE_TYPES;
//
//   list = signal<SampleResponseDTO[]>([]);
//   participants = signal<ParticipantResponseDTO[]>([]);
//   studies = signal<StudyResponseDto[]>([]);
//
//   search = signal('');
//   statusFilter = signal('');
//   typeFilter = signal('');
//
//   createOpen = signal(false);
//   detailsOpen = signal(false);
//   transferOpen = signal(false);
//   storeOpen = signal(false);
//   assayOpen = signal(false);
//
//   selected = signal<SampleResponseDTO | null>(null);
//   custody = signal<ChainOfCustodyResponseDTO[]>([]);
//   storage = signal<SampleStorageResponseDTO[]>([]);
//   assays = signal<AssayRunResponseDTO[]>([]);
//
//   /** Participant looked up when user picks/types a participant ID in the log form. */
//   lookedUpParticipant = signal<ParticipantResponseDTO | null>(null);
//   participantLookupError = signal<string>('');
//
//   canCreate = computed(() => this.auth.can('SAMPLE_CREATE'));
//   canUpdate = computed(() => this.auth.can('SAMPLE_UPDATE'));
//   canCustody = computed(() => this.auth.can('CUSTODY_CREATE'));
//   canStorage = computed(() => this.auth.can('STORAGE_CREATE'));
//   canAssay = computed(() => this.auth.can('ASSAY_CREATE'));
//   canViewCustody = computed(() => this.auth.can('CUSTODY_VIEW'));
//   canViewStorage = computed(() => this.auth.can('STORAGE_VIEW'));
//   canViewAssay = computed(() => this.auth.can('ASSAY_VIEW'));
//   canListParticipants = computed(() => this.auth.can('PARTICIPANT_LIST'));
//   canListStudy = computed(() => this.auth.can('STUDY_LIST'));
//
//   participantOptions = computed<SearchOption[]>(() =>
//     this.participants().map(p => ({
//       id: p.participantId, label: p.name,
//       subtitle: `${p.externalId} · Study #${p.studyId}`
//     }))
//   );
//   studyOptions = computed<SearchOption[]>(() =>
//     this.studies().map(s => ({
//       id: s.studyId, label: s.title,
//       subtitle: `${s.sponsor} · #${s.protocolNumber}`
//     }))
//   );
//
//   /** True only when participant exists and is ENROLLED (blocks submit if lookup failed). */
//   participantValid = computed(() => {
//     const err = this.participantLookupError();
//     const p = this.lookedUpParticipant();
//     if (err) return false;
//     if (p) return p.enrollmentStatus === 'ENROLLED';
//     return true;
//   });
//
//   /** Validator: toUser must not equal fromUser (case-insensitive). */
//   private notSameUser(): ValidatorFn {
//     return (control: AbstractControl) => {
//       const from = this.transferForm?.controls.fromUser.value?.trim().toLowerCase();
//       const to = (control.value ?? '').trim().toLowerCase();
//       return from && to && from === to ? { sameUser: true } : null;
//     };
//   }
//
//   setParticipant(id: number | null) {
//     this.form.patchValue({ participantId: id });
//     this.lookedUpParticipant.set(null);
//     this.participantLookupError.set('');
//     if (!id) return;
//     this.partApi.get(id).pipe(
//       catchError(() => {
//         this.participantLookupError.set('Participant #' + id + ' not found.');
//         return of(null);
//       })
//     ).subscribe(p => {
//       if (!p) return;
//       this.lookedUpParticipant.set(p);
//       if (p.enrollmentStatus !== 'ENROLLED') {
//         this.participantLookupError.set(
//           'Participant is ' + p.enrollmentStatus + '. Only ENROLLED participants can have samples logged.'
//         );
//       }
//     });
//   }
//
//   setStudy(id: number | null) { this.form.patchValue({ studyId: id }); }
//
//   filtered = computed(() => {
//     const q = this.search().toLowerCase();
//     return this.list().filter(s =>
//       (!this.statusFilter() || s.status === this.statusFilter()) &&
//       (!this.typeFilter() || s.sampleType === this.typeFilter()) &&
//       (!q || `${s.sampleId} ${s.sampleType} ${s.initialLocation}`.toLowerCase().includes(q))
//     );
//   });
//
//   form = this.fb.nonNullable.group({
//     participantId: this.fb.control<number | null>(null, { validators: [Validators.required] }),
//     studyId: this.fb.control<number | null>(null, { validators: [Validators.required] }),
//     sampleType: ['BLOOD' as SampleType, Validators.required],
//     collectedBy: ['', Validators.required],
//     initialLocation: ['', Validators.required],
//     status: ['COLLECTED' as SampleStatus, Validators.required]
//   });
//
//   transferForm = this.fb.nonNullable.group({
//     fromUser: [{ value: '', disabled: true }],
//     toUser: ['', [Validators.required, Validators.minLength(2)]],
//     fromLocation: ['', Validators.required],
//     toLocation: ['', Validators.required],
//     notes: ['']
//   });
//
//   storeForm = this.fb.nonNullable.group({
//     freezerId: [1, Validators.required], shelf: ['', Validators.required],
//     box: ['', Validators.required], position: ['', Validators.required]
//   });
//   assayForm = this.fb.nonNullable.group({
//     instrumentId: [1, Validators.required], operatorId: [1, Validators.required],
//     protocolRef: ['', Validators.required],
//     resultUri: [''],
//     metadataJson: ['{}']
//   });
//
//   ngOnInit() {
//     this.load();
//     if (this.canListParticipants()) this.partApi.list().subscribe(v => this.participants.set(v ?? []));
//     if (this.canListStudy()) this.studyApi.list().subscribe(v => this.studies.set(v ?? []));
//   }
//
//   load() { this.api.list().subscribe({ next: v => this.list.set(v ?? []) }); }
//
//   openCreate() {
//     this.form.reset({
//       participantId: null, studyId: null, sampleType: 'BLOOD',
//       collectedBy: this.auth.user()?.name ?? '',
//       initialLocation: '', status: 'COLLECTED'
//     });
//     this.lookedUpParticipant.set(null);
//     this.participantLookupError.set('');
//     this.createOpen.set(true);
//   }
//
//   submit() {
//     if (this.form.invalid) return;
//     this.api.create(this.form.getRawValue() as any).subscribe({
//       next: () => { this.toast.success('Sample logged'); this.createOpen.set(false); this.load(); },
//       error: e => this.toast.error(extractErrorMessage(e, 'Failed'))
//     });
//   }
//
//   updateStatus(s: SampleResponseDTO, status: string, selectEl: HTMLSelectElement) {
//     if (!status || status === s.status) return;
//     this.api.updateStatus(s.sampleId, status as SampleStatus).subscribe({
//       next: updated => {
//         this.toast.success('Status updated to ' + updated.status);
//         this.load();
//         if (this.selected()?.sampleId === s.sampleId) this.selected.set(updated);
//       },
//       error: e => {
//         selectEl.value = s.status;
//         this.toast.error(extractErrorMessage(e, 'Status update failed'));
//       }
//     });
//   }
//
//   openDetails(s: SampleResponseDTO) {
//     this.selected.set(s);
//     this.detailsOpen.set(true);
//     if (this.canViewCustody()) this.api.custodyForSample(s.sampleId).subscribe(v => this.custody.set(v ?? []));
//     if (this.canViewStorage()) this.api.storageHistory(s.sampleId).subscribe(v => this.storage.set(v ?? []));
//     if (this.canViewAssay()) this.api.assaysForSample(s.sampleId).subscribe(v => this.assays.set(v ?? []));
//   }
//
//   openTransfer() {
//     const currentUserName = this.auth.user()?.name ?? '';
//     this.transferForm.reset({ toUser: '', fromLocation: '', toLocation: '', notes: '' });
//     this.transferForm.controls.fromUser.setValue(currentUserName);
//     // Apply cross-field validator now that transferForm is initialised
//     this.transferForm.controls.toUser.setValidators([
//       Validators.required,
//       Validators.minLength(2),
//       this.notSameUser()
//     ]);
//     this.transferForm.controls.toUser.updateValueAndValidity();
//     this.transferOpen.set(true);
//   }
//
//   submitTransfer() {
//     const s = this.selected(); if (!s) return;
//     this.api.addCustody({
//       sampleId: s.sampleId,
//       ...this.transferForm.getRawValue(),
//       fromUser: this.transferForm.controls.fromUser.value
//     }).subscribe({
//       next: () => {
//         this.toast.success('Custody transferred'); this.transferOpen.set(false);
//         this.api.custodyForSample(s.sampleId).subscribe(v => this.custody.set(v ?? []));
//       },
//       error: e => this.toast.error(extractErrorMessage(e, 'Failed'))
//     });
//   }
//
//   openStore() {
//     this.storeForm.reset({ freezerId: 1, shelf: '', box: '', position: '' });
//     this.storeOpen.set(true);
//   }
//   submitStore() {
//     const s = this.selected(); if (!s) return;
//     this.api.storeSample(s.sampleId, this.storeForm.getRawValue()).subscribe({
//       next: () => {
//         this.toast.success('Stored'); this.storeOpen.set(false);
//         this.api.storageHistory(s.sampleId).subscribe(v => this.storage.set(v ?? []));
//       },
//       error: e => this.toast.error(extractErrorMessage(e, 'Failed'))
//     });
//   }
//
//   openAssay() {
//     this.assayForm.reset({
//       instrumentId: 1, operatorId: this.auth.user()?.userId ?? 1,
//       protocolRef: '', resultUri: '', metadataJson: '{}'
//     });
//     this.assayOpen.set(true);
//   }
//   submitAssay() {
//     const s = this.selected(); if (!s) return;
//     this.api.createAssay({ sampleId: s.sampleId, ...this.assayForm.getRawValue() }).subscribe({
//       next: () => {
//         this.toast.success('Assay logged'); this.assayOpen.set(false);
//         this.api.assaysForSample(s.sampleId).subscribe(v => this.assays.set(v ?? []));
//       },
//       error: e => this.toast.error(extractErrorMessage(e, 'Failed'))
//     });
//   }
// }












import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidatorFn, Validators } from '@angular/forms';
import { SampleService } from '../../core/services/sample.service';
import { ParticipantService } from '../../core/services/participant.service';
import { StudyService } from '../../core/services/study.service';
import { AuthService } from '../../core/auth/auth.service';
import { ToastService } from '../../core/services/toast.service';
import { extractErrorMessage } from '../../core/utils/error-message';
import {
  SampleResponseDTO, SampleStatus, SampleType,
  ChainOfCustodyResponseDTO, AssayRunResponseDTO, SampleStorageResponseDTO,
  ALL_SAMPLE_STATUSES, ALL_SAMPLE_TYPES
} from '../../core/models/sample.models';
import { ParticipantResponseDTO } from '../../core/models/participant.models';
import { StudyResponseDto } from '../../core/models/study.models';
import { StatusBadgeComponent } from '../../shared/status-badge/status-badge.component';
import { ModalComponent } from '../../shared/modal/modal.component';
import { EmptyStateComponent } from '../../shared/empty-state/empty-state.component';
import { SearchSelectComponent, SearchOption } from '../../shared/search-select/search-select.component';

@Component({
  selector: 'tl-samples',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, DatePipe,
    StatusBadgeComponent, ModalComponent, EmptyStateComponent, SearchSelectComponent],
  templateUrl: './samples.component.html',
  styleUrls: ['./samples.component.css']
})
export class SamplesComponent implements OnInit {
  api = inject(SampleService);
  private partApi = inject(ParticipantService);
  private studyApi = inject(StudyService);
  private toast = inject(ToastService);
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);

  statuses = ALL_SAMPLE_STATUSES;
  types = ALL_SAMPLE_TYPES;

  list = signal<SampleResponseDTO[]>([]);
  participants = signal<ParticipantResponseDTO[]>([]);
  studies = signal<StudyResponseDto[]>([]);

  search = signal('');
  statusFilter = signal('');
  typeFilter = signal('');

  createOpen = signal(false);
  detailsOpen = signal(false);
  transferOpen = signal(false);
  storeOpen = signal(false);
  assayOpen = signal(false);

  selected = signal<SampleResponseDTO | null>(null);
  custody = signal<ChainOfCustodyResponseDTO[]>([]);
  storage = signal<SampleStorageResponseDTO[]>([]);
  assays = signal<AssayRunResponseDTO[]>([]);

  canCreate = computed(() => this.auth.can('SAMPLE_CREATE'));
  canUpdate = computed(() => this.auth.can('SAMPLE_UPDATE'));
  canCustody = computed(() => this.auth.can('CUSTODY_CREATE'));
  canStorage = computed(() => this.auth.can('STORAGE_CREATE'));
  canAssay = computed(() => this.auth.can('ASSAY_CREATE'));
  canViewCustody = computed(() => this.auth.can('CUSTODY_VIEW'));
  canViewStorage = computed(() => this.auth.can('STORAGE_VIEW'));
  canViewAssay = computed(() => this.auth.can('ASSAY_VIEW'));
  canListParticipants = computed(() => this.auth.can('PARTICIPANT_LIST'));
  canListStudy = computed(() => this.auth.can('STUDY_LIST'));

  participantOptions = computed<SearchOption[]>(() =>
    this.participants().map(p => ({
      id: p.participantId, label: p.name,
      subtitle: `${p.externalId} · Study #${p.studyId}`
    }))
  );
  studyOptions = computed<SearchOption[]>(() =>
    this.studies().map(s => ({
      id: s.studyId, label: s.title,
      subtitle: `${s.sponsor} · #${s.protocolNumber}`
    }))
  );

  /** Validator: toUser must not equal fromUser (case-insensitive). */
  private notSameUser(): ValidatorFn {
    return (control: AbstractControl) => {
      const from = this.transferForm?.controls.fromUser.value?.trim().toLowerCase();
      const to = (control.value ?? '').trim().toLowerCase();
      return from && to && from === to ? { sameUser: true } : null;
    };
  }

  /** Extract just the filename from a full server file path for display. */
  fileName(uri: string): string {
    if (!uri) return '';
    return uri.split(/[/\\]/).pop() ?? uri;
  }

  /** Trigger a browser file download for the given assay result. */
  downloadResult(assayId: number, filename: string) {
    this.api.downloadAssayResult(assayId).subscribe({
      next: blob => {
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = filename || `assay-${assayId}-result.json`;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(url);
        this.toast.success('Download started');
      },
      error: e => this.toast.error(extractErrorMessage(e, 'Result file could not be downloaded.'))
    });
  }

  setParticipant(id: number | null) { this.form.patchValue({ participantId: id }); }
  setStudy(id: number | null)       { this.form.patchValue({ studyId: id }); }

  filtered = computed(() => {
    const q = this.search().toLowerCase();
    return this.list().filter(s =>
      (!this.statusFilter() || s.status === this.statusFilter()) &&
      (!this.typeFilter() || s.sampleType === this.typeFilter()) &&
      (!q || `${s.sampleId} ${s.sampleType} ${s.initialLocation}`.toLowerCase().includes(q))
    );
  });

  form = this.fb.nonNullable.group({
    participantId: this.fb.control<number | null>(null, { validators: [Validators.required] }),
    studyId: this.fb.control<number | null>(null, { validators: [Validators.required] }),
    sampleType: ['BLOOD' as SampleType, Validators.required],
    collectedBy: ['', Validators.required],
    initialLocation: ['', Validators.required],
    status: ['COLLECTED' as SampleStatus, Validators.required]
  });

  transferForm = this.fb.nonNullable.group({
    fromUser: [{ value: '', disabled: true }],
    toUser: ['', [Validators.required, Validators.minLength(2)]],
    fromLocation: ['', Validators.required],
    toLocation: ['', Validators.required],
    notes: ['']
  });

  storeForm = this.fb.nonNullable.group({
    freezerId: [1, Validators.required], shelf: ['', Validators.required],
    box: ['', Validators.required], position: ['', Validators.required]
  });
  assayForm = this.fb.nonNullable.group({
    instrumentId: [1, Validators.required], operatorId: [1, Validators.required],
    protocolRef: ['', Validators.required],
    resultUri: [''],
    metadataJson: ['{}']
  });

  ngOnInit() {
    this.load();
    if (this.canListParticipants()) this.partApi.list().subscribe(v => this.participants.set(v ?? []));
    if (this.canListStudy()) this.studyApi.list().subscribe(v => this.studies.set(v ?? []));
  }

  load() { this.api.list().subscribe({ next: v => this.list.set(v ?? []) }); }

  openCreate() {
    this.form.reset({
      participantId: null, studyId: null, sampleType: 'BLOOD',
      collectedBy: this.auth.user()?.name ?? '',
      initialLocation: '', status: 'COLLECTED'
    });
    this.createOpen.set(true);
  }

  submit() {
    if (this.form.invalid) return;
    // Let the backend validate participant and study — errors shown via toast
    this.api.create(this.form.getRawValue() as any).subscribe({
      next: () => { this.toast.success('Sample logged'); this.createOpen.set(false); this.load(); },
      error: e => this.toast.error(extractErrorMessage(e, 'Failed to log sample'))
    });
  }

  updateStatus(s: SampleResponseDTO, status: string, selectEl: HTMLSelectElement) {
    if (!status || status === s.status) return;
    this.api.updateStatus(s.sampleId, status as SampleStatus).subscribe({
      next: updated => {
        this.toast.success('Status updated to ' + updated.status);
        this.load();
        if (this.selected()?.sampleId === s.sampleId) this.selected.set(updated);
      },
      error: e => {
        selectEl.value = s.status;
        this.toast.error(extractErrorMessage(e, 'Status update failed'));
      }
    });
  }

  openDetails(s: SampleResponseDTO) {
    this.selected.set(s);
    this.detailsOpen.set(true);
    if (this.canViewCustody()) this.api.custodyForSample(s.sampleId).subscribe(v => this.custody.set(v ?? []));
    if (this.canViewStorage()) this.api.storageHistory(s.sampleId).subscribe(v => this.storage.set(v ?? []));
    if (this.canViewAssay()) this.api.assaysForSample(s.sampleId).subscribe(v => this.assays.set(v ?? []));
  }

  openTransfer() {
    const currentUserName = this.auth.user()?.name ?? '';
    this.transferForm.reset({ toUser: '', fromLocation: '', toLocation: '', notes: '' });
    this.transferForm.controls.fromUser.setValue(currentUserName);
    this.transferForm.controls.toUser.setValidators([
      Validators.required,
      Validators.minLength(2),
      this.notSameUser()
    ]);
    this.transferForm.controls.toUser.updateValueAndValidity();
    this.transferOpen.set(true);
  }

  submitTransfer() {
    const s = this.selected(); if (!s) return;
    this.api.addCustody({
      sampleId: s.sampleId,
      ...this.transferForm.getRawValue(),
      fromUser: this.transferForm.controls.fromUser.value
    }).subscribe({
      next: () => {
        this.toast.success('Custody transferred'); this.transferOpen.set(false);
        this.api.custodyForSample(s.sampleId).subscribe(v => this.custody.set(v ?? []));
      },
      error: e => this.toast.error(extractErrorMessage(e, 'Failed'))
    });
  }

  openStore() {
    this.storeForm.reset({ freezerId: 1, shelf: '', box: '', position: '' });
    this.storeOpen.set(true);
  }
  submitStore() {
    const s = this.selected(); if (!s) return;
    this.api.storeSample(s.sampleId, this.storeForm.getRawValue()).subscribe({
      next: () => {
        this.toast.success('Stored'); this.storeOpen.set(false);
        this.api.storageHistory(s.sampleId).subscribe(v => this.storage.set(v ?? []));
      },
      error: e => this.toast.error(extractErrorMessage(e, 'Failed'))
    });
  }

  openAssay() {
    this.assayForm.reset({
      instrumentId: 1, operatorId: this.auth.user()?.userId ?? 1,
      protocolRef: '', resultUri: '', metadataJson: '{}'
    });
    this.assayOpen.set(true);
  }
  submitAssay() {
    const s = this.selected(); if (!s) return;
    this.api.createAssay({ sampleId: s.sampleId, ...this.assayForm.getRawValue() }).subscribe({
      next: () => {
        this.toast.success('Assay logged'); this.assayOpen.set(false);
        this.api.assaysForSample(s.sampleId).subscribe(v => this.assays.set(v ?? []));
      },
      error: e => this.toast.error(extractErrorMessage(e, 'Failed'))
    });
  }
}

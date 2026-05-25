import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AlertRuleService } from '../../../core/services/alert-rule.service';
import { AuthService } from '../../../core/auth/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { extractErrorMessage } from '../../../core/utils/error-message';
import { AlertRuleResponseDTO, AlertSeverity, ALL_ALERT_SEVERITIES } from '../../../core/models/notification.models';
import { StatusBadgeComponent } from '../../../shared/status-badge/status-badge.component';
import { ModalComponent } from '../../../shared/modal/modal.component';
import { EmptyStateComponent } from '../../../shared/empty-state/empty-state.component';

@Component({
  selector: 'tl-alert-rules',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, StatusBadgeComponent, ModalComponent, EmptyStateComponent],
  templateUrl: './alert-rules.component.html',
  styleUrls: ['./alert-rules.component.css']
})
export class AlertRulesComponent implements OnInit {
  private api = inject(AlertRuleService);
  private toast = inject(ToastService);
  private auth = inject(AuthService);
  private fb = inject(FormBuilder);

  severities = ALL_ALERT_SEVERITIES;
  list = signal<AlertRuleResponseDTO[]>([]);
  open = signal(false);
  editing = signal<AlertRuleResponseDTO | null>(null);

  canManage = computed(() => this.auth.can('ALERT_MANAGE'));

  form = this.fb.nonNullable.group({
    name: ['', Validators.required],
    triggerExpression: ['', Validators.required],
    severity: ['MEDIUM' as AlertSeverity, Validators.required],
    recipientsJson: ['[]', Validators.required],
    active: [true]
  });

  ngOnInit() { this.load(); }
  load() { this.api.list().subscribe(v => this.list.set(v ?? [])); }

  openCreate() {
    this.editing.set(null);
    this.form.reset({ name: '', triggerExpression: '', severity: 'MEDIUM', recipientsJson: '[]', active: true });
    this.open.set(true);
  }

  openEdit(r: AlertRuleResponseDTO) {
    this.editing.set(r);
    this.form.setValue({
      name: r.name,
      triggerExpression: r.triggerExpression,
      severity: r.severity,
      recipientsJson: r.recipientsJson,
      active: r.active
    });
    this.open.set(true);
  }

  submit() {
    if (this.form.invalid) return;
    const dto = this.form.getRawValue();
    const e = this.editing();
    const op = e ? this.api.update(e.ruleId, dto) : this.api.create(dto);
    op.subscribe({
      next: () => { this.toast.success('Saved'); this.open.set(false); this.load(); },
      error: er => this.toast.error(extractErrorMessage(er, 'Failed'))
    });
  }

  toggle(r: AlertRuleResponseDTO) {
    this.api.toggle(r.ruleId).subscribe({
      next: () => this.load(),
      error: e => this.toast.error(extractErrorMessage(e, 'Failed'))
    });
  }

  del(r: AlertRuleResponseDTO) {
    if (!confirm(`Delete rule "${r.name}"?`)) return;
    this.api.delete(r.ruleId).subscribe({
      next: () => { this.toast.success('Deleted'); this.load(); }
    });
  }
}

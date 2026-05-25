import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { ToastsComponent } from '../../../shared/toasts/toasts.component';

@Component({
  selector: 'tl-forgot-username',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, ToastsComponent],
  templateUrl: './forgot-username.component.html',
  styleUrls: ['../auth-shared.css']
})
export class ForgotUsernameComponent {
  private auth = inject(AuthService);
  private toast = inject(ToastService);
  private fb = inject(FormBuilder);

  loading = signal(false);
  result = signal<string | null>(null);
  form = this.fb.nonNullable.group({
    phoneNumber: ['', [Validators.required]],
    password: ['', [Validators.required]]
  });

  submit() {
    if (this.form.invalid) return;
    this.loading.set(true);
    this.auth.forgotUsername(this.form.getRawValue()).subscribe({
      next: r => { this.loading.set(false); this.result.set(r); },
      error: e => { this.loading.set(false); this.toast.error(e?.error?.message ?? 'Not found'); }
    });
  }
}

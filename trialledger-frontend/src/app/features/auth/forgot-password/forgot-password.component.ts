import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { ToastsComponent } from '../../../shared/toasts/toasts.component';

@Component({
  selector: 'tl-forgot-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, ToastsComponent],
  templateUrl: './forgot-password.component.html',
  styleUrls: ['../auth-shared.css']
})
export class ForgotPasswordComponent {
  private auth = inject(AuthService);
  private router = inject(Router);
  private toast = inject(ToastService);
  private fb = inject(FormBuilder);

  loading = signal(false);
  form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    newPassword: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(50)]]
  });

  submit() {
    if (this.form.invalid) return;
    this.loading.set(true);
    this.auth.forgotPassword(this.form.getRawValue()).subscribe({
      next: m => { this.loading.set(false); this.toast.success(m); this.router.navigate(['/login']); },
      error: e => { this.loading.set(false); this.toast.error(e?.error?.message ?? 'Reset failed'); }
    });
  }
}

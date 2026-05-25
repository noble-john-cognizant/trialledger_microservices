import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { ToastsComponent } from '../../../shared/toasts/toasts.component';
import { extractErrorMessage } from '../../../core/utils/error-message';

/**
 * Two-step password reset:
 *   1. User enters email -> server generates a 6-digit OTP and logs it to
 *      the auth-service console.
 *   2. User enters that OTP plus a new password -> server validates and
 *      updates the password.
 */
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

  step = signal<1 | 2>(1);
  requesting = signal(false);
  resetting = signal(false);
  /** Echoed in the UI between steps so the user knows where the OTP went. */
  sentTo = signal<string>('');

  /** Step 1 form */
  emailForm = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]]
  });

  /** Step 2 form */
  resetForm = this.fb.nonNullable.group({
    otp:         ['', [Validators.required, Validators.pattern(/^\d{6}$/)]],
    newPassword: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(50)]]
  });

  requestOtp() {
    if (this.emailForm.invalid) return;
    this.requesting.set(true);
    const email = this.emailForm.controls.email.value.trim();
    this.auth.requestPasswordResetOtp({ email }).subscribe({
      next: msg => {
        this.requesting.set(false);
        this.sentTo.set(email);
        this.step.set(2);
        this.toast.success(msg || 'OTP generated — check the server console.');
      },
      error: e => {
        this.requesting.set(false);
        this.toast.error(extractErrorMessage(e, 'Could not request OTP.'));
      }
    });
  }

  submit() {
    if (this.resetForm.invalid) return;
    this.resetting.set(true);
    const { otp, newPassword } = this.resetForm.getRawValue();
    this.auth.forgotPassword({ email: this.sentTo(), otp, newPassword }).subscribe({
      next: m => {
        this.resetting.set(false);
        this.toast.success(m || 'Password updated.');
        this.router.navigate(['/login']);
      },
      error: e => {
        this.resetting.set(false);
        this.toast.error(extractErrorMessage(e, 'Reset failed.'));
      }
    });
  }

  resendOtp() {
    // re-fire step 1 — the server replaces the previous OTP for this email
    this.requesting.set(true);
    this.auth.requestPasswordResetOtp({ email: this.sentTo() }).subscribe({
      next: () => {
        this.requesting.set(false);
        this.toast.success('A new OTP was generated — check the server console.');
      },
      error: e => {
        this.requesting.set(false);
        this.toast.error(extractErrorMessage(e, 'Could not resend OTP.'));
      }
    });
  }

  backToEmail() {
    this.resetForm.reset();
    this.step.set(1);
  }
}

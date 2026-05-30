import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { extractErrorMessage } from '../../../core/utils/error-message';
import { ToastsComponent } from '../../../shared/toasts/toasts.component';

@Component({
  selector: 'tl-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, ToastsComponent],
  templateUrl: './login.component.html',
  styleUrls: ['../auth-shared.css']
})
export class LoginComponent {
  private auth = inject(AuthService);
  private router = inject(Router);
  private toast = inject(ToastService);
  private fb = inject(FormBuilder);

  loading = signal(false);
  form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(50)]]
  });

  submit() {
    if (this.form.invalid) return;
    this.loading.set(true);
    this.auth.login(this.form.getRawValue()).subscribe({
      next: u => {
        this.loading.set(false);
        this.toast.success(`Welcome, ${u.name}`);
        this.router.navigate(['/dashboard']);
      },
      error: err => {
        this.loading.set(false);
        // this.toast.error(extractErrorMessage(err, 'Invalid credentials'));
      }
    });
  }
}

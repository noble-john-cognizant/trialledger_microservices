import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { ToastService } from '../../../core/services/toast.service';
import { ToastsComponent } from '../../../shared/toasts/toasts.component';

@Component({
  selector: 'tl-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, ToastsComponent],
  templateUrl: './register.component.html',
  styleUrls: ['../auth-shared.css']
})
export class RegisterComponent {
  private auth = inject(AuthService);
  private router = inject(Router);
  private toast = inject(ToastService);
  private fb = inject(FormBuilder);

  loading = signal(false);

  form = this.fb.nonNullable.group({
    name:     ['', [Validators.required]],
    email:    ['', [Validators.required, Validators.email]],
    phone:    ['', [Validators.required, Validators.pattern(/^\d{10}$/)]],
    password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(50)]]
  });

  submit() {
    if (this.form.invalid) return;
    this.loading.set(true);
    this.auth.register(this.form.getRawValue()).subscribe({
      next: msg => {
        this.loading.set(false);
        this.toast.success(msg || 'Registered. Please sign in.');
        this.router.navigate(['/login']);
      },
      error: err => {
        this.loading.set(false);
        this.toast.error(err?.error?.message ?? 'Registration failed');
      }
    });
  }
}

import { Component, EventEmitter, Input, Output, OnChanges, SimpleChanges, computed, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../core/auth/auth.service';
import { UserService } from '../../core/services/user.service';
import { ToastService } from '../../core/services/toast.service';
import { ModalComponent } from '../../shared/modal/modal.component';
import { StatusBadgeComponent } from '../../shared/status-badge/status-badge.component';
import { LoginResponseDTO } from '../../core/models/auth.models';
import { UserDTO } from '../../core/models/user.models';

@Component({
  selector: 'tl-user-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, DatePipe, ModalComponent, StatusBadgeComponent],
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css']
})
export class UserProfileComponent implements OnChanges {
  @Input() open = false;
  @Output() close = new EventEmitter<void>();

  private auth = inject(AuthService);
  private userApi = inject(UserService);
  private toast = inject(ToastService);
  private fb = inject(FormBuilder);

  /** Logged-in user shape from the JWT response */
  loggedIn = computed<LoginResponseDTO | null>(() => this.auth.user());

  /** Full details from /api/users/{id} when available */
  fullProfile = signal<UserDTO | null>(null);
  loading = signal(false);
  editing = signal(false);

  /** Whether we can call GET /api/users/{userId} — backend allows ADMIN, PI */
  canFetchFull = computed(() => this.auth.can('USER_VIEW_ONE'));
  canEdit = computed(() => this.auth.can('USER_UPDATE'));

  form = this.fb.nonNullable.group({
    name: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    phone: ['', [Validators.required, Validators.pattern(/^\d{10}$/)]]
  });

  initials = computed(() => {
    const n = this.loggedIn()?.name ?? '';
    return n.split(' ').map(s => s[0]).slice(0, 2).join('').toUpperCase() || 'U';
  });

  ngOnChanges(c: SimpleChanges) {
    if (c['open'] && this.open) {
      this.editing.set(false);
      this.loadFullProfile();
    }
  }

  loadFullProfile() {
    const u = this.loggedIn();
    if (!u ) {
      this.fullProfile.set(null);
      return;
    }
    this.loading.set(true);
    this.userApi.get(u.userId).subscribe({
      next: full => {
        this.fullProfile.set(full);
        this.loading.set(false);
      },
      error: () => {
        this.fullProfile.set(null);
        this.loading.set(false);
      }
    });
  }

  /** Display source — fall back to JWT data when the GET endpoint is forbidden. */
  displayedEmail() { return this.fullProfile()?.email ?? '—'; }
  displayedPhone() { return this.fullProfile()?.phone ?? '—'; }
  displayedCreatedAt() { return this.fullProfile()?.createdAt ?? this.loggedIn()?.createdAt; }

  startEdit() {
    const u = this.fullProfile();
    if (!u) return;
    this.form.setValue({ name: u.name, email: u.email, phone: u.phone });
    this.editing.set(true);
  }

  saveEdit() {
    const u = this.fullProfile();
    if (!u || this.form.invalid) return;
    this.userApi.update(u.userId, this.form.getRawValue()).subscribe({
      next: () => {
        this.toast.success('Profile updated');
        this.editing.set(false);
        this.loadFullProfile();
      },
      error: e => this.toast.error(e?.error?.message ?? 'Update failed')
    });
  }

  signOut() {
    this.close.emit();
    this.auth.logout();
  }
}

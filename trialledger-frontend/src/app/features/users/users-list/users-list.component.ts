import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserService } from '../../../core/services/user.service';
import { ToastService } from '../../../core/services/toast.service';
import { AuthService } from '../../../core/auth/auth.service';
import { extractErrorMessage } from '../../../core/utils/error-message';
import { Role, UserDTO, ALL_ROLES } from '../../../core/models/user.models';
import { StatusBadgeComponent } from '../../../shared/status-badge/status-badge.component';
import { ModalComponent } from '../../../shared/modal/modal.component';
import { SpinnerComponent } from '../../../shared/spinner/spinner.component';

@Component({
  selector: 'tl-users-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, DatePipe, StatusBadgeComponent, ModalComponent, SpinnerComponent],
  templateUrl: './users-list.component.html'
})
export class UsersListComponent implements OnInit {
  private api = inject(UserService);
  private toast = inject(ToastService);
  private auth = inject(AuthService);
  private fb = inject(FormBuilder);

  roles = ALL_ROLES;
  users = signal<UserDTO[]>([]);
  search = signal('');
  roleFilter = signal<string>('');
  loading = signal(true);
  error = signal<string | null>(null);

  canRegister = computed(() => this.auth.can('USER_REGISTER'));
  canUpdate = computed(() => this.auth.can('USER_UPDATE'));

  filtered = computed(() => {
    const s = this.search().toLowerCase();
    const r = this.roleFilter();
    return this.users().filter(u =>
      (!r || u.role === r) &&
      (!s || u.name.toLowerCase().includes(s) || u.email.toLowerCase().includes(s))
    );
  });

  createOpen = signal(false);
  editOpen = signal(false);
  editing = signal<UserDTO | null>(null);

  createForm = this.fb.nonNullable.group({
    role: ['COORDINATOR' as Role, Validators.required],
    name: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    phone: ['', [Validators.required, Validators.pattern(/^\d{10}$/)]],
    password: ['', [Validators.required, Validators.minLength(8)]]
  });

  editForm = this.fb.nonNullable.group({
    name: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    phone: ['', [Validators.required, Validators.pattern(/^\d{10}$/)]]
  });

  ngOnInit() { this.load(); }

  load() {
    this.loading.set(true);
    this.error.set(null);
    this.api.list().subscribe({
      next: u => { this.users.set(u ?? []); this.loading.set(false); },
      error: e => { this.error.set(extractErrorMessage(e, 'Could not load users.')); this.loading.set(false); }
    });
  }

  onSearch(v: string) { this.search.set(v); }
  onRoleFilter(v: string) { this.roleFilter.set(v); }

  openCreate() {
    this.createForm.reset({ role: 'COORDINATOR', name: '', email: '', phone: '', password: '' });
    this.createOpen.set(true);
  }

  submitCreate() {
    if (this.createForm.invalid) return;
    const { role, ...dto } = this.createForm.getRawValue();
    this.api.registerByAdmin(role, dto).subscribe({
      next: () => { this.toast.success('User created'); this.createOpen.set(false); this.load(); }
    });
  }

  openEdit(u: UserDTO) {
    this.editing.set(u);
    this.editForm.setValue({ name: u.name, email: u.email, phone: u.phone });
    this.editOpen.set(true);
  }

  submitEdit() {
    const u = this.editing();
    if (!u || this.editForm.invalid) return;
    this.api.update(u.userId, this.editForm.getRawValue()).subscribe({
      next: () => { this.toast.success('Updated'); this.editOpen.set(false); this.load(); }
    });
  }

  toggleStatus(u: UserDTO) {
    const next = u.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
    this.api.updateStatus(u.userId, next).subscribe({
      next: () => { this.toast.success(`User ${next.toLowerCase()}`); this.load(); }
    });
  }
}

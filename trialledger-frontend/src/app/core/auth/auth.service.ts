import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import {
  LoginDTO, LoginResponseDTO,
  ForgotPasswordDTO, ForgotPasswordRequestOtpDTO, ForgotUsernameDTO
} from '../models/auth.models';
import { Role } from '../models/user.models';
import { isAllowed, PermissionKey } from './permissions';

const TOKEN_KEY = 'tl_token';
const USER_KEY = 'tl_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly api = `${environment.apiBase}/api/auth`;
  private http = inject(HttpClient);
  private router = inject(Router);

  private _user = signal<LoginResponseDTO | null>(this.readUser());

  readonly user = computed(() => this._user());
  readonly isLoggedIn = computed(() => !!this._user());
  readonly role = computed<Role | null>(() => this._user()?.role ?? null);

  login(dto: LoginDTO): Observable<LoginResponseDTO> {
    return this.http.post<LoginResponseDTO>(`${this.api}/login`, dto).pipe(
      tap(res => this.persist(res))
    );
  }

  // Self-service register was removed. Accounts are created either by an
  // admin (via UserService.registerByAdmin) or implicitly when a coordinator
  // enrolls a participant. The corresponding endpoint here is gone too.

  /** Step 1 — ask the server to generate an OTP and print it to its console. */
  requestPasswordResetOtp(dto: ForgotPasswordRequestOtpDTO): Observable<string> {
    return this.http.post(`${this.api}/forgot-password/request-otp`, dto, { responseType: 'text' });
  }

  /** Step 2 — submit OTP + new password to finish the reset. */
  forgotPassword(dto: ForgotPasswordDTO): Observable<string> {
    return this.http.post(`${this.api}/forgot-password`, dto, { responseType: 'text' });
  }

  forgotUsername(dto: ForgotUsernameDTO): Observable<string> {
    return this.http.post(`${this.api}/forgot-username`, dto, { responseType: 'text' });
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this._user.set(null);
    this.router.navigate(['/login']);
  }

  token(): string | null { return localStorage.getItem(TOKEN_KEY); }

  /** Backward-compat for components that already used hasRole(...) */
  hasRole(...roles: Role[]): boolean {
    const r = this.role();
    return !!r && roles.includes(r);
  }

  /** Preferred permission check — single source of truth */
  can(key: PermissionKey): boolean {
    return isAllowed(this.role(), key);
  }

  private persist(res: LoginResponseDTO) {
    localStorage.setItem(TOKEN_KEY, res.accessToken);
    localStorage.setItem(USER_KEY, JSON.stringify(res));
    this._user.set(res);
  }

  private readUser(): LoginResponseDTO | null {
    const raw = localStorage.getItem(USER_KEY);
    if (!raw) return null;
    try { return JSON.parse(raw); } catch { return null; }
  }
}

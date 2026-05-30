import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, map, of, switchMap, tap } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import {
  LoginDTO, LoginResponseDTO,
  ForgotPasswordDTO, ForgotPasswordRequestOtpDTO, ForgotUsernameDTO
} from '../models/auth.models';
import { Role, UserDTO } from '../models/user.models';
import { ParticipantResponseDTO } from '../models/participant.models';
import { isAllowed, PermissionKey } from './permissions';

const TOKEN_KEY = 'tl_token';
const USER_KEY = 'tl_user';            // LoginResponseDTO returned by /api/auth/login
const USER_FULL_KEY = 'tl_user_full';  // UserDTO from /api/users/{id}  (has phone, email)
const PARTICIPANT_KEY = 'tl_participant'; // ParticipantResponseDTO (only for PARTICIPANT role)

/**
 * Auth + identity cache.
 *
 * On a successful login we make at most two follow-up calls and store the
 * results in localStorage so the rest of the app doesn't have to re-fetch:
 *
 *   1. GET /api/users/{userId}              → full user (every role)
 *   2. GET /api/participants/by-phone/{...} → only when role === PARTICIPANT
 *
 * Subsequent component reads come straight from the `fullUser()` /
 * `participant()` signals — no extra network traffic.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly api = `${environment.apiBase}/api/auth`;
  private readonly usersApi = `${environment.apiBase}/api/users`;
  private readonly partApi = `${environment.apiBase}/api/participants`;

  private http = inject(HttpClient);
  private router = inject(Router);

  private _user        = signal<LoginResponseDTO | null>(this.read<LoginResponseDTO>(USER_KEY));
  private _fullUser    = signal<UserDTO | null>(this.read<UserDTO>(USER_FULL_KEY));
  private _participant = signal<ParticipantResponseDTO | null>(this.read<ParticipantResponseDTO>(PARTICIPANT_KEY));

  readonly user        = computed(() => this._user());
  readonly fullUser    = computed(() => this._fullUser());
  readonly participant = computed(() => this._participant());
  readonly isLoggedIn  = computed(() => !!this._user());
  readonly role        = computed<Role | null>(() => this._user()?.role ?? null);

  login(dto: LoginDTO): Observable<LoginResponseDTO> {
    return this.http.post<LoginResponseDTO>(`${this.api}/login`, dto).pipe(
      tap(res => this.persistLogin(res)),
      // Chain the identity bootstrap so login() only resolves once the cache is warm.
      switchMap(res => this.bootstrapIdentity(res).pipe(map(() => res)))
    );
  }

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
    [TOKEN_KEY, USER_KEY, USER_FULL_KEY, PARTICIPANT_KEY].forEach(k => localStorage.removeItem(k));
    this._user.set(null);
    this._fullUser.set(null);
    this._participant.set(null);
    this.router.navigate(['/login']);
  }

  token(): string | null { return localStorage.getItem(TOKEN_KEY); }

  hasRole(...roles: Role[]): boolean {
    const r = this.role();
    return !!r && roles.includes(r);
  }

  can(key: PermissionKey): boolean {
    return isAllowed(this.role(), key);
  }

  // ────────────────────────────────────────────────────────────────
  // Identity bootstrap
  // ────────────────────────────────────────────────────────────────

  /**
   * Fetches the full user, and — if the logged-in role is PARTICIPANT —
   * also fetches their participant row by phone. Both go into localStorage.
   * Failures are swallowed so a network blip doesn't block login.
   */
  private bootstrapIdentity(res: LoginResponseDTO): Observable<unknown> {
    return this.http.get<UserDTO>(`${this.usersApi}/${res.userId}`).pipe(
      tap(full => this.persistFullUser(full)),
      switchMap(full => {
        if (res.role !== 'PARTICIPANT' || !full?.phone) return of(null);
        return this.http
          .get<ParticipantResponseDTO>(`${this.partApi}/by-phone/${encodeURIComponent(full.phone)}`)
          .pipe(
            tap(p => this.persistParticipant(p)),
            catchError(() => of(null))
          );
      }),
      catchError(() => of(null))
    );
  }

  // ────────────────────────────────────────────────────────────────
  // localStorage helpers
  // ────────────────────────────────────────────────────────────────

  private persistLogin(res: LoginResponseDTO) {
    localStorage.setItem(TOKEN_KEY, res.accessToken);
    localStorage.setItem(USER_KEY, JSON.stringify(res));
    this._user.set(res);
    // A fresh login wipes any stale cache from a previous session.
    localStorage.removeItem(USER_FULL_KEY);
    localStorage.removeItem(PARTICIPANT_KEY);
    this._fullUser.set(null);
    this._participant.set(null);
  }

   persistFullUser(u: UserDTO) {
    localStorage.setItem(USER_FULL_KEY, JSON.stringify(u));
    this._fullUser.set(u);
  }

   persistParticipant(p: ParticipantResponseDTO) {
    localStorage.setItem(PARTICIPANT_KEY, JSON.stringify(p));
    this._participant.set(p);
  }

  private read<T>(key: string): T | null {
    const raw = localStorage.getItem(key);
    if (!raw) return null;
    try { return JSON.parse(raw) as T; } catch { return null; }
  }
}

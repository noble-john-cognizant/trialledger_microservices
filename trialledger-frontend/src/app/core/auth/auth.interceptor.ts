import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from './auth.service';
import { ToastService } from '../services/toast.service';
import { extractErrorMessage } from '../utils/error-message';

/**
 * Adds the Bearer JWT header and globally surfaces session/connectivity
 * errors. 4xx errors are NOT toasted here — calling components show their
 * own contextual message via `extractErrorMessage(err)` so the user sees the
 * exact reason ("Email already exists", "Phone already enrolled", ...) rather
 * than a generic "Bad request".
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const toast = inject(ToastService);
  const token = auth.token();
  const cloned = token
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(cloned).pipe(
    catchError((err: HttpErrorResponse) => {
      if (err.status === 401) {
        toast.error(extractErrorMessage(err, 'Session expired — please sign in again.'));
        auth.logout();
      } else if (err.status === 0) {
        toast.error('Cannot reach the API gateway. Check your connection.');
      }
      // Let the component toast contextual errors for 4xx/5xx itself.
      return throwError(() => err);
    })
  );
};

import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from './auth.service';
import { ToastService } from '../services/toast.service';
import { extractErrorMessage } from '../utils/error-message';

/**
 * Attaches the JWT to every outgoing request and shows a single friendly
 * toast for any API failure. Components never need to handle error
 * messaging themselves — they just call toast.success() on a successful
 * mutation.
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
      toast.error(extractErrorMessage(err));
      if (err.status === 401) auth.logout();
      return throwError(() => err);
    })
  );
};

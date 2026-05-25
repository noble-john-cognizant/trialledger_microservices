import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from './auth.service';
import { ToastService } from '../services/toast.service';

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
        toast.error('Session expired — please log in again.');
        auth.logout();
      } else if (err.status === 403) {
        toast.error("You don't have permission for this action.");
      } else if (err.status === 0) {
        toast.error('Cannot reach the API gateway (localhost:9090).');
      } else if (err.status >= 500) {
        toast.error(`Server error (${err.status}). Try again later.`);
      }
      return throwError(() => err);
    })
  );
};

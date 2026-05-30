import { HttpErrorResponse } from '@angular/common/http';


export function extractErrorMessage(err: unknown, fallback = 'Something went wrong.'): string {
  if (err instanceof HttpErrorResponse) {
    const body = err.error;
    if (typeof body === 'string' && body) return body;
    if (body?.message) return body.message;
    if (body?.error)   return body.error;

    if (err.status === 0)   return 'Cannot reach the server.';
    if (err.status === 401) return 'Please sign in again.';
    if (err.status === 403) return "You don't have permission for that.";
    if (err.status === 404) return 'Not found.';
    if (err.status >= 500)  return 'Server error. Please try again.';
  }
  if (typeof err === 'string') return err;
  return fallback;
}

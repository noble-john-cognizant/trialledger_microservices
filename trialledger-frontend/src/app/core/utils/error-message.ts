import { HttpErrorResponse } from '@angular/common/http';

/**
 * Extract a clean, user-friendly message from any error our backend can
 * return. Tries the common shapes in order:
 *
 *   Spring/our handlers   →  { error: "..." }      or { message: "..." }
 *   Validation maps       →  { field: "msg", ... } or { errors: [{ defaultMessage }] }
 *   Plain text body       →  "string"
 *   ApiMessage envelope   →  { message, timestamp }
 *
 * Falls back to a status-code-specific generic message when nothing
 * sensible is in the body.
 */
export function extractErrorMessage(
  err: unknown,
  fallback = 'Something went wrong. Please try again.'
): string {
  if (!err) return fallback;

  // Plain string thrown
  if (typeof err === 'string') return err;

  if (err instanceof HttpErrorResponse) {
    // 1) Try body
    const body: any = err.error;
    const fromBody = pickMessage(body);
    if (fromBody) return fromBody;

    // 2) Try statusText / fallback by status
    return statusFallback(err.status, err.statusText) ?? fallback;
  }

  // Plain object thrown (e.g. server returned text body that we already parsed)
  return pickMessage(err) ?? fallback;
}

function pickMessage(body: any): string | null {
  if (body == null) return null;
  if (typeof body === 'string') {
    // Some servers return text "{...}" — try to parse once
    const s = body.trim();
    if (s.startsWith('{') || s.startsWith('[')) {
      try { return pickMessage(JSON.parse(s)); } catch { /* ignore */ }
    }
    return s || null;
  }
  if (typeof body !== 'object') return null;

  // Common single-field shapes from this project's backends.
  // Prefer `message` over `error` — our handlers put the descriptive text in
  // `message` (e.g. "Study not found with id=60 | Participant not found with id=10")
  // while `error` only contains the generic HTTP reason phrase ("Bad Request").
  // If `message` is absent or empty, fall back to `error`.
  if (typeof body.message === 'string' && body.message)   return body.message;
  if (typeof body.error === 'string' && body.error)       return body.error;
  if (typeof body.detail === 'string' && body.detail)     return body.detail;

  // Spring's validation errors[] list
  if (Array.isArray(body.errors) && body.errors.length) {
    const first = body.errors[0];
    if (typeof first === 'string') return first;
    if (typeof first?.defaultMessage === 'string') return first.defaultMessage;
    if (typeof first?.message === 'string') return first.message;
  }

  // Object whose values are validation messages: { name: 'required', email: 'invalid' }
  const values = Object.values(body).filter(v => typeof v === 'string') as string[];
  if (values.length === 1) return values[0];

  return null;
}

function statusFallback(status: number, statusText?: string): string | null {
  switch (status) {
    case 0:   return 'Cannot reach the server. Check your connection and try again.';
    case 400: return 'Request was rejected. Please check the values and try again.';
    case 401: return 'You need to sign in to do that.';
    case 403: return "You don't have permission for this action.";
    case 404: return 'The requested item could not be found.';
    case 409: return 'That value conflicts with an existing record.';
    case 422: return 'Some fields are invalid. Please review them.';
    case 502: return 'A downstream service is unavailable. Try again shortly.';
    case 503: return 'The service is temporarily unavailable. Try again shortly.';
    case 504: return 'The request timed out. Try again shortly.';
  }
  if (status >= 500 && status < 600) {
    return 'The server hit an error processing that request. Please try again.';
  }
  return statusText || null;
}

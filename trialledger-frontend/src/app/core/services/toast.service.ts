import { Injectable, signal } from '@angular/core';

export type ToastKind = 'info' | 'success' | 'error';
export interface Toast { id: number; message: string; kind: ToastKind; }

/**
 * Tiny notification queue. The auth interceptor calls `error()` for every
 * failed API request; components call `success()` after a mutation.
 */
@Injectable({ providedIn: 'root' })
export class ToastService {
  toasts = signal<Toast[]>([]);
  private nextId = 1;

  private show(message: string, kind: ToastKind) {
    const id = this.nextId++;
    this.toasts.update(list => [...list, { id, message, kind }]);
    setTimeout(() => this.dismiss(id), 3500);
  }

  success(m: string) { this.show(m, 'success'); }
  error(m: string)   { this.show(m, 'error'); }
  info(m: string)    { this.show(m, 'info'); }

  dismiss(id: number) {
    this.toasts.update(list => list.filter(t => t.id !== id));
  }
}

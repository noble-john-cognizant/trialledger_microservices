import { Injectable, signal } from '@angular/core';

export type ToastKind = 'info' | 'success' | 'error' | 'warn';
export interface Toast { id: number; message: string; kind: ToastKind; }

@Injectable({ providedIn: 'root' })
export class ToastService {
  toasts = signal<Toast[]>([]);
  private nextId = 1;

  show(message: string, kind: ToastKind = 'info', ms = 3500) {
    const id = this.nextId++;
    this.toasts.update(list => [...list, { id, message, kind }]);
    setTimeout(() => this.dismiss(id), ms);
  }
  success(m: string) { this.show(m, 'success'); }
  error(m: string)   { this.show(m, 'error', 4500); }
  warn(m: string)    { this.show(m, 'warn'); }
  info(m: string)    { this.show(m, 'info'); }

  dismiss(id: number) {
    this.toasts.update(list => list.filter(t => t.id !== id));
  }
}

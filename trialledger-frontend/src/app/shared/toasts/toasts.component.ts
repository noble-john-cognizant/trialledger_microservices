import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService } from '../../core/services/toast.service';

@Component({
  selector: 'tl-toasts',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './toasts.component.html',
  styleUrls: ['./toasts.component.css']
})
export class ToastsComponent {
  toast = inject(ToastService);

  bgClass(kind: string): string {
    switch (kind) {
      case 'success': return 'text-bg-success';
      case 'error':   return 'text-bg-danger';
      case 'warn':    return 'text-bg-warning';
      default:        return 'text-bg-primary';
    }
  }
}

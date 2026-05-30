import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'tl-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './modal.component.html',
  styles: `.modal-overlay {
      position: fixed; inset: 0;
      background: rgba(15, 23, 42, 0.5);
      z-index: 1050;
    }
  `
})
export class ModalComponent {
  @Input() open = false;
  @Input() title = '';
  @Input() size: 'sm' | 'md' | 'lg' | 'xl' = 'md';
  @Input() showFooter = true;
  @Input() backdropDismiss = true;
  @Output() close = new EventEmitter<void>();

  onBackdrop() {
    if (this.backdropDismiss) this.close.emit();
  }

  get dialogClass(): string {
    if (this.size === 'sm') return 'modal-dialog modal-sm modal-dialog-centered modal-dialog-scrollable';
    if (this.size === 'lg') return 'modal-dialog modal-lg modal-dialog-centered modal-dialog-scrollable';
    if (this.size === 'xl') return 'modal-dialog modal-xl modal-dialog-centered modal-dialog-scrollable';
    return 'modal-dialog modal-dialog-centered modal-dialog-scrollable';
  }
}

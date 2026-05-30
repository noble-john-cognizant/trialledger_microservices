import { Component, Input } from '@angular/core';

/**
 * Inline loading indicator. Two sizes: small (default) and large block.
 * Use `<tl-spinner />` for inline, `<tl-spinner block />` for a centered
 * "loading…" panel.
 */
@Component({
  selector: 'tl-spinner',
  standalone: true,
  template: `
    @if (block !== false) {
      <div class="tl-loading-block">
        <span class="tl-spinner lg" role="status" aria-label="Loading"></span>
        @if (label) { <span class="small">{{ label }}</span> }
      </div>
    } @else {
      <span class="tl-spinner" role="status" aria-label="Loading"></span>
    }
  `
})
export class SpinnerComponent {
  /** Render as a centered block with a label. Pass `block` attribute to enable. */
  @Input() block: boolean | '' = false;
  @Input() label = 'Loading…';
}

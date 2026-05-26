import {
  Component, ElementRef, HostListener,
  computed, effect, inject, input, output, signal
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

export interface SearchOption {
  id: number;
  /** Primary display text */
  label: string;
  /** Optional one-line subtitle shown under the label */
  subtitle?: string;
}

/**
 * A typeahead picker that works in two modes:
 *
 *  1) `canList = true`  → shows a filtered dropdown of the supplied items.
 *  2) `canList = false` → renders only a numeric ID input (no dropdown).
 *
 *  IMPORTANT: This component uses signal-based inputs (`input()`) so the
 *  `selected()` computed re-evaluates whenever the parent changes `value`.
 *  Classic `@Input()` would not trigger the computed in zoneless Angular 21.
 */
@Component({
  selector: 'tl-search-select',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './search-select.component.html',
  styleUrls: ['./search-select.component.css']
})
export class SearchSelectComponent {
  // ===== Signal inputs =====
  items         = input<SearchOption[]>([]);
  value         = input<number | null>(null);
  canList       = input(true);
  placeholder   = input('Search by name or ID…');
  idPlaceholder = input('Enter ID');
  emptyText     = input('No matches');
  disabled      = input(false);

  // ===== Output =====
  valueChange = output<number | null>();

  private host = inject(ElementRef<HTMLElement>);

  search = signal('');
  open   = signal(false);

  /** Currently selected option (looked up against the items list). */
  selected = computed(() => {
    const v = this.value();
    return v == null ? null : (this.items().find(i => i.id === v) ?? null);
  });

  filtered = computed(() => {
    const q = this.search().toLowerCase().trim();
    const items = this.items();
    if (!q) return items.slice(0, 50);
    return items.filter(i =>
      i.label.toLowerCase().includes(q) ||
      (i.subtitle ?? '').toLowerCase().includes(q) ||
      String(i.id).includes(q)
    ).slice(0, 50);
  });

  constructor() {
    // When the parent clears the value, also clear the search box so the
    // input is empty again instead of still showing the old query.
    effect(() => {
      if (this.value() === null) this.search.set('');
    });
  }

  /** Selecting an option from the dropdown */
  pick(o: SearchOption) {
    this.valueChange.emit(o.id);
    this.search.set('');
    this.open.set(false);
  }

  clear() {
    this.valueChange.emit(null);
    this.search.set('');
  }

  /** ID-mode (canList=false) — accept raw numeric input */
  onIdInput(raw: string) {
    const n = Number(raw);
    this.valueChange.emit(Number.isFinite(n) && n > 0 ? n : null);
  }

  onFocus() { if (!this.disabled()) this.open.set(true); }

  @HostListener('document:click', ['$event'])
  onDocClick(e: MouseEvent) {
    if (!this.host.nativeElement.contains(e.target as Node)) this.open.set(false);
  }
}

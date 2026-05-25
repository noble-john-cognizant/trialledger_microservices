import { Injectable, computed, effect, signal } from '@angular/core';

export type ThemeMode = 'light' | 'dark' | 'system';

const THEME_KEY = 'tl_theme';

/**
 * Drives the light/dark/system theme for the whole app.
 *
 * Strategy:
 *   - User picks one of {light, dark, system}
 *   - We persist the choice in localStorage
 *   - The data-theme attribute on the <html> element is the source of truth
 *     for component CSS — every component just reads CSS variables that
 *     resolve differently per theme.
 *   - When in 'system' mode we mirror the OS preference and react to changes
 *     of (prefers-color-scheme).
 */
@Injectable({ providedIn: 'root' })
export class ThemeService {
  /** What the user picked. */
  mode = signal<ThemeMode>(this.readPreference());

  /** Tracks the OS preference when mode === 'system'. */
  private systemDark = signal<boolean>(this.readSystemPreference());

  /** The effective theme actually applied to the page. */
  effective = computed<'light' | 'dark'>(() => {
    const m = this.mode();
    if (m === 'system') return this.systemDark() ? 'dark' : 'light';
    return m;
  });

  /** True when the user currently sees dark mode (useful for toggle UI). */
  isDark = computed(() => this.effective() === 'dark');

  constructor() {
    // Listen to OS theme changes so 'system' mode follows along live.
    if (typeof window !== 'undefined' && window.matchMedia) {
      const mql = window.matchMedia('(prefers-color-scheme: dark)');
      const onChange = (e: MediaQueryListEvent) => this.systemDark.set(e.matches);
      // Modern browsers
      mql.addEventListener?.('change', onChange);
    }

    // Apply theme to <html> whenever the effective signal changes.
    effect(() => {
      const theme = this.effective();
      const root = document.documentElement;
      root.setAttribute('data-theme', theme);
      // Helps Bootstrap's prebuilt color-mode-aware components.
      root.style.colorScheme = theme;
    });

    // Persist user's choice (not effective — keep 'system' as 'system').
    effect(() => {
      try { localStorage.setItem(THEME_KEY, this.mode()); } catch { /* ignore */ }
    });
  }

  set(mode: ThemeMode) { this.mode.set(mode); }

  /** Cycle through light → dark → system → light. */
  cycle() {
    const next: ThemeMode =
      this.mode() === 'light' ? 'dark'
      : this.mode() === 'dark' ? 'system'
      : 'light';
    this.set(next);
  }

  /** Simple binary toggle: light <-> dark (ignores 'system'). */
  toggle() {
    this.set(this.isDark() ? 'light' : 'dark');
  }

  private readPreference(): ThemeMode {
    try {
      const raw = localStorage.getItem(THEME_KEY);
      if (raw === 'light' || raw === 'dark' || raw === 'system') return raw;
    } catch { /* ignore */ }
    return 'system';
  }

  private readSystemPreference(): boolean {
    if (typeof window === 'undefined' || !window.matchMedia) return false;
    return window.matchMedia('(prefers-color-scheme: dark)').matches;
  }
}

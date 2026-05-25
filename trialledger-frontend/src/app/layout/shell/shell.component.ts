import { Component, OnDestroy, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { ToastsComponent } from '../../shared/toasts/toasts.component';
import { UserProfileComponent } from '../../features/user-profile/user-profile.component';
import { GlobalSearchComponent } from '../global-search/global-search.component';
import { NotificationStateService } from '../../core/services/notification-state.service';
import { ThemeService } from '../../core/services/theme.service';
import { PermissionKey } from '../../core/auth/permissions';

interface NavItem {
  label: string;
  route: string;
  icon: string;
  /** Permission key required to even see this menu entry. */
  permission?: PermissionKey;
}

@Component({
  selector: 'tl-shell',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive,
    ToastsComponent, UserProfileComponent, GlobalSearchComponent],
  templateUrl: './shell.component.html',
  styleUrls: ['./shell.component.css']
})
export class ShellComponent implements OnInit, OnDestroy {
  private auth = inject(AuthService);
  private notif = inject(NotificationStateService);
  /** Exposed so the template can bind/toggle directly. */
  theme = inject(ThemeService);

  collapsed = signal(false);
  profileOpen = signal(false);
  user = this.auth.user;

  /** Live badge count from the shared notification state. */
  unreadCount = this.notif.unreadCount;

  initials = computed(() => {
    const n = this.user()?.name ?? '';
    return n.split(' ').map(s => s[0]).slice(0, 2).join('').toUpperCase() || 'U';
  });

  navItems: NavItem[] = [
    { label: 'Dashboard',      route: '/dashboard',      icon: 'bi-grid-1x2-fill' },
    { label: 'Studies',        route: '/studies',        icon: 'bi-journal-bookmark-fill', permission: 'STUDY_LIST' },
    { label: 'Participants',   route: '/participants',   icon: 'bi-people-fill',           permission: 'PARTICIPANT_LIST' },
    { label: 'Consent',        route: '/consents',       icon: 'bi-pen-fill',              permission: 'CONSENT_VIEW' },
    { label: 'Visits',         route: '/visits',         icon: 'bi-calendar-event-fill',   permission: 'VISIT_VIEW' },
    { label: 'Samples',        route: '/samples',        icon: 'bi-droplet-fill',          permission: 'SAMPLE_VIEW' },
    { label: 'Adverse Events', route: '/adverse-events', icon: 'bi-exclamation-triangle-fill', permission: 'AE_VIEW' },
    { label: 'Provenance',     route: '/provenance',     icon: 'bi-link-45deg',            permission: 'PROVENANCE_VIEW' },
    { label: 'Reports',        route: '/reports',        icon: 'bi-bar-chart-line-fill',   permission: 'REPORT_VIEW' },
    { label: 'Notifications',  route: '/notifications',  icon: 'bi-bell-fill' },
    { label: 'Users',          route: '/users',          icon: 'bi-person-badge-fill',     permission: 'USER_LIST' },
    { label: 'Audit Log',      route: '/audit-log',      icon: 'bi-clipboard-data-fill',   permission: 'AUDIT_VIEW' },
    { label: 'Alert Rules',    route: '/alert-rules',    icon: 'bi-bell-slash-fill',       permission: 'ALERT_VIEW' }
  ];

  visibleNav = computed(() =>
    this.navItems.filter(n => !n.permission || this.auth.can(n.permission))
  );

  ngOnInit() {
    // Begin polling once the shell mounts (user is authenticated by guard).
    this.notif.start();
  }

  ngOnDestroy() {
    this.notif.stop();
  }
}

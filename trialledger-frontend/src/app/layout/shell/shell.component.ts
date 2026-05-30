import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { ToastsComponent } from '../../shared/toasts/toasts.component';
import { UserProfileComponent } from '../../features/user-profile/user-profile.component';
import { NotificationService } from '../../core/services/notification.service';
import { PermissionKey } from '../../core/auth/permissions';
import { Role } from '../../core/models/user.models';

interface NavItem {
  label: string;
  route: string;
  icon: string;
  /** Permission key required to even see this menu entry. */
  permission?: PermissionKey;
  /** If set, the entry only shows for these roles (overrides `permission`). */
  onlyFor?: Role[];
  /** If set, the entry is hidden for these roles. */
  hideFor?: Role[];
}

@Component({
  selector: 'tl-shell',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive,
    ToastsComponent, UserProfileComponent],
  templateUrl: './shell.component.html',
  styleUrls: ['./shell.component.css']
})
export class ShellComponent implements OnInit {
  private auth = inject(AuthService);
  private notif = inject(NotificationService);

  collapsed = signal(false);
  profileOpen = signal(false);
  user = this.auth.user;
  role = this.auth.role;

  /** Live badge count from the shared notification state. */
  unreadCount = this.notif.unreadCount;

  /** Participants don't see the notifications bell or the menu entry. */
  isParticipant = computed(() => this.role() === 'PARTICIPANT');

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

    // Participant-scoped pages — only the logged-in participant sees these.
    { label: 'My study',       route: '/my-study',       icon: 'bi-journal-bookmark-fill', onlyFor: ['PARTICIPANT'] },
    { label: 'My samples',     route: '/my-samples',     icon: 'bi-droplet-fill',          onlyFor: ['PARTICIPANT'] },
    { label: 'My adverse events', route: '/my-adverse-events', icon: 'bi-exclamation-triangle-fill', onlyFor: ['PARTICIPANT'] },

    { label: 'Notifications',  route: '/notifications',  icon: 'bi-bell-fill', hideFor: ['PARTICIPANT'] },
    { label: 'Users',          route: '/users',          icon: 'bi-person-badge-fill',     permission: 'USER_LIST' },
    { label: 'Audit Log',      route: '/audit-log',      icon: 'bi-clipboard-data-fill',   permission: 'AUDIT_VIEW' }
  ];

  visibleNav = computed(() => {
    const r = this.role();
    return this.navItems.filter(n => {
      if (n.onlyFor && (!r || !n.onlyFor.includes(r))) return false;
      if (n.hideFor && r && n.hideFor.includes(r)) return false;
      if (n.permission && !this.auth.can(n.permission)) return false;
      return true;
    });
  });

  ngOnInit() {
    // Skip notification fetch for the participant — they don't see the bell.
    if (!this.isParticipant()) this.notif.refresh();
  }
}

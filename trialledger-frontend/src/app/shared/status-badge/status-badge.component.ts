import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

const COLOR_MAP: Record<string, string> = {
  // study
  PLANNED: 'bg-soft-secondary', ACTIVE: 'bg-soft-success', COMPLETED: 'bg-soft-primary',
  SUSPENDED: 'bg-soft-warning', TERMINATED: 'bg-soft-danger',
  // protocol
  DRAFT: 'bg-soft-secondary', UNDER_REVIEW: 'bg-soft-warning', APPROVED: 'bg-soft-success',
  SUPERSEDED: 'bg-soft-secondary', ARCHIVED: 'bg-soft-secondary',
  // user
  INACTIVE: 'bg-soft-secondary',
  // consent
  WITHDRAWN: 'bg-soft-danger', EXPIRED: 'bg-soft-warning',
  // enrollment
  ENROLLED: 'bg-soft-success', PENDING: 'bg-soft-warning',
  // visit
  SCHEDULED: 'bg-soft-primary', MISSED: 'bg-soft-danger', CANCELLED: 'bg-soft-secondary',
  // sample
  COLLECTED: 'bg-soft-primary', IN_ANALYSIS: 'bg-soft-warning',
  // AE status
  OPEN: 'bg-soft-warning', RESOLVED: 'bg-soft-success', REPORTED: 'bg-soft-primary',
  // notification status
  UNREAD: 'bg-soft-primary', READ: 'bg-soft-secondary',
  // severity
  LOW: 'bg-soft-secondary', MEDIUM: 'bg-soft-warning', HIGH: 'bg-soft-danger', CRITICAL: 'bg-soft-danger',
  MILD: 'bg-soft-success', MODERATE: 'bg-soft-warning', SEVERE: 'bg-soft-danger'
};

@Component({
  selector: 'tl-status',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './status-badge.component.html',
  styleUrls: ['./status-badge.component.css']
})
export class StatusBadgeComponent {
  @Input() value: string | null | undefined = '';

  get color(): string {
    return COLOR_MAP[(this.value || '').toUpperCase()] || 'bg-soft-primary';
  }
  get label(): string {
    return (this.value || '—').replace(/_/g, ' ');
  }
}

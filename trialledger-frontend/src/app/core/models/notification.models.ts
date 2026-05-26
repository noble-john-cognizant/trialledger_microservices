export type NotificationCategory = 'CONSENT' | 'SAMPLE' | 'VISIT' | 'AE';
export const ALL_NOTIFICATION_CATEGORIES: NotificationCategory[] = ['CONSENT', 'SAMPLE', 'VISIT', 'AE'];

export type NotificationStatus = 'UNREAD' | 'READ';

export type AlertSeverity = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
export const ALL_ALERT_SEVERITIES: AlertSeverity[] = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'];

export interface NotificationRequestDTO {
  userId: number;
  entityId: number;
  message: string;
  category: NotificationCategory;
}

export interface NotificationResponseDTO {
  notificationId: number;
  userId: number;
  entityId: number;
  message: string;
  category: NotificationCategory;
  status: NotificationStatus;
  createdAt: string;
}

export interface AlertRuleRequestDTO {
  name: string;
  triggerExpression: string;
  severity: AlertSeverity;
  recipientsJson: string;
  active: boolean;
}

export interface AlertRuleResponseDTO {
  ruleId: number;
  name: string;
  triggerExpression: string;
  severity: AlertSeverity;
  recipientsJson: string;
  active: boolean;
}

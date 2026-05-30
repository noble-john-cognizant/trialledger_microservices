export type NotificationCategory = 'CONSENT' | 'SAMPLE' | 'VISIT' | 'AE';
export const ALL_NOTIFICATION_CATEGORIES: NotificationCategory[] = ['CONSENT', 'SAMPLE', 'VISIT', 'AE'];

export type NotificationStatus = 'UNREAD' | 'READ';

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

export type Role =
  | 'PARTICIPANT' | 'COORDINATOR' | 'TECHNICIAN' | 'PI'
  | 'COMPLIANCE' | 'DATA_MANAGER' | 'AUDITOR' | 'ADMIN';

export const ALL_ROLES: Role[] = [
  'PARTICIPANT', 'COORDINATOR', 'TECHNICIAN', 'PI',
  'COMPLIANCE', 'DATA_MANAGER', 'AUDITOR', 'ADMIN'
];

export type UserStatus = 'ACTIVE' | 'INACTIVE';

export interface UserDTO {
  userId: number;
  name: string;
  role: Role;
  email: string;
  phone: string;
  status: UserStatus;
  createdAt: string;
}

export interface UpdateUserDTO {
  name: string;
  email: string;
  phone: string;
}

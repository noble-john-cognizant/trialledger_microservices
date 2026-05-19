import { Role, UserStatus } from './user.models';

export interface RegisterDTO {
  email: string;
  password: string;
  phone: string;
  name: string;
}

export interface LoginDTO {
  email: string;
  password: string;
}

export interface LoginResponseDTO {
  name: string;
  accessToken: string;
  role: Role;
  userId: number;
  status: UserStatus;
  createdAt: string;
}

export interface ForgotPasswordDTO {
  email: string;
  newPassword: string;
}

export interface ForgotUsernameDTO {
  phoneNumber: string;
  password: string;
}

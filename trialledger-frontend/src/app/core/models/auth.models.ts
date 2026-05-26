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

/** Step 1 — request an OTP for the given email. */
export interface ForgotPasswordRequestOtpDTO {
  email: string;
}

/** Step 2 — submit the OTP plus the new password. */
export interface ForgotPasswordDTO {
  email: string;
  otp: string;
  newPassword: string;
}

export interface ForgotUsernameDTO {
  phoneNumber: string;
  password: string;
}

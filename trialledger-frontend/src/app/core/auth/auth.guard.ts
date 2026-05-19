import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';
import { ToastService } from '../services/toast.service';
import { PermissionKey } from './permissions';

export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  if (auth.isLoggedIn()) return true;
  router.navigate(['/login']);
  return false;
};

/**
 * Block a route unless the user has the given permission key.
 * Used in routes config: `canActivate: [permissionGuard('STUDY_LIST')]`
 */
export const permissionGuard = (key: PermissionKey): CanActivateFn => () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const toast = inject(ToastService);
  if (!auth.isLoggedIn()) { router.navigate(['/login']); return false; }
  if (auth.can(key)) return true;
  toast.error("You don't have access to that area.");
  router.navigate(['/dashboard']);
  return false;
};

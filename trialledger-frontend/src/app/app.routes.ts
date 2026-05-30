import { Routes } from '@angular/router';
import { authGuard, permissionGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'dashboard' },

  // ---- Public auth routes ----
  
  { path: 'login',           loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent) },
  { path: 'forgot-password', loadComponent: () => import('./features/auth/forgot-password/forgot-password.component').then(m => m.ForgotPasswordComponent) },
  { path: 'forgot-username', loadComponent: () => import('./features/auth/forgot-username/forgot-username.component').then(m => m.ForgotUsernameComponent) },

  // ---- Protected shell ----
  {
    path: '',
    loadComponent: () => import('./layout/shell/shell.component').then(m => m.ShellComponent),
    canActivate: [authGuard],
    children: [
      { path: 'dashboard',
        loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent) },

      { path: 'studies',
        canActivate: [permissionGuard('STUDY_LIST')],
        loadComponent: () => import('./features/studies/studies-list/studies-list.component').then(m => m.StudiesListComponent) },
      { path: 'studies/:id',
        canActivate: [permissionGuard('STUDY_VIEW')],
        loadComponent: () => import('./features/studies/study-detail/study-detail.component').then(m => m.StudyDetailComponent) },

      { path: 'participants',
        canActivate: [permissionGuard('PARTICIPANT_LIST')],
        loadComponent: () => import('./features/participants/participants.component').then(m => m.ParticipantsComponent) },
      { path: 'participants/:id',
        canActivate: [permissionGuard('PARTICIPANT_VIEW')],
        loadComponent: () => import('./features/participants/participant-dashboard/participant-dashboard.component').then(m => m.ParticipantDashboardComponent) },

      { path: 'consents',
        canActivate: [permissionGuard('CONSENT_VIEW')],
        loadComponent: () => import('./features/consents/consents.component').then(m => m.ConsentsComponent) },

      { path: 'visits',
        canActivate: [permissionGuard('VISIT_VIEW')],
        loadComponent: () => import('./features/visits/visits.component').then(m => m.VisitsComponent) },

      { path: 'samples',
        canActivate: [permissionGuard('SAMPLE_VIEW')],
        loadComponent: () => import('./features/samples/samples.component').then(m => m.SamplesComponent) },

      { path: 'adverse-events',
        canActivate: [permissionGuard('AE_VIEW')],
        loadComponent: () => import('./features/adverse-events/adverse-events.component').then(m => m.AdverseEventsComponent) },

      { path: 'provenance',
        canActivate: [permissionGuard('PROVENANCE_VIEW')],
        loadComponent: () => import('./features/provenance/provenance.component').then(m => m.ProvenanceComponent) },

      { path: 'reports',
        canActivate: [permissionGuard('REPORT_VIEW')],
        loadComponent: () => import('./features/reports/reports.component').then(m => m.ReportsComponent) },

      { path: 'notifications',
        loadComponent: () => import('./features/notifications/notifications-list/notifications-list.component').then(m => m.NotificationsListComponent) },

      // Participant-scoped views — auto-resolve the logged-in participant's own id.
      { path: 'my-study',
        loadComponent: () => import('./features/participants/my/my-study.component').then(m => m.MyStudyComponent) },
      { path: 'my-samples',
        loadComponent: () => import('./features/participants/my/my-samples.component').then(m => m.MySamplesComponent) },
      { path: 'my-adverse-events',
        loadComponent: () => import('./features/participants/my/my-adverse-events.component').then(m => m.MyAdverseEventsComponent) },

      { path: 'users',
        canActivate: [permissionGuard('USER_LIST')],
        loadComponent: () => import('./features/users/users-list/users-list.component').then(m => m.UsersListComponent) },

      { path: 'audit-log',
        canActivate: [permissionGuard('AUDIT_VIEW')],
        loadComponent: () => import('./features/users/audit-log/audit-log.component').then(m => m.AuditLogComponent) },
    ]
  },

  { path: '**', redirectTo: 'dashboard' }
];

# TrialLedger Frontend

Angular 21 single-page application for the **TrialLedger** clinical trials platform.
It is the user-facing layer for the Spring Boot microservices backend living in the parent folder.

## Stack

- Angular 21 (standalone components, signals, the new `@if/@for` control-flow syntax)
- Reactive forms + RxJS
- SCSS with a small design-token system (no UI framework)
- HttpClient with a Bearer-JWT interceptor
- Lazy-loaded routes + role-based guards

## Architecture

```
src/app
├── app.config.ts        # provideHttpClient + interceptors + router
├── app.routes.ts        # public auth routes + protected shell with children
├── core/
│   ├── models.ts        # TypeScript types for every backend DTO
│   ├── api.services.ts  # one Injectable per microservice
│   ├── auth.service.ts  # login/logout/token state (signals)
│   ├── auth.interceptor # adds Bearer header + handles 401/403/0/5xx
│   ├── auth.guard.ts    # authGuard + roleGuard
│   └── toast.service.ts
├── shared/              # reusable widgets (modal, badges, toasts, empty state)
├── layout/              # main shell (sidebar + topbar + outlet)
└── features/            # one folder per backend module
    ├── auth/            # login / register / forgot-password / forgot-username
    ├── dashboard/
    ├── users/           # users list + audit log
    ├── studies/         # studies list + detail (with protocol versions)
    ├── participants/
    ├── consents/
    ├── visits/          # visits + source data
    ├── samples/         # samples + chain of custody + storage + assays
    ├── adverse-events/  # AE + follow-ups
    ├── provenance/      # records + dataset snapshots + audit packages
    ├── reports/         # reports + KPIs
    └── notifications/   # inbox + alert rules
```

## Backend dependency

The app expects the API gateway from the parent project to be running on **http://localhost:9090**.
Start the backend in this order:

1. `service-registry` (Eureka)
2. `api-gateway` (port 9090)
3. The 8 domain services (`study-service`, `consent-service`, `visit-service`,
   `sample-service`, `adverse-event-service`, `provenance-service`,
   `report-service`, `notification-service`)

If the gateway lives elsewhere, edit `src/environments/environment.ts`:

```ts
export const environment = {
  production: false,
  apiBase: 'http://your-gateway:9090'
};
```

## Run

```powershell
cd trialledger-frontend
npm install        # only once
npm start          # http://localhost:4200
```

For a production build:

```powershell
npm run build
# dist/trialledger-frontend
```

## How to sign in for the first time

1. The very first user must be promoted to **ADMIN** in the database (the `/api/auth/register`
   endpoint creates only `PARTICIPANT` accounts by design).
2. Once you have an ADMIN account, log in and use the **Users → New user** screen to
   provision Coordinators, PIs, Lab Technicians, Compliance Officers, etc.
3. Public sign-up at `/register` is only intended for participants.

## Routes

| Path | Roles | Notes |
|------|-------|-------|
| `/login`, `/register`, `/forgot-*` | public | |
| `/dashboard` | any logged-in user | KPIs, recent studies, AEs |
| `/studies` `/studies/:id` | most roles | List + protocol versions |
| `/participants` | most roles | Enrollment roster |
| `/consents` | coordinators, PIs, participants… | Record + withdraw |
| `/visits` | clinical roles + participants | Visits + source data |
| `/samples` | lab + admin | Full sample lifecycle |
| `/adverse-events` | clinical + compliance | AEs + follow-ups |
| `/provenance` | compliance + audit | Records / snapshots / packages |
| `/reports` | reporting roles | Reports + KPIs |
| `/notifications` | any | Inbox |
| `/users` | ADMIN, PI | Manage users |
| `/audit-log` | ADMIN, COMPLIANCE, AUDITOR | Immutable activity |
| `/alert-rules` | ADMIN, COMPLIANCE | Trigger expressions |

## Notes on contract quirks

- `visit-service` wraps everything in `ApiResponseDto<T>`; the API service layer
  in `core/api.services.ts` unwraps `.data` automatically.
- `adverse-event-service` returns `ApiMessage` for POSTs.
- `provenance-service` `GET /api/provenance` returns a Spring `Page<T>` —
  the UI paginates accordingly.
- `audit-packages/download/{id}` returns a binary ZIP — handled with `responseType: 'blob'`
  and an `<a download>` click.

## License

Internal demonstration project.

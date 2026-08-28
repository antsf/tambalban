# Tasks: D1 Bearer API Migration

## Phase 1: Config
- [ ] T001 [P1] Rename `SupabaseConfig` → `TambalBanApiConfig`, new `BASE_URL`, drop `ANON_KEY` (`core/utils/TambalBanApiConfig.kt`, was `SupabaseConfig.kt`)

## Phase 2: Network
- [ ] T002 [P1] Rename `SupabaseService` → `TambalBanApiService`, rewrite every endpoint to `/api/v2/*` shapes (`core/network/TambalBanApiService.kt`, was `SupabaseService.kt`)
- [ ] T003 [P1] Rewrite `AuthInterceptor`: bearer-only, no apikey header, no anon fallback (`core/network/AuthInterceptor.kt`)
- [ ] T004 [P1] Update `NetworkModule`/`ApiClient` for the new service type + base URL (`core/network/NetworkModule.kt`, `ApiClient.kt`)

## Phase 3: Models
- [ ] T005 [P1] Rewrite `AuthResponse`/drop `refresh_token` (`auth/data/AuthModels.kt`)
- [ ] T006 [P1] Update `Profile` to match v2's user shape (`username` field added, `id`/`email`/`full_name`/`phone`/`avatar_url`) (`auth/data/Profile.kt`)
- [ ] T007 [P2] Trim `WorkshopSubmission` — drop `source`/`verified` (server-set, sending them is inert) (`workshop/data/WorkshopSubmission.kt`)
- [ ] T008 [P1] Remove now-dead `saveRefreshToken`/`getRefreshToken` from `AuthPrefs` (`core/utils/AuthPrefs.kt`)

## Phase 4: Repositories
- [ ] T009 [P1] `AuthRepository`: new response shape, `logout()` becomes suspend + calls `/api/v2/auth/logout` (`auth/data/AuthRepository.kt`)
- [ ] T010 [P1] `WorkshopRepository`: new query param shapes (bbox instead of PostgREST `gte.`/`lte.` filters), `addWorkshop` uses new upload+submit endpoints (`workshop/data/WorkshopRepository.kt`)
- [ ] T011 [P1] `ReviewRepository`: new endpoint paths (`workshop/data/ReviewRepository.kt`)
- [ ] T012 [P1] `ProfileRepository`: `GET`/`PATCH /api/v2/profile`, avatar upload via `/api/v2/upload/avatar` (`auth/data/ProfileRepository.kt`)

## Phase 5: Call-site fixups
- [ ] T013 [P1] Update every `logout()` call site for the new suspend signature (grep `\.logout()`)

## Phase 6: Tests
- [ ] T014 [P1] Update `AuthRepositoryTest`, `ProfileRepositoryTest`, `ReviewRepositoryTest`, `SubmissionRepositoryTest`, `WorkshopRepositoryTest` for new types/mocks
- [ ] T015 [P1] Build verification: `./gradlew compileDebugKotlin`
- [ ] T016 [P1] Test verification: `./gradlew testDebugUnitTest`
- [ ] T017 [P2] Manual on-device smoke test (device already connected): register → login → view map → add workshop with photo → view profile

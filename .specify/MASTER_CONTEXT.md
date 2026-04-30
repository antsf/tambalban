# TambalBan Master Context
# Constitution: v1.2.0 | TOKEN-OPTIMIZED
# Generated: 2026-04-30

## Constitution Anchor
- MVVM strict: UI → ViewModel → Repository → Network/DB (no skip)
- Stack: Kotlin 1.9+, Retrofit 2, OkHttp 4, osmdroid, Supabase, SQLite Native
- Packaging: Feature-based — auth/ workshop/ map/ core/
- Forbidden: Firebase, Google Maps SDK, hardcoded API keys, !! operator
- Security: EncryptedSharedPreferences, BuildConfig for all keys
- UI: Orchid #D672E1, Plus Jakarta Sans, 20dp icons, Bottom Sheets
- Performance: Viewport/radius bounding box queries, marker clustering, 10k+ markers
- Dev Order: API Contract → Model → Repository → ViewModel → UI
- Offline: Empty/Loading/Error states mandatory in every UI
- Testing: Repository logic, submission validation, auth flows required

---

## Features Index

| ID | Feature | Status | Key Files | Blocker |
|----|---------|--------|-----------|---------|
| 001 | Tambal Ban Finder (Map) | ✅ Done | MainActivity, MainViewModel, WorkshopRepository, WorkshopDbHelper | — |
| 002 | Supabase Backend Integration | ✅ Done | SupabaseService, NetworkModule, AuthInterceptor, Workshop models | — |
| 009 | Design System Guardian | ✅ Done | colors.xml, type.xml, TambalButton, TambalTextField, AvatarView | — |
| 010 | Refresh Login Screen | 🔄 In Progress | LoginActivity, LoginViewModel, activity_login.xml | T001–T019 pending |
| 011 | Refresh Home Screen | 🔄 In Progress | MainViewModel, MainAct, SearchSuggestionAdapter | T020–T024 pending |
| 012 | User Profile Screen | ✅ Done | ProfileActivity, EditProfileActivity, ProfileViewModel, ProfileRepository | — |
| 013 | Auth Register | ✅ Done | RegisterActivity, RegisterViewModel, AuthRepository | — |
| 014 | Modular Structure | ✅ Done | Feature-based packaging applied across all packages | — |
| 015 | Workshops List | ✅ Done | WorkshopListActivity, WorkshopListViewModel, WorkshopListAdapter | — |

---

## Pending Tasks

### 010 - Refresh Login Screen
- [ ] T001 Define purple theme colors in colors.xml
- [ ] T002 Create bg_login_gradient.xml drawable
- [ ] T003 Update M3 pill-shaped text field styles in styles.xml
- [ ] T004 Implement ConstraintLayout root + MaterialCardView in activity_login.xml
- [ ] T005 Create ic_brand_icon.xml (wrench icon)
- [ ] T006 Implement branding logo section in activity_login.xml
- [ ] T007 Add rounded Email/Password input fields in activity_login.xml
- [ ] T008 Implement pill-shaped Login button in activity_login.xml
- [ ] T009 Remove old social login buttons from activity_login.xml
- [ ] T010 Update LoginActivity.kt to bind new layout + inline error states
- [ ] T011 Update LoginViewModel.kt for loading/error state management
- [ ] T012 Add registration prompt text in activity_login.xml
- [ ] T013 Update LoginActivity.kt for register navigation click
- [ ] T014 Implement "Forgot?" link with accent styling in activity_login.xml
- [ ] T015 Update LoginActivity.kt for password recovery navigation
- [ ] T016 Implement footer copyright text in activity_login.xml
- [ ] T017 Add contentDescription to all icons (accessibility)
- [ ] T018 Confirm keyboard adjustResize in AndroidManifest.xml
- [ ] T019 Final validation against quickstart.md

### 011 - Refresh Home Screen
- [ ] T020 Add ilike search to SupabaseService.kt + WorkshopRepository.kt
- [ ] T021 Implement debounced search pipeline in MainViewModel.kt (Flow + debounce)
- [ ] T022 Add RecyclerView for search suggestions in view_search_overlay.xml
- [ ] T023 Create SearchSuggestionAdapter.kt + bind in MainActivity.kt
- [ ] T024 Implement snap-to-marker logic on suggestion click in MainActivity.kt

### 015 - Workshops List
- [ ] T025 Set click listener on `btnViewAll` in `MainActivity.kt` to open `WorkshopListActivity`

---

## API Contracts Quick Reference

| Method | Endpoint | Repository |
|--------|----------|------------|
| GET | /rest/v1/workshops?verified=eq.true&lat filters | WorkshopRepository |
| GET | /rest/v1/workshops?id=eq.{id} | WorkshopRepository |
| GET | /rest/v1/workshops?name=ilike.*{q}* | WorkshopRepository |
| GET | /rest/v1/reviews?workshop_id=eq.{id} | ReviewRepository |
| POST | /rest/v1/reviews | ReviewRepository |
| POST | /rest/v1/workshop_submissions | SubmissionRepository |
| GET | /rest/v1/workshop_submissions?user_id=eq.{id} | SubmissionRepository |
| GET | /rest/v1/users_profile?id=eq.{id} | ProfileRepository |
| PATCH | /rest/v1/users_profile?id=eq.{id} | ProfileRepository |
| POST | /auth/v1/token?grant_type=password | AuthRepository |
| POST | /auth/v1/signup | AuthRepository |
| POST | /storage/v1/object/{bucket}/{path} | ProfileRepository |

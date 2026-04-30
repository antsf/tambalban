# TambalBan Session State
# Constitution: v1.2.0 | Update after every STATUS command
# Last Updated: 2026-04-30

## Package Status

| Package | Status | Files Done | Open Issues |
|---------|--------|------------|-------------|
| core/network | ✅ Done | SupabaseService, NetworkModule, AuthInterceptor, ApiClient | — |
| core/utils | ✅ Done | SupabaseConfig, AuthPrefs, GeoUtils, Constants, IntentUtils, MapUtils, AuthErrorMapper | — |
| core/ui | ✅ Done | TambalButton, TambalTextField, AvatarView, LiveStatusDrawer | — |
| core/location | ✅ Done | LocationService | — |
| core/ads | ✅ Done | AdMobManager | — |
| auth/data | ✅ Done | AuthModels, AuthRepository, Profile, ProfileRepository | — |
| auth/ui | ✅ Done | LoginActivity, RegisterActivity, ProfileActivity, EditProfileActivity | T010-T015 polish pending |
| auth/viewmodel | ✅ Done | LoginViewModel, RegisterViewModel, ProfileViewModel | — |
| workshop/data | ✅ Done | Workshop, Review, WorkshopSubmission, WorkshopRepository, ReviewRepository, SubmissionRepository, WorkshopDbHelper, WorkshopMapper | — |
| workshop/ui | ✅ Done | WorkshopDetailActivity, AddWorkshopActivity, WorkshopListActivity, ReviewAdapter, WorkshopListAdapter | — |
| workshop/viewmodel | ✅ Done | WorkshopDetailViewModel, AddWorkshopViewModel, WorkshopListViewModel | — |
| map/data | ⬜ Not Started | — | No dedicated map repository/data models |
| map/ui | 🔄 In Progress | MainActivity, NearbyWorkshopAdapter, SearchSuggestionAdapter | T020-T024 pending |
| map/viewmodel | ✅ Done | MainViewModel | — |

Legend: ⬜ Not Started | 🔄 In Progress | ✅ Done | ❌ Blocked

---

## Last BRIEF Plan
```yaml
task: none
status: awaiting_first_task
```

---

## TEST Compliance Log

| Package | MVVM | Offline | Null Safe | Repo Pattern | Security |
|---------|------|---------|-----------|--------------|----------|
| core/network | ✅ | — | ✅ | ✅ | ✅ |
| core/utils | ✅ | — | ✅ | ✅ | ✅ |
| workshop/data | ✅ | ✅ | ⚠️ | ✅ | ✅ |

---

## Open Issues

- **011-refresh-home-screen**: T020–T024 — Search pipeline not fully implemented
- **010-refresh-login-screen**: T001–T019 — Full UI refresh not done
- **map/data**: No dedicated data layer in `map/` package (uses `workshop/data` via ViewModel)
- **SupabaseService.kt**: `getReviews` method signature was corrupted twice — monitor carefully

---

## Pending Tasks (All Features)

### 010 - Refresh Login Screen
- [ ] T001 Define purple theme colors in colors.xml
- [ ] T002 Create bg_login_gradient.xml
- [ ] T003 Update M3 pill-shaped text field styles
- [ ] T004–T009 activity_login.xml layout implementation
- [ ] T010–T011 LoginActivity + LoginViewModel updates
- [ ] T012–T016 Additional UI elements
- [ ] T017–T019 Accessibility + validation

### 011 - Refresh Home Screen
- [ ] T020 Add ilike search to SupabaseService.kt + WorkshopRepository.kt
- [ ] T021 Debounced search pipeline in MainViewModel.kt
- [ ] T022 Search suggestions RecyclerView in view_search_overlay.xml
- [ ] T023 SearchSuggestionAdapter.kt binding in MainActivity.kt
- [ ] T024 Snap-to-marker logic on suggestion click

---

## Session Resume Prompt
> Paste this at the start of a new session to restore full context:
> "Load TambalBan agent system. Constitution v1.2.0. Read `current_state.md` and `.specify/MASTER_CONTEXT.md`. Resume from last known state."

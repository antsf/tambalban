# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**TambalBan** — Android app to find nearby tire repair shops (tambal ban) in Indonesia. Users open map, see workshops around them, tap for details, call or navigate. Secondary flows: submit new workshop, write reviews, manage profile.

Target user: Indonesian drivers who need roadside tire repair.

## Build & Test Commands

```bash
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK (minified + resource shrunk)
./gradlew test                   # Run all unit tests
./gradlew testDebugUnitTest --tests "com.tambal_ban.auth.viewmodel.RegisterViewModelTest"
./gradlew connectedAndroidTest   # Instrumented tests (requires device/emulator)
./gradlew compileDebugKotlin     # Compile check only (fast)
./gradlew lint                   # Android Lint
./gradlew clean                  # Clean build outputs
```

Build config: compileSdk 35, minSdk 24, targetSdk 35, JVM 17, Kotlin 1.9.22.

## Tech Stack

| Layer | Choice |
|---|---|
| Language | Kotlin 1.9.22 |
| UI | XML layouts + ViewBinding (NO Compose) |
| Architecture | MVVM + Repository pattern |
| Networking | Retrofit 2.9 + OkHttp 4.12 + `AuthInterceptor` |
| Serialization | kotlinx-serialization (NOT Gson/Moshi) |
| Backend | Supabase (PostgreSQL REST + Auth) |
| Maps | osmdroid 6.1.18 (NOT Google Maps) |
| Local DB | `SQLiteOpenHelper` via `WorkshopDbHelper` (NOT Room) |
| Auth storage | `EncryptedSharedPreferences` via `AuthPrefs` |
| Images | Coil 2.6 |
| DI | Manual service locator via `TambalBanApp` (NO Hilt/Dagger) |
| Ads | AdMob 23 |
| Async | Kotlin Coroutines 1.7.3 |

## Custom UI Components

| Use | Instead of |
|---|---|
| `TambalButton` (variants: Primary/Secondary/Outlined, has loading state) | `MaterialButton` directly |
| `TambalTextField` (variants: Text/Email/Password, wraps TextInputLayout) | `TextInputLayout` directly |
| `AvatarView` (FrameLayout with Coil load + edit button + progress) | Manual avatar setup |
| `LiveStatusDrawer` (BottomSheetDialogFragment for workshop status) | Generic BottomSheet |
| `BaseActivity` (handles edge-to-edge + `applySafeArea()`) | `AppCompatActivity` directly |

All in `core/ui/`.

## Project Structure

```
com.tambal_ban/
├── TambalBanApp.kt          — Application; manual service locator, holds all singletons
├── auth/
│   ├── data/                — AuthRepository, ProfileRepository, AuthModels, Profile
│   ├── viewmodel/           — LoginViewModel, RegisterViewModel, ProfileViewModel
│   └── ui/                  — LoginActivity, RegisterActivity, ProfileActivity, EditProfileActivity
├── workshop/
│   ├── data/                — Workshop, Review, WorkshopSubmission, WorkshopDetailUIState
│   │   └── database/        — WorkshopDbHelper (SQLiteOpenHelper), WorkshopMapper
│   ├── viewmodel/           — WorkshopDetailViewModel, WorkshopListViewModel, AddWorkshopViewModel
│   └── ui/                  — WorkshopDetailActivity, WorkshopListActivity, AddWorkshopActivity,
│                               ReviewAdapter, WorkshopListAdapter
├── map/
│   ├── viewmodel/           — MainViewModel
│   └── ui/                  — MainActivity (LAUNCHER), NearbyWorkshopAdapter, SearchSuggestionAdapter
└── core/
    ├── network/             — SupabaseService (Retrofit interface), ApiClient, NetworkModule, AuthInterceptor
    ├── ui/                  — TambalButton, TambalTextField, AvatarView, LiveStatusDrawer, BaseActivity
    ├── location/            — LocationService
    ├── ads/                 — AdMobManager
    └── utils/               — AuthPrefs, SupabaseConfig, GeoUtils, MapUtils, IntentUtils,
                               Constants, AuthErrorMapper
```

## Code Style

**Naming:**
- Activities: `{Feature}Activity` — `WorkshopDetailActivity`
- ViewModels: `{Feature}ViewModel` — `WorkshopDetailViewModel`
- Repositories: `{Entity}Repository` — `WorkshopRepository`
- Adapters: `{Entity}Adapter` — `ReviewAdapter`
- Data models: PascalCase — `Workshop`, `Review`, `AuthResponse`
- Utils/objects: PascalCase — `AuthPrefs`, `GeoUtils`, `SupabaseConfig`

**Resource naming:**
- Layouts: `activity_{name}.xml`, `item_{name}.xml`, `fragment_{name}.xml`
- Strings: `snake_case` Indonesian/English — `status_open_now`, `workshop_detail`
- Styles: dot-notation `Guardian.Button.Primary`, `ShapeAppearance.Guardian.Pill`
- Colors: semantic — `colorPrimary`, `colorSurface`, `on_surface`
- Dimens: semantic — `radius_pill`, `radius_lg`, `touch_target_min`

**String resources:** All user-visible text in `strings.xml`. No hardcoded strings in code.

**Phone format:** Indonesian format expected (stored as-is, display as entered by user).

## Do NOT

- Use `!!` — use `?.` or `?:` instead
- Call Retrofit/SupabaseService from Activity or ViewModel — only from Repository
- Import across features: `workshop.*` must not import `auth.*` (and vice versa). Cross-feature via `Intent.setClassName()` only
- Add Firebase, Google Maps SDK, Room, Hilt, Dagger, or Gson
- Hardcode API keys — use `BuildConfig` fields or `SupabaseConfig`
- Skip Empty/Loading/Error states in any screen
- Put business logic in UI layer

## Constitution

Full rules: `.specify/memory/constitution.md`

<!-- SPECKIT START -->
**Current**: `specs/018-add-workshop/` — Add Workshop from Edit Profile feature  
Next: BUILD tasks.md phase by phase

Tech stack, structure, patterns: `.specify/context/stack.md`
<!-- SPECKIT END -->

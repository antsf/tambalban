# TambalBan Constitution

## Core Principles

### I. Package-by-Feature (NON-NEGOTIABLE)

All code lives in its feature package. `feature/X` MUST NOT import from `feature/Y`.

- `auth/` → `workshop/` imports: **FORBIDDEN**
- `workshop/` → `auth/` imports: **FORBIDDEN**
- `map/` → `auth/` or `workshop/` direct imports: **FORBIDDEN**
- Cross-feature navigation: `Intent.setClassName("com.tambal_ban", "com.tambal_ban.workshop.ui.WorkshopDetailActivity")` only
- Shared code goes in `core/` — any feature may import from `core/`

Grep enforcement: `grep -r "import com.tambal_ban.auth" app/src/main/java/com/tambal_ban/workshop/`

### II. XML-First

This project uses XML layouts with ViewBinding. No Jetpack Compose.

- No new Compose dependencies
- No `@Composable` functions
- ViewBinding is enabled: `ActivityXxxBinding.inflate(layoutInflater)`
- All Activities extend `BaseActivity` for edge-to-edge + safe area
- Custom components before raw Material3 widgets — see CLAUDE.md custom component table

### III. Simplicity — No Premature Abstractions

Follow YAGNI strictly.

- No DI frameworks (no Hilt, no Dagger, no Koin) — use `TambalBanApp` service locator
- No multi-module — single `:app` module
- No Room — use `WorkshopDbHelper` (SQLiteOpenHelper) for local DB
- No use-cases/interactors layer — ViewModel calls Repository directly
- No BaseViewModel, no generic Response wrapper classes
- Three similar lines > premature abstraction

### IV. MVVM-Chain

Strict one-way data flow. No skipping layers.

```
Activity/Adapter
    ↓ user events
ViewModel (LiveData exposed, MutableLiveData private, viewModelScope.launch)
    ↓ coroutine calls
Repository (runCatching/try-catch, no Android Context, single source of truth)
    ↓ suspend calls
SupabaseService (Retrofit interface, suspend fun, Response<T>)
    ↓ HTTP
Supabase REST API
```

- `MutableLiveData` is always `private`; expose as `LiveData`
- No network calls in Activity, Fragment, or ViewModel
- No Android `Context` in Repository
- Loading/Error/Success states mandatory for every async operation

### V. Build-First Verification

`./gradlew assembleDebug` MUST pass zero errors before any PR or task is marked complete.

- Kotlin compile check after each layer (Model/Network/Repo): `./gradlew compileDebugKotlin`
- Full build after UI layer: `./gradlew assembleDebug`
- No `!!` operator — Kotlin null safety enforced at compile time

---

## Tech Stack Constraints

- **Language**: Kotlin 1.9.22 only
- **UI**: XML + ViewBinding (no Compose, no DataBinding)
- **Architecture**: MVVM + Repository (no MVP, no MVI)
- **Networking**: Retrofit 2.9 + OkHttp 4.12 + AuthInterceptor
- **Serialization**: kotlinx-serialization (no Gson, no Moshi)
- **Backend**: Supabase REST API (no Firebase, no custom backend)
- **Maps**: osmdroid (no Google Maps SDK)
- **Local DB**: SQLiteOpenHelper (no Room)
- **Auth storage**: EncryptedSharedPreferences via AuthPrefs (no plaintext prefs)
- **Images**: Coil (no Glide, no Picasso)
- **Min SDK**: 24 (Android 7.0)
- **Secrets**: via BuildConfig fields only (no hardcoded keys)

---

## Changelog

Every change — direct or via agent — MUST be recorded in `CHANGELOG.md` under `## [Unreleased]` before the task is considered complete.

Format: `### Added / Changed / Removed / Fixed` — one line per change.

---

## Development Workflow

1. `/speckit-specify` — define WHAT (spec.md)
2. `/speckit-clarify` — resolve ambiguity (optional)
3. `/speckit-plan` — define HOW (technical plan)
4. `/speckit-tasks` — break into tasks (tasks.md)
5. `/speckit-implement` — execute phase by phase

Agents: `BRIEF:` → `BUILD:` → `TEST:`

---

## Governance

Amendments require ALL of:
1. Update this constitution
2. Update `../.claude/agents/brief.md`, `build.md`, `test.md`
3. Update `CLAUDE.md`
4. Record change in `CHANGELOG.md`

**Version**: 2.0.0 | **Ratified**: 2026-05-03 | **Last Amended**: 2026-05-03

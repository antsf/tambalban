# TambalBan

Android app to find nearby tire repair shops (tambal ban) in Indonesia.

## Features

- Map view with nearby workshop markers (OpenStreetMap)
- Workshop detail: address, phone, hours, rating, reviews
- Call or navigate to workshop in one tap
- Search workshops by name or area
- Submit new workshop for review
- Write reviews for visited workshops
- User profile with avatar

## Tech Stack

| Layer | Choice |
|---|---|
| Language | Kotlin 1.9.22 |
| UI | XML + ViewBinding, Material Design 3 |
| Architecture | MVVM + Repository |
| Backend | Supabase (PostgreSQL REST + Auth) |
| Maps | osmdroid (OpenStreetMap) |
| Networking | Retrofit 2 + OkHttp 4 |
| Auth storage | EncryptedSharedPreferences |
| Min SDK | 24 (Android 7.0) |

## Build

```bash
./gradlew assembleDebug     # debug APK
./gradlew assembleRelease   # release APK
./gradlew test              # unit tests
./gradlew lint              # lint check
```

Requires `local.properties` with `sdk.dir` pointing to Android SDK.

Build secrets (Supabase URL/key, AdMob IDs) are injected via `buildConfigField` in `app/build.gradle.kts`.

## Architecture

```
Activity/Adapter → ViewModel → Repository → SupabaseService → Supabase REST
```

Package layout under `com.tambal_ban`:

```
auth/       login, register, profile
workshop/   detail, list, add, reviews
map/        main map screen (LAUNCHER)
core/       network, ui components, utils
```

Full rules: [`.specify/memory/constitution.md`](.specify/memory/constitution.md)

## Development Workflow

New features follow: **BRIEF → BUILD → TEST**

1. `BRIEF: <feature>` — design agent produces spec + tasks
2. `BUILD: <tasks>` — build agent implements phase by phase
3. `TEST: <feature>` — test agent writes + runs unit tests

See [CLAUDE.md](CLAUDE.md) for full agent guidance and code conventions.

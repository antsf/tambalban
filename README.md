# TambalBan

Android app to find nearby tire repair shops (tambal ban) in Indonesia.

## Features

- Map view with nearby workshop markers (OpenStreetMap)
- Workshop detail: address, phone, hours, rating, reviews
- Call or navigate to workshop in one tap
- Search workshops by name or area
- Submit new workshop for review (from map or profile)
- Write reviews for visited workshops
- User profile with avatar
- Dark mode (follows system by default, toggleable in Profile)
- Banner and native ads (AdMob)

Latest changes: [`CHANGELOG.md`](./CHANGELOG.md).

## Requirements

- **JDK 17** — `java -version` must show 17
- **Android SDK** with API 35 platform installed
- **Android Studio Hedgehog+** (recommended) or CLI-only with Gradle wrapper
- Device or emulator running **Android 7.0+ (API 24+)**

## Setup

1. Clone the repo
2. Open in Android Studio, or set up `local.properties`:

```properties
sdk.dir=/path/to/Android/Sdk
```

3. Sync Gradle — all dependencies download automatically (no manual steps)

> Build secrets (Supabase URL/key, AdMob IDs) are already baked into
> `buildConfigField` in `app/build.gradle.kts`. No `.env` needed.

## Run on Device / Emulator

### Android Studio
1. Connect a device (USB debugging on) or launch an AVD
2. Press **Run** (Shift+F10) — builds and installs automatically

### Command line

```bash
# List connected devices
adb devices

# Install and launch debug build on connected device
./gradlew installDebug
adb shell am start -n com.tambal_ban/.map.ui.MainActivity
```

## Build APK

### Debug APK

```bash
./gradlew assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`

Install directly:
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Release APK

Release build minifies + shrinks resources (ProGuard enabled).

```bash
./gradlew assembleRelease
```

Output: `app/build/outputs/apk/release/app-release-unsigned.apk`

> Release APK must be signed before distribution. Use Android Studio
> **Build > Generate Signed Bundle/APK** or configure a keystore in
> `app/build.gradle.kts` under `signingConfigs`.

### Release AAB (Play Store)

```bash
./gradlew bundleRelease
```

Output: `app/build/outputs/bundle/release/app-release.aab`. Signing is configured via
`signingConfigs` in `app/build.gradle.kts`.

### Clean build

```bash
./gradlew clean assembleDebug
```

## Tests

```bash
./gradlew testDebugUnitTest          # unit tests (JVM, no device needed)
./gradlew connectedAndroidTest       # Espresso UI tests (device/emulator required)
./gradlew lint                       # static analysis
./gradlew compileDebugKotlin         # fast compile check only
```

## Tech Stack

| Layer | Choice |
|---|---|
| Language | Kotlin 1.9.22 |
| UI | XML + ViewBinding, Material Design 3 |
| Architecture | MVVM + Repository |
| Backend | Supabase (PostgreSQL REST + Auth) |
| Maps | osmdroid 6.1.18 (OpenStreetMap) |
| Networking | Retrofit 2 + OkHttp 4 |
| Auth storage | EncryptedSharedPreferences |
| Ads | AdMob 23 (banner + native) |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 35 (Android 15) |

## Architecture

```
Activity/Adapter → ViewModel → Repository → SupabaseService → Supabase REST
```

Package layout under `com.tambal_ban`:

```
auth/       login, register, profile
workshop/   detail, list, add, reviews
map/        main map screen (LAUNCHER)
core/       network, ui components, utils, ads
```

Full rules: [`.specify/memory/constitution.md`](.specify/memory/constitution.md)

## Development Workflow

New features follow: **BRIEF → BUILD → TEST**

1. `BRIEF: <feature>` — design agent produces spec + tasks
2. `BUILD: <tasks>` — build agent implements phase by phase
3. `TEST: <feature>` — test agent writes + runs unit tests

See [CLAUDE.md](CLAUDE.md) for full agent guidance and code conventions.

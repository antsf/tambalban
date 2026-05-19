# TambalBan — Analisis & Roadmap CTO

> **Tanggal:** 2026-05-20
> **Analisis:** Comprehensive codebase review by CTO/Lead Architect
> **Skor Kualitas Keseluruhan:** 48/100

---

## Executive Summary

TambalBan adalah aplikasi Android fungsional dengan arsitektur MVVM + Repository yang solid untuk MVP tahap awal. Kode bersih, struktur folder rapi, dan pilihan tech stack masuk akal (osmdroid, Supabase, Retrofit, manual DI). Namun, terdapat ~30+ masalah mulai dari **security (ProGuard mati, API key hardcoded)**, **bug logika (radius 30km, status "Buka" hardcode)**, hingga **scaling blockers (service locator, LiveData, SQLite tanpa Room)**.

**Prioritas utama:** Fix critical bugs → Enable security → Perbaiki arsitektur → Monetisasi B2B.

---

## Skor Kualitas

| Dimensi | Skor | Notes |
|---|---|---|
| **Architecture Quality** | 65/100 | MVVM + Repository benar, tapi AndroidViewModel di semua VM, LiveData instead of StateFlow, manual DI |
| **Scalability** | 35/100 | Service locator (TambalBanApp), tidak modular, LiveData, SQLiteOpenHelper, tidak ada dynamic feature |
| **Maintainability** | 55/100 | Wildcard imports di semua file, duplikasi kode (formatPhoneNumber), dead code (LiveStatusDrawer), .bak file ter-commit |
| **Performance** | 50/100 | ProGuard mati (APK ~30MB+), image cache tidak dikonfigurasi, tile cache 50MB ok, shimmer loading sudah ada |
| **Monetization Readiness** | 30/100 | AdMob banner hanya di MainActivity, native ad tidak pernah dipanggil, tidak ada IAP/premium |
| **UX Quality** | 60/100 | Loading/empty/error states ada, shimmer baik, tapi status "Buka" hardcode, form validation tidak konsisten |
| **Play Store Readiness** | 40/100 | Crashlytics + Analytics aktif, in-app update ada, tapi ProGuard mati, testing minim, bugs serius |

---

## Critical Bugs (Prioritas 🔴)

### 🔴 #1 — Search Radius Bug (10x)
**Lokasi:** `MainViewModel.kt:139`

```kotlin
// Current (SALAH):
repository.getNearbyWorkshops(lat, lon, radiusKm * 10000, getApplication())

// radiusKm = 3, 3 * 10000 = 30000 meter = 30km
// Yang benar: radiusKm * 1000 = 3000 meter = 3km
```

- [ ] **Fix:** Ganti `radiusKm * 10000` → `radiusKm * 1000`

### 🔴 #2 — ProGuard / Obfuscation Disabled
**Lokasi:** `app/build.gradle.kts:42-43`

```kotlin
// Current (TIDAK AMAN):
isMinifyEnabled = false
isShrinkResources = false
```

API keys di `buildConfigField` bisa dibaca siapa saja yang decompile APK.

- [ ] **Fix:** Set `isMinifyEnabled = true`, `isShrinkResources = true`
- [ ] **Fix:** Update `proguard-rules.pro` untuk kotlinx-serialization, Retrofit, osmdroid
- [ ] **Fix:** Test obfuscation dengan `./gradlew assembleRelease` dan verifikasi

### 🔴 #3 — DB Migration Drop Data
**Lokasi:** `WorkshopDbHelper.kt:47-49`

```kotlin
override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    db.execSQL(SQL_DELETE_WORKSHOPS) // ← DROP TABLE = DATA HILANG
    onCreate(db)
}
```

- [ ] **Fix:** Implementasi ALTER TABLE atau migrasi data incremental
- [ ] **Fix:** Hapus data hanya jika schema benar-benar tidak kompatibel

### 🔴 #4 — Status Selalu "Buka"
**Lokasi:** `WorkshopDetailViewModel.kt:81-85`

```kotlin
// Current: hardcode "BUKA SEKARANG"
val statusTextRes = R.string.status_open_now
val statusText = context.getString(statusTextRes)
val statusColor = R.color.success
```

Tidak parsing `opening_hours`. Semua workshop selalu "Buka".

- [ ] **Fix:** Parse `opening_hours` field, compare dengan current time
- [ ] **Fix:** Jika null/tidak bisa di-parse, tampilkan "-" (tidak tahu)
- [ ] **Fix:** Update warna status (hijau = buka, merah = tutup, abu = tidak tahu)

### 🔴 #5 — Address & Phone Null Display
**Lokasi:** `WorkshopDetailActivity.kt:107`, `WorkshopDetailViewModel.kt:96-99`

```kotlin
// Saat address null, tetap nampilin "Alamat" (R.string.address)
// Saat phone null, tetap nampilin "Nomor Telepon"
```

- [ ] **Fix:** Hide address row jika null
- [ ] **Fix:** Hide phone row jika null
- [ ] **Fix:** Jangan set default string — gunakan null check

### 🔴 #6 — Dead Code & .bak File
**Lokasi:** `core/ui/LiveStatusDrawer.kt`, `core/ui/LiveStatusDrawer.kt.bak`

`LiveStatusDrawer` (BottomSheetDialogFragment) tidak pernah dipanggil dari mana pun.

- [ ] **Hapus:** `LiveStatusDrawer.kt`
- [ ] **Hapus:** `LiveStatusDrawer.kt.bak`

### 🔴 #7 — Wildcard Imports (Semua File)
Hampir setiap file di `workshop/`, `auth/`, `map/` punya:

```kotlin
import com.tambal_ban.workshop.ui.*
import com.tambal_ban.workshop.viewmodel.*
import com.tambal_ban.workshop.data.*
```

Di **file repository**. Ini polusi namespace dan berbahaya untuk compilation.

- [ ] **Fix:** Ganti semua wildcard dengan import spesifik

### 🔴 #8 — Splash Routing Rusak
**Lokasi:** `SplashActivity.kt:25`

Selalu navigasi ke `MainActivity`, abaikan login status. Code login check di-comment.

- [ ] **Fix:** Aktifkan routing logic: cek `authPrefs.isLoggedIn()` → MainActivity atau LoginActivity

---

## Architecture Problems (Prioritas 🟠)

### 🟠 — LiveData → StateFlow Migration
**Masalah:** Semua ViewModel pakai `LiveData`. Lifecycle-aware tapi tidak coroutine-native.

- [ ] **Migrasi:** Ganti `MutableLiveData<T>` → `MutableStateFlow<T>`
- [ ] **Migrasi:** Ganti `val data: LiveData<T>` → `val data: StateFlow<T>`
- [ ] **Migrasi:** Activity observer → `lifecycleScope.launch { repeatOnLifecycle {} }`
- [ ] **Catatan:** Lakukan per-feature bertahap, mulai dari `MainViewModel`

**Mengapa:** StateFlow coroutine-native, no lifecycle leak, bisa pakai Flow operators, testing lebih mudah pakai Turbine.

### 🟠 — AndroidViewModel → ViewModel
**Masalah:** Semua ViewModel extends `AndroidViewModel`, butuh `Application` context.

- [ ] **Migrasi:** Ubah ke `ViewModel()` + `SavedStateHandle`
- [ ] **Dampak:** Bisa unit test tanpa Android dependency

### 🟠 — Manual DI → Hilt
**Masalah:** Service locator pattern via `TambalBanApp`:

```kotlin
// Di setiap ViewModel:
(application as TambalBanApp).workshopRepository
```

Setiap fitur baru → tambah property ke `TambalBanApp` (open-closed principle violation).

- [ ] **Setup:** Tambah Hilt dependency ke `build.gradle.kts`
- [ ] **Setup:** `@HiltAndroidApp` di `TambalBanApp`
- [ ] **Migrasi:** `@AndroidEntryPoint` di Activities + ViewModels
- [ ] **Migrasi:** Hapus semua `(application as TambalBanApp).xxx`
- [ ] **Catatan:** Bisa partial — mulai dengan 1 feature dulu

### 🟠 — API Key Management
**Lokasi:** `app/build.gradle.kts:24-30`

```kotlin
buildConfigField("String", "SUPABASE_ANON_KEY", "\"sb_publishable_...\"")
buildConfigField("String", "ADMOB_BANNER_AD_UNIT", "\"ca-app-pub-...\"")
```

Key ada di source code + BuildConfig. Dengan ProGuard mati, siapa pun bisa baca.

- [ ] **Fix:** Gunakan `google-secrets-gradle-plugin` untuk local development
- [ ] **Fix:** Di CI, inject via environment variables
- [ ] **Fix:** Jangan commit production keys
- [ ] **Fix:** Aktifkan Supabase Row Level Security + Airtight RLS policies

---

## UI/UX Improvements (Prioritas 🟡)

### 🟡 — Onboarding Flow
Saat ini splash → langsung maps. Tidak ada onboarding, tidak ada value proposition.

- [ ] **Buat:** 3-screen onboarding carousel
  - Screen 1: "Cari tambal ban terdekat dalam hitungan detik"
  - Screen 2: "Lihat detail, telepon, navigasi dalam satu tap"
  - Screen 3: "Bagikan review bantu pengguna lain"
- [ ] **Tambahkan:** Skip button + indicator dots
- [ ] **Tampilkan:** Hanya sekali (SharedPreferences flag)

### 🟡 — Emergency Mode
Ini adalah viral hook utama aplikasi. "Ban bocor? Satu tap, 3 workshop terdekat."

- [ ] **Buat:** Emergency FAB besar (merah berdenyut)
- [ ] **Buat:** Emergency screen dengan:
  - 3 workshop terdekat + distance + phone
  - Auto-call button
  - Navigate button
  - Share location to WhatsApp
- [ ] **Tambahkan:** Analytics event `emergency_mode_activated`

### 🟡 — Favorites / Bookmark
- [ ] **Tambahkan:** Bookmark icon di workshop detail
- [ ] **Tambahkan:** Tab "Tersimpan" di bottom sheet
- [ ] **Simpan:** Di SQLite lokal (identik dengan cache)

### 🟡 — Search Enhancement
Saat ini search hanya di MainActivity.

- [ ] **Tambahkan:** Search di WorkshopListActivity
- [ ] **Tambahkan:** Recent searches (SharedPreferences)
- [ ] **Tambahkan:** Search filter (kota, provinsi)

### 🟡 — Phone Number Format
- [ ] **Buat:** Utility function di `core/utils` (sudah ada duplikasi di ProfileActivity + EditProfileActivity)
- [ ] **Hapus:** Duplikasi di kedua Activity
- [ ] **Tambahkan:** Format konsisten: `+62xxx-xxxx-xxxx`

### 🟡 — Address Format
- [ ] **Format:** `Jl. Name, City, Province` (gabung komponen)
- [ ] **Hide:** Jika null, jangan tampilkan row

---

## Performance (Prioritas 🟡)

### 🟡 — ProGuard Enablement
- [ ] **Aktifkan:** `isMinifyEnabled = true`
- [ ] **Aktifkan:** `isShrinkResources = true`
- [ ] **Test:** `./gradlew assembleRelease` → verifikasi APK size turun (estimasi: 30MB → 12-15MB)
- [ ] **Test:** Fungsionalitas critical path setelah minification

### 🟡 — Coil Image Cache
**Lokasi:** Tidak ada konfigurasi cache eksplisit

- [ ] **Tambahkan:** `ImageLoader` dengan disk cache 100MB di `TambalBanApp`
- [ ] **Tambahkan:** Memory cache 25% of available heap
- [ ] **Apply:** Ke Coil default instance

### 🟡 — Map Marker Optimization
**Masalah:** Tidak ada clustering. Dengan 100+ workshop di viewport, marker overlap.

- [ ] **Implementasi:** Marker clustering untuk zoom < 15
- [ ] **Implementasi:** Gunakan osmdroid `MarkerClusterer` atau custom
- [ ] **Optimasi:** `Marker.setVisible()` instead of remove/re-add
- [ ] **Debounce:** Map move events (300ms) sebelum API call

### 🟡 — Baseline Profiles
- [ ] **Setup:** Module `core-profiling` dengan Macrobenchmark
- [ ] **Generate:** Baseline Profile untuk critical path
- [ ] **Apply:** Ke release build

### 🟡 — Startup Optimization
- [ ] **Lazy-init:** AdMob (tidak perlu di App.onCreate)
- [ ] **Lazy-init:** Firebase Analytics (prioritas rendah untuk first frame)
- [ ] **Pindahkan:** Init yang tidak critical ke background thread

---

## Monetization (Prioritas 🟠)

### Current State
- AdMob banner di MainActivity (1 placement)
- Native ad loader ada tapi tidak dipanggil
- Tidak ada IAP, subscription, premium features

### 🟠 — Ad Placement Optimization

#### Workshop Detail
- [ ] **Tambahkan:** Native ad di antara header workshop dan reviews section
- [ ] **Tambahkan:** Banner ad di bottom (sebelum footer)
- **Estimasi RPM:** $8-15

#### Workshop List
- [ ] **Tambahkan:** Native ad setiap 5 item di RecyclerView
- **Estimasi RPM:** $6-12

#### Add Workshop
- [ ] **Tambahkan:** Interstitial ad setelah submit sukses
- **Estimasi RPM:** $10-20

#### Search Results
- [ ] **Tambahkan:** Banner di bagian bawah hasil pencarian
- **Estimasi RPM:** $2-5

### 🟠 — Ad Mediation
- [ ] **Tambah:** Meta Audience Network sebagai mediation
- **Dampak:** Fill rate 85% → 95%+, RPM naik 30-50%

### 🟠 — Premium Features (IAP)

#### Premium — Remove Ads ($2.99 one-time)
- [ ] **Buat:** `BillingManager` untuk Google Play Billing
- [ ] **Tambahkan:** Premium check di setiap ad placement
- [ ] **Tambahkan:** UI untuk upgrade (Profile screen)

#### Workshop Owner Account ($9.99/month)
- [ ] **Buat:** Dashboard untuk workshop owner
- [ ] **Buat:** Fitur claim listing (verifikasi kepemilikan)
- [ ] **Buat:** Analytics (views, calls, directions)
- [ ] **Market:** Target 200 workshop di Jakarta × $10 avg = $2,000 MRR

#### Emergency SOS ($0.99/month)
- [ ] **Buat:** Premium emergency feature
- [ ] **Buat:** One-tap find 3 nearest + auto-call sequence
- **Value Prop:** Safety — "kalau ban bocor tengah malam, satu tap dapat bantuan"

---

## Testing Strategy (Prioritas 🟡)

### 🟡 — Unit Tests

#### Repository Tests
- [ ] **AuthRepositoryTest** — login, register, logout, token management
- [ ] **WorkshopRepositoryTest** — CRUD, search, nearby, bounds
- [ ] **ReviewRepositoryTest** — getReviews, submitReview
- [ ] **ProfileRepositoryTest** — getProfile, updateProfile, uploadAvatar

#### ViewModel Tests
- [ ] **LoginViewModelTest** — existing, expand coverage
- [ ] **RegisterViewModelTest** — existing, expand coverage
- [ ] **MainViewModelTest** — search, location, nearby fetch
- [ ] **WorkshopDetailViewModelTest** — load workshop, submit review
- [ ] **AddWorkshopViewModelTest** — form validation, submit

#### Utility Tests
- [ ] **GeoUtilsTest** — calculateDistance, formatDistance
- [ ] **AuthErrorMapperTest** — error message mapping
- [ ] **AuthPrefsTest** — read/write/clear

### 🟡 — Instrumentation Tests
- [ ] **MainActivityTest** — search, bottom sheet, marker click
- [ ] **WorkshopDetailActivityTest** — call, navigate, review
- [ ] **LoginActivityTest** — field validation, button states

### 🟡 — CI Setup
- [ ] **GitHub Actions:** lint + compile + unit test on every PR
- [ ] **GitHub Actions:** connected test on merge to main (emulator)
- [ ] **GitHub Actions:** release build + upload to Play Store (internal track)

---

## Scalability Roadmap

### Phase 1: Hilt DI
- [ ] Setup Hilt (`@HiltAndroidApp`, `@AndroidEntryPoint`)
- [ ] Buat `@Module` untuk repository, service, prefs
- [ ] Hapus `TambalBanApp` sebagai service locator

### Phase 2: StateFlow
- [ ] Migrasi per-feature: map → workshop → auth
- [ ] Ganti Activity observer ke `repeatOnLifecycle`
- [ ] Update testing ke Turbine

### Phase 3: Multi-module
- [ ] Split `:core` → `:core:ui`, `:core:network`, `:core:utils`
- [ ] Split `:feature:auth`, `:feature:workshop`, `:feature:map`
- [ ] Hanya jika codebase > 75 files atau build time > 5 menit

### Phase 4: Room (Jika dibutuhkan)
- [ ] Hanya jika schema SQLite menjadi kompleks (>5 tabel)
- [ ] Saat ini `SQLiteOpenHelper` cukup untuk 1 tabel

---

## Security Checklist

### Critical
- [ ] Enable ProGuard (`isMinifyEnabled = true`)
- [ ] Test obfuscation (APK decompile check)
- [ ] Rotate exposed Supabase API key (siapa pun bisa baca dari APK)
- [ ] Tambah Supabase RLS yang ketat (server-side)

### High
- [ ] SSL pinning untuk OkHttp (Supabase connection)
- [ ] Biometric lock untuk profile (opsional)
- [ ] Rate limiting client-side (debounce sudah ada, tapi perlu formal)
- [ ] Token refresh logic (sebelum JWT expired)

### Medium
- [ ] Input sanitization di AddWorkshop form
- [ ] Root detection (basic)
- [ ] Logout invalidate server session

---

---
## AI Development Workflow

### AI Coding Stack (Recommended)

| Tool | Use | Priority |
|---|---|---|
| **Claude Code** | Primary coding agent — best Android/Kotlin understanding | Must Have |
| **OpenCode AI** | Task runner, context management, multi-session orchestration | Must Have |
| **GitHub MCP** | PR creation, code review, issue tracking | Must Have |
| **Filesystem MCP** | Direct file I/O for agent orchestration | Must Have |
| **Android/ADB MCP** | Build, lint, test automation via Gradle | Nice to Have |
| **Firebase MCP** | Crashlytics + Analytics monitoring | Nice to Have |
| **Play Console MCP** | Release tracking, crash rates, review scores | Nice to Have |
| **Slack/Discord MCP** | Build notifications, crash alerts, release announcements | Nice to Have |

**ADK Recommendation:** Tidak perlu Google ADK untuk project ini. Claude Code + MCP servers sudah cukup. ADK baru berguna jika: codebase >100 files, multiple backend services, atau microservices. Saat ini single-module Android app.

### Claude Code Setup (Primary)

```
CLAUDE.md — project conventions (auto-read setiap session start)
  ├── Build commands (./gradlew compileDebugKotlin, test, lint)
  ├── Tech stack (Kotlin, Retrofit, Supabase, osmdroid)
  ├── Code style (no `!!`, strings in xml, MVVM pattern)
  └── Architecture rules (abstraction boundary, no cross-feature imports)

MCP servers:
  - GitHub (PR + issues)
  - Filesystem (file access)

Workflow mode:
  - Plan mode: arsitektur & design decision (manual approval)
  - Build mode: implementasi task per file (auto execute)
```

**WHY:** Claude Code punya pemahaman Kotlin/Android terbaik dibanding AI coding tools lain. MCP servers extend reach ke GitHub + filesystem.
**BENEFIT:** Context retention sepanjang session, pattern follow dari existing code, compile-check otomatis.
**TRADEOFF:** Kadang over-engineer untuk task sederhana. Monitor dan correct jika perlu.

### OpenCode AI Setup (Complementary)

```
OpenCode configuration:
  - Agent: general-purpose (Android/Kotlin expert)
  - Auto-approve: lint, compile check, test run
  - Manual approval: file creation, refactor, dependency changes
  - Context: project CLAUDE.md + ANALYSIS.md + current spec
  - Model: big-pickle (default)

Usage pattern:
  - Feature task: OpenCode executes tasks.md sequentially
  - Bug fix: OpenCode + Claude Code parallel investigation
  - Refactor: OpenCode batch edit, Claude Code review
```

**WHY:** OpenCode handles task orchestration + context management lebih baik untuk multi-step workflows.
**BENEFIT:** Task execution terstruktur, dependency ordering, progress tracking.
**TRADEOFF:** Setup awal perlu konfigurasi agents. Overlap dengan Claude Code di beberapa area.

### Zed Editor Setup

```
Zed configuration:
  {
    "lsp": {
      "kotlin-language-server": {
        "settings": {
          "format": true,
          "lint": true
        }
      }
    },
    "formatter": "ktlint",
    "format_on_save": true,
    "tasks": [
      {
        "label": "Compile Kotlin",
        "command": "./gradlew compileDebugKotlin"
      },
      {
        "label": "Run Unit Tests",
        "command": "./gradlew testDebugUnitTest"
      },
      {
        "label": "Lint Check",
        "command": "./gradlew lint"
      }
    ],
    "snippets": {
      "viewmodel": "class ${1:Feature}ViewModel(application: Application) : AndroidViewModel(application) { ... }",
      "repository": "class ${1:Feature}Repository(private val service: SupabaseService) { ... }",
      "activity": "class ${1:Feature}Activity : BaseActivity() { ... }"
    }
  }
```

**WHY:** Zed lebih cepat dari VSCode, built-in terminal untuk Gradle, LSP support untuk Kotlin.
**BENEFIT:** Startup <1s, format-on-save, task runner terintegrasi.
**TRADEOFF:** Tidak ada Layout Inspector / ADB integration. Gunakan Android Studio untuk XML layout editing + profiling.

### Debugging Tools

| Tool | Use | Stage |
|---|---|---|
| **Android Studio Layout Inspector** | XML layout debugging, view hierarchy | Development |
| **Chucker** | HTTP request/response inspection in-app | Development |
| **LeakCanary** | Memory leak detection otomatis | Development + QA |
| **Firebase Crashlytics** | Production crash monitoring + stack traces | Production |
| **Android Profiler (AS)** | CPU/memory/network profiling | Optimization |
| **StrictMode** (debug) | Thread + VM policy violation detection | Development |

```kotlin
// TambalBanApp.kt — debug mode
if (BuildConfig.DEBUG) {
    StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
        .detectAll()
        .penaltyLog()
        .build())
    StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
        .detectAll()
        .penaltyLog()
        .build())
}
```

### Testing Tools

| Tool | Use | Current Status |
|---|---|---|
| **JUnit 5** | Unit tests (migrate from JUnit 4) | ❌ Not used |
| **MockK** | Kotlin mocking (already used) | ✅ Active |
| **Turbine** | StateFlow/Flow testing | ❌ Not used |
| **Espresso** | UI tests | ✅ Active (minimal) |
| **Robolectric** | Fast UI tests without emulator | ❌ Not used |
| **Barista** | Espresso wrapper (less boilerplate) | ❌ Not used |

### CI/CD Pipeline

```yaml
# .github/workflows/ci.yml
name: CI

on:
  pull_request:
    branches: [main]
  push:
    branches: [main]

jobs:
  lint:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'
      - uses: gradle/actions/setup-gradle@v3
      - run: ./gradlew lint
      - run: ./gradlew compileDebugKotlin

  unit-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'
      - uses: gradle/actions/setup-gradle@v3
      - run: ./gradlew testDebugUnitTest
      - uses: actions/upload-artifact@v4
        if: failure()
        with:
          name: test-reports
          path: app/build/reports/tests/

  connected-test:
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    steps:
      - uses: actions/checkout@v4
      - uses: reactivecircus/android-emulator-runner@v2
        with:
          api-level: 30
          arch: x86_64
          target: google_apis
          script: ./gradlew connectedDebugAndroidTest

  release:
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'
      - uses: gradle/actions/setup-gradle@v3
      - run: ./gradlew assembleRelease
        env:
          KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
          KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
          KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}
      - uses: r0adkll/upload-google-play@v1
        with:
          serviceAccountJson: ${{ secrets.PLAY_SERVICE_ACCOUNT }}
          packageName: com.tambal_ban
          releaseFile: app/build/outputs/bundle/release/app-release.aab
          track: internal
```

**Branch Strategy:**
```
main (production)
  └── dev (integration)
       ├── feature/012-add-workshop (fitur baru dari spec)
       ├── fix/search-radius-10x (bug fix)
       └── refactor/hilt-di-migration (refactor)
```

**WHY:** PR gates catch issues early. Auto-deploy to Play Store internal track on main merge.
**BENEFIT:** Setiap PR otomatis di-lint, compile-check, dan test. Release build terverifikasi.
**TRADEOFF:** Connected tests butuh Firebase Test Lab (~$100/bulan) atau self-hosted emulator (setup 1 hari).

### Code Review Workflow

```
Stage 1: AI Review (auto on PR)
  ├── Lint check (./gradlew lint)
  ├── Compile check (./gradlew compileDebugKotlin)
  ├── Convention check (no `!!`, strings in XML, no cross-feature import)
  └── Test pass (./gradlew testDebugUnitTest)

Stage 2: Human Review
  ├── Architecture: apakah sesuai spec?
  ├── Security: ada data leak?
  ├── Performance: ada N+1 query?
  └── UX: user-friendly?

Stage 3: Merge
  ├── Squash & merge
  └── Delete branch
```

**AI Review Prompt Template:**
```
Review PR ini untuk:
1. Bugs (logic error, null safety, race condition)
2. Security (API key leak, input validation, auth bypass)
3. Performance (N+1 query, memory leak, layout overdraw)
4. Convention (no `!!`, strings in XML, MVVM pattern)
5. Test coverage (apakah ada test untuk kode baru?)
```

### Prompt Workflow

```
SETUP:
1. Read specs/<feature>/spec.md
2. Read specs/<feature>/tasks.md  
3. Read existing code patterns (cari file serupa)
4. Check CLAUDE.md + constitution.md rules

EXECUTE:
5. Generate code sesuai pattern existing
6. Verify: ./gradlew compileDebugKotlin
7. Write tests
8. Verify: ./gradlew testDebugUnitTest

SELF-CRITIQUE:
9. "Review kode di atas untuk bugs, security, performance, convention violations, test coverage gaps"

COMMIT:
10. Conventional Commits: "feat: add workshop submission from profile"
```

### Documentation Workflow

```
CLAUDE.md → konvensi project (auto-read tiap session)
  ├── Build commands
  ├── Code style (no `!!`, strings in XML, etc)
  ├── Architecture rules
  ├── Tech stack
  └── ⚠️ Always update CHANGELOG.md after changes

ANALYSIS.md → CTO review + roadmap (dokumen ini)
  ├── Scorecard
  ├── Bugs & priorities
  ├── Task tracker dengan checkbox
  ├── ✅ Update checklist saat task selesai
  └── ✅ Update CHANGELOG.md setiap ada code change

specs/<feature>/ → per-fitur documentation
  ├── spec.md (requirements)
  ├── plan.md (technical design)
  ├── tasks.md (implementation tasks) — ✅ centang per task
  └── checklists/ — QA checklists

README.md → build instructions (human-written)

CHANGELOG.md → ✨ WAJIB UPDATE SETIAP KALI ADA PERUBAHAN
  ├── feat → ### Added
  ├── fix  → ### Fixed
  ├── refactor/perf → ### Changed
  ├── chore/docs → ### Changed
  └── Format: Keep a Changelog
```

---

## MCP & Agent Orchestration

### Recommended Agent Architecture

```
                        ┌─────────────────────┐
                        │   Orchestrator       │
                        │   (Claude Code)      │
                        └──────────┬──────────┘
                                   │
               ┌───────────────────┼───────────────────┐
               │                   │                   │
        ┌──────▼──────┐   ┌───────▼───────┐   ┌───────▼──────┐
        │  Architect   │   │    Builder    │   │    Reviewer  │
        │  (Manual)    │   │   (Auto)      │   │   (Auto)    │
        └──────┬──────┘   └───────┬───────┘   └───────┬──────┘
               │                   │                   │
        ┌──────▼──────┐   ┌───────▼───────┐           │
        │  Investigator│   │  Test Agent   │           │
        │  (Auto)      │   │  (Auto)       │           │
        └─────────────┘   └───────────────┘           │
                                                       │
               ┌───────────────────────────────────────┘
               │
        ┌──────▼──────┐
        │   Human     │
        │   Approval  │
        └─────────────┘
```

### Agent Types & Responsibilities

| Agent | Responsibility | Auto/Manual | Trigger |
|---|---|---|---|
| **Orchestrator** | Route tasks, manage flow, track progress | Auto | Session start |
| **Architect Agent** | Design decisions, tech debt, architecture review | **Manual (approval)** | Feature start |
| **Investigator** | Find code locations, grep, read existing patterns | Auto | Before any edit |
| **Builder Agent** | 1-2 file edits, follow existing patterns | Auto | Per task |
| **Test Agent** | Generate + run unit tests, verify coverage | Auto | After build |
| **Review Agent** | Code review, convention check, security scan | Auto | On PR |
| **Refactor Agent** | Batch rename, extract util, inline, restructure | **Manual (approval)** | Per refactor phase |
| **Performance Agent** | Baseline Profile, startup optimization, image cache | **Manual** | Optimization phase |
| **Security Agent** | API key scan, ProGuard audit, SSL pinning check | Auto | On every PR |
| **Monetization Agent** | Ad placement analysis, IAP pricing, revenue tracking | **Manual** | Monetization phase |
| **UX Agent** | Accessibility check, layout consistency, onboarding flow | **Manual** | Feature complete |
| **QA Agent** | Instrumentation test, smoke test, regression | Auto (CI) | On merge to main |

### Agent Communication Flow

```
1. [User Input] "Add workshop from profile screen"
   ↓
2. [Orchestrator] Cek specs/018-add-workshop/*, cek current branch
   ↓
3. [Architect Agent — Manual] 
   - Read spec.md + plan.md
   - Cek existing code patterns
   - Output: design decision + files to touch
   → HUMAN APPROVE
   ↓
4. [Investigator — Auto] (parallel)
   - Cari semua file relevan: WorkshopRepository, AddWorkshopViewModel, ProfileActivity
   - Output: file paths + patterns
   ↓
5. [Builder Agent — Auto] (sequential per file)
   - Edit file 1: AddWorkshopViewModel (tambah method)
   - Edit file 2: ProfileActivity (tambah button)
   - Edit file 3: strings.xml (tambah string)
   → Setiap edit: compile check
   ↓
6. [Test Agent — Auto]
   - Generate unit test untuk method baru
   - Run ./gradlew testDebugUnitTest
   ↓
7. [Review Agent — Auto]
   - Cek: no `!!`, strings in XML, cross-feature import
   - Cek: code pattern match existing
   → OUTPUT: review summary
   ↓
8. [Orchestrator]
   - Summary: files changed, tests passed, review passed
   → HUMAN APPROVE COMMIT
   ↓
9. [Commit + PR]
```

### Context Sharing Strategy

```
Shared Context (passed antar agent):
  {
    "feature": "018-add-workshop",
    "files_touched": [
      "auth/ui/ProfileActivity.kt",
      "workshop/viewmodel/AddWorkshopViewModel.kt",
      "res/values/strings.xml"
    ],
    "patterns": {
      "viewmodel": "AndroidViewModel + LiveData (current)",
      "di": "(application as TambalBanApp).xxx",
      "navigation": "Intent with setClassName()"
    },
    "constraints": {
      "no_cross_feature_import": true,
      "strings_in_xml": true,
      "no_bang_bang": true
    }
  }
```

**WHY:** Agent butuh konteks file mana yang sudah disentuh, pattern apa yang harus diikuti, constraint apa yang berlaku.
**BENEFIT:** Agent tidak ngawur, output konsisten, tidak melanggar aturan project.
**TRADEOFF:** Setup context sharing perlu template/format yang disepakati.

### Memory Strategy

```
Short-term (per session):
  - Files touched dalam session ini
  - Current task dari tasks.md
  - Compile errors yang terjadi

Long-term (per project):
  - CLAUDE.md (konvensi)
  - ANALYSIS.md (roadmap + priorities)
  - specs/* (feature documentation)
  - Git log (pattern commit messages)

No persistence needed: Context cukup dari filesystem + git.
Agent state tidak perlu disimpan — selalu baca dari current codebase.
```

### Prompt Strategy

```
System prompt template:
  "Kamu adalah Android developer untuk project TambalBan.
   - Bahasa: Kotlin 1.9.22
   - UI: XML + ViewBinding (BUKAN Compose)
   - Architecture: MVVM + Repository  
   - Network: Retrofit + Supabase
   - Maps: osmdroid
   - DI: Manual via TambalBanApp (sebelum migrasi ke Hilt)
   - CONSTRAINT: Jangan import feature A dari feature B
   - CONSTRAINT: Semua user-facing text di strings.xml
   - CONSTRAINT: Jangan pakai !!
   - CONSTRAINT: Data hanya dari Repository, bukan langsung SupabaseService"
```

### Safety Strategy

```
1. Read-only first: Investigator cek dulu sebelum Builder edit
2. Compile gate: Setiap edit harus compile pass
3. Test gate: Setiap PR harus test pass
4. No bulk delete: Jangan hapus file tanpa konfirmasi
5. No production keys: Jangan commit API keys atau secrets
6. Rollback: Git revert jika ada masalah
7. Diff review: Selalu review diff sebelum commit
```

### Tasks Never Automate

| Task | Risk | Alternative |
|---|---|---|
| Play Store listing | Reputation damage | Human writes description |
| API key rotation | Production outage | Human rotates + verifies |
| Database migration (production) | Data loss | Human writes + reviews migration SQL |
| Major dependency version bump | Breaking changes | Human reviews changelog + tests |
| Signing config changes | Build break | Human updates + signs |
| Pricing/monetization | Revenue loss | Product team decides |
| Privacy policy | Legal risk | Lawyer reviews |
| App permissions | User trust | Product + legal reviews |

### Multi-Agent vs Single Agent Decision

**Current (codebase < 50 files):**
- Single agent (Claude Code) + MCP servers — CUKUP
- Tidak perlu multi-agent orchestration
- Tidak perlu specialized agents

**Future (> 100 files, > 5 features):**
- Orchestrator + specialized agents
- Parallel task execution untuk independent features
- Dedicated review agent untuk setiap PR

**Decision for TambalBan: Single agent is enough.**
- Reason: Kodebelum cukup besar untuk justify overhead multi-agent
- Tradeoff: Sequential execution (lebih lambat untuk parallel tasks)
- Migration path: Tambah agents bertahap saat codebase grows

---

### Recommended MCP Server Architecture

```
Claude Code (Orchestrator)
  ├── GitHub MCP
  │   ├── List open issues
  │   ├── Create PR with diff
  │   └── Add review comments
  ├── Filesystem MCP
  │   ├── Read/write files
  │   ├── Glob search
  │   └── Grep content
  └── ADB MCP (future)
      ├── Run gradle tasks
      ├── Parse lint output
      └── Parse test output
```

```
OpenCode (Task Runner)
  └── Filesystem MCP
      ├── Read task list
      ├── Track progress
      └── Write completion report
```

---

## Priority Roadmap

### Quick Wins (1-3 hari)

| Task | Estimasi | Prioritas |
|---|---|---|
| [ ] Fix search radius bug (`* 10000` → `* 1000`) | 30 menit | 🔴 Critical |
| [ ] Fix address/phone null display | 30 menit | 🔴 Critical |
| [ ] Remove dead code (LiveStatusDrawer + .bak) | 15 menit | 🔴 Critical |
| [ ] Remove wildcard imports (ganti ke specific) | 1 jam | 🟠 High |
| [ ] Extract formatPhoneNumber ke utils | 30 menit | 🟠 High |
| [ ] Fix splash routing (aktifkan auth check) | 30 menit | 🟠 High |

### Short-Term (1-4 minggu)

| Task | Estimasi | Prioritas |
|---|---|---|
| [ ] Enable ProGuard + test obfuscation | 4 jam | 🔴 Critical |
| [ ] Fix DB migration (ALTER TABLE, bukan DROP) | 2 jam | 🔴 Critical |
| [ ] Implementasi status logic (parsing opening_hours) | 4 jam | 🟠 High |
| [ ] Tambah Coil image cache config | 2 jam | 🟡 Medium |
| [ ] Tambah debounce map pan events | 1 jam | 🟡 Medium |
| [ ] Unit test untuk semua ViewModel | 2 hari | 🟡 Medium |
| [ ] Hilt setup + migrasi 1 feature | 3 hari | 🟠 High |
| [ ] Onboarding carousel (3 screens) | 1 hari | 🟡 Medium |

### Mid-Term (1-3 bulan)

| Task | Estimasi | Prioritas |
|---|---|---|
| [ ] LiveData → StateFlow migration (semua feature) | 4 hari | 🟠 High |
| [ ] AndroidViewModel → ViewModel migration | 2 hari | 🟡 Medium |
| [ ] Hilt migration (lengkap) | 5 hari | 🟠 High |
| [ ] Marker clustering untuk osmdroid | 3 hari | 🟡 Medium |
| [ ] Ad placement optimization (native + interstitial) | 2 hari | 💰 Revenue |
| [ ] Ad mediation (AdMob + Meta) | 1 hari | 💰 Revenue |
| [ ] Create Workshop Owner B2B feature | 2 minggu | 💰 Revenue |
| [ ] Premium subscription (IAP + BillingManager) | 1 minggu | 💰 Revenue |
| [ ] Baseline Profiles | 2 hari | ⚡ Performance |
| [ ] Emergency mode feature | 3 hari | 🚀 Growth |

### Long-Term (6-12 bulan)

| Task | Prioritas |
|---|---|
| [ ] Multi-module architecture | 📐 Scale |
| [ ] Paging 3 integration | 📐 Scale |
| [ ] Dynamic feature delivery | 📐 Scale |
| [ ] Room migration (jika diperlukan) | 📐 Scale |
| [ ] Real-time workshop availability | 🚀 Feature |
| [ ] AI-powered route planning (cari sepanjang rute) | 🚀 Feature |
| [ ] Community forum / tips | 🚀 Feature |
| [ ] Fleet management for tire shops | 💰 Revenue |
| [ ] Expansion to SE Asia markets | 🌍 Growth |
| [ ] iOS / PWA version | 🌍 Growth |

---

## Task Tracker

### Dikerjakan Sekarang
- [ ]

### Quick Wins (1-3 hari)
- [ ] Fix search radius bug (`* 10000` → `* 1000`)
- [ ] Fix address/phone null display
- [ ] Remove dead code (LiveStatusDrawer + .bak)
- [ ] Remove wildcard imports
- [ ] Extract formatPhoneNumber ke utils
- [ ] Fix splash routing (aktifkan auth check)
- [ ] Enable ProGuard + test obfuscation
- [ ] Fix DB migration (ALTER TABLE)
- [ ] Tambah Coil image cache config
- [ ] Tambah debounce map pan events
- [ ] Implementasi status logic (parsing opening_hours)

### Short-Term (1-4 minggu)
- [ ] Hilt setup + migrasi feature map
- [ ] Unit test semua ViewModel
- [ ] Onboarding carousel
- [ ] LiveData → StateFlow (feature map)
- [ ] Marker clustering
- [ ] Ad placement di WorkshopDetail

### Mid-Term (1-3 bulan)
- [ ] StateFlow migration (auth + workshop)
- [ ] Hilt migration (full)
- [ ] AndroidViewModel → ViewModel
- [ ] Premium + IAP
- [ ] Workshop Owner B2B
- [ ] Baseline Profiles

### Done ✅
- (none yet)

---

## Key Metrics to Track

| Metric | Target | Current (estimated) |
|---|---|---|
| Crash-free rate | >99.5% | ~98% |
| Time to find workshop | <10 seconds | ~15s |
| Call-through rate | >30% | Unknown |
| Navigation start rate | >20% | Unknown |
| Workshop submission completion | >50% | Unknown |
| Ad RPM | >$5 | ~$0-2 |
| DAU/MAU ratio | >25% | Unknown |
| Review submission rate | >5% | Unknown |
| App size (release) | <15MB | ~30MB+ |
| Cold start time | <2s | ~3-4s |

---

## Catatan Tambahan

### Yang SUDAH BAIK (pertahankan)
- MVVM + Repository pattern ✓
- Material Design 3 ✓
- osmdroid + OpenStreetMap (tepat — tidak perlu Google Maps) ✓
- Shimmer loading states ✓
- Loading/Empty/Error state management ✓
- Manual DI sederhana (baik untuk MVP awal) ✓
- EncryptedSharedPreferences (baik untuk token storage) ✓
- Coil (image loading ringan) ✓
- Firebase Crashlytics + Analytics ✓
- In-app update mechanism ✓
- Indonesian language support ✓

### Yang Perlu Dipertahankan (jangan ganti)
- **XML + ViewBinding** — jangan migrasi ke Compose untuk aplikasi ini
- **Supabase** — backend sudah sesuai
- **kotlinx-serialization** — jangan ganti ke Gson/Moshi
- **Retrofit** — sudah tepat
- **osmdroid** — jangan ganti ke Google Maps

### Prinsip Pengembangan
1. Setiap PR harus: lint pass + compile pass + no `!!` + strings in XML
2. Test sebelum merge — minimal unit test untuk ViewModel baru
3. Satu refactor per PR — jangan campur refactor dengan feature
4. Dokumentasi di `CLAUDE.md` untuk konvensi baru
5. Review security untuk setiap perubahan networking
6. **Update CHANGELOG.md setiap ada perubahan** — jangan lupa!
7. **Commit message: Conventional Commits** — `feat:`, `fix:`, `refactor:`, `chore:`, `docs:`
8. **Satu commit = satu logical change** — jangan campur

---

## Project Health & Process

### Changelog Convention

**Wajib update `CHANGELOG.md` setiap kali:**
- Menambah fitur baru → `### Added`
- Mengubah behavior existing → `### Changed`
- Memperbaiki bug → `### Fixed`
- Menghapus fitur/code → `### Removed`
- Deprecate API → `### Deprecated`
- Security fix → `### Security`

**Format (Keep a Changelog):**
```markdown
## [Unreleased]

### Added
- Fitur baru A (link ke spec jika ada)

### Changed
- Behavior B berubah karena alasan C

### Fixed
- Bug X di location Y (closes #ISSUE)
```

### Commit Message Convention

```
type(scope): deskripsi singkat (max 50 chars)

[optional body: why, not what]

[optional footer: BREAKING CHANGE, Closes #123, Ref #456]
```

| Type | When |
|---|---|
| `feat` | Fitur baru |
| `fix` | Bug fix |
| `refactor` | Refactor tanpa perubahan behavior |
| `perf` | Performance improvement |
| `test` | Tambah/ubah test |
| `docs` | Dokumentasi |
| `chore` | Build, CI, dependencies, tooling |
| `style` | Formatting, import, whitespace (no code change) |

**Contoh:**
```
feat(workshop): add image upload to workshop submission
fix(map): correct search radius from 30km to 3km
refactor(auth): extract formatPhoneNumber to core/utils
chore(deps): bump AGP 8.2.2 → 8.3.0
```

### Branch Naming Convention

| Pattern | Example |
|---|---|
| `feature/XXX-deskripsi` | `feature/018-add-workshop` |
| `fix/deskripsi-singkat` | `fix/search-radius-10x` |
| `refactor/deskripsi` | `refactor/hilt-di-migration` |
| `chore/deskripsi` | `chore/update-dependencies` |

**Rules:**
- Gunakan `kebab-case`
- Feature branch dari `dev`
- Fix/refactor branch dari `dev`
- Jangan commit langsung ke `main`

### Pull Request Convention

```markdown
## Description
Apa yang diubah dan why.

## Related Issues
Closes #ISSUE_NUMBER

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Refactor
- [ ] Performance
- [ ] Documentation

## Testing
- [ ] Unit tests added/passed
- [ ] Manual testing done
- [ ] Compile check passed

## Screenshots (if UI change)
[attach]

## Checklist
- [ ] Lint pass
- [ ] No `!!`
- [ ] Strings in XML
- [ ] CHANGELOG updated
- [ ] No cross-feature imports
```

### Release Process

```
1. Branch: `release/vX.Y.Z` dari `dev`
2. Update versionName + versionCode di `app/build.gradle.kts`
3. Update CHANGELOG: pindah [Unreleased] → [X.Y.Z] — YYYY-MM-DD
4. Test full regression: ./gradlew test + connected check
5. Build release: ./gradlew assembleRelease
6. Deploy to Play Store internal track
7. Smoke test dari internal track
8. Promote to production
9. Merge release branch ke main
10. Tag: git tag vX.Y.Z && git push --tags
```

### Spec Lifecycle

Setiap fitur di `specs/XXX-feature/`:
```
specs/
├── 018-add-workshop/
│   ├── spec.md       ← requirements (fase design)
│   ├── plan.md       ← technical design (fase planning)
│   ├── tasks.md      ← implementation tasks (fase execution)
│   ├── checklists/   ← QA checklists (fase testing)
│   └── archive/      ← moved here when done (fase cleanup)
```

**Lifecycle:**
1. `OPEN` — spec dibuat, belum dikerjakan
2. `PLANNING` — plan + tasks dibuat
3. `IN PROGRESS` — sedang diimplementasi
4. `REVIEW` — PR dibuat, menunggu review
5. `DONE` — merged, spec diarsipkan ke `archive/`

### Technical Debt Register

Buat file `TECH_DEBT.md` di root untuk tracking technical debt:

```markdown
# Technical Debt Register

| ID | Description | Impact | Effort | Added | Status |
|---|---|---|---|---|---|
| TD-001 | LiveData → StateFlow migration | Testability, leaks | 4 days | 2026-05-20 | OPEN |
| TD-002 | Manual DI → Hilt | Scalability blocker | 5 days | 2026-05-20 | OPEN |
| TD-003 | SQLiteOpenHelper → Room | Schema evolution | 3 days | 2026-05-20 | DEFERRED |
```

**Rules:**
- Setiap refactor task = Tech Debt item
- Review setiap bulan → decide: pay off now or defer
- Critical security debt: pay immediately
- Architecture debt: pay within 1-2 sprints

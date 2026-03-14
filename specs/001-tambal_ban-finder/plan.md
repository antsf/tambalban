# Implementation Plan: Tambal Ban Finder (Lightweight)

**Branch**: `001-tambal_ban-finder` | **Date**: 2026-03-13 | **Spec**: [spec.md](spec.md)
**Input**: Feature specification from `/specs/001-tambal_ban-finder/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/plan-template.md` for the execution workflow.

## Summary

The Tambal Ban Finder will be refactored to prioritize a minimal footprint for low-spec devices (2GB RAM). The technical approach shifts from high-level abstractions (`Room`, `Retrofit`) to lower-level, built-in Android components (`SQLiteOpenHelper`, `HttpURLConnection`). This reduction in dependencies will significantly decrease the APK size and memory overhead. MVVM architecture persists, but data models and repositories will use manual JSON parsing and SQL management.

## Technical Context

**Language/Version**: Kotlin 1.9+, Android SDK 35+
**Primary Dependencies**: osmdroid 6.x (Map), OSMBonusPack (Clustering), AdMob SDK (Monetization), ViewModel, LiveData
**Built-in Replacements**:
- **Networking**: `HttpURLConnection` & `ThreadPoolExecutor` (Replacing Retrofit/OkHttp)
- **Database**: `SQLiteOpenHelper` (Replacing Room)
- **JSON Parsing**: `org.json` (Replacing Gson)
**Storage**: SQLite manual schema management (Local), Supabase PostGIS (Remote)
**Testing**: JUnit 4 (Unit), Espresso (UI)
**Target Platform**: Android 7.0 (API 24) and above
**Performance Goals**: < 3MB APK code size (excluding assets), < 60MB RAM usage during map peak, 60 FPS clustering
**Constraints**: 2GB RAM device optimization, offline-capable, ZERO redundant external dependencies
**Scale/Scope**: Support 10,000+ workshop markers; 5-10 core UI screens

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- **Principle 1: Minimal Footprint**: Avoid external libraries for functionality provided by the Android Framework (JSON, HTTP, SQLite). (Status: PASS)
- **Principle 2: Low-Memory Optimization**: Use manual resource management and avoid heavy reflection-based libraries. (Status: PASS)
- **Principle 3: Repository Pattern**: Abstraction layer remains mandatory for local/remote sync, even with manual implementations. (Status: PASS)
- **Principle 4: Reactive UI**: Use `LiveData` or simple callbacks to keep UI responsive and updated from background threads. (Status: PASS)

## Project Structure

### Documentation (this feature)

```text
specs/001-tambal_ban-finder/
├── plan.md              # This file
├── research.md          # Updated for the lightweight stack
├── data-model.md        # Updated for SQLite schema
├── quickstart.md        # Updated for setup requirements
├── contracts/           # API contract definitions
└── tasks.md             # To be updated by /speckit.tasks
```

### Source Code (repository root)

```text
app/
├── src/
│   ├── main/
│   │   ├── java/com/tambal_ban/
│   │   │   ├── data/
│   │   │   │   ├── api/          # HttpURLConnection implementation, JSON parsers
│   │   │   │   ├── database/     # SQLiteOpenHelper, Manual SQL queries
│   │   │   │   └── repository/   # Repository logic
│   │   │   ├── ui/
│   │   │   │   ├── main/         # Map View & ViewModel
│   │   │   │   ├── detail/       # Detail View
│   │   │   │   └── common/       # UI utils
│   │   │   └── utils/            # GeoUtils, NetworkUtils
│   │   └── res/
│   │       ├── layout/           # Lightweight XML layouts
│   │       └── values/           # Theme & strings
└── build.gradle
```

**Structure Decision**: A streamlined feature-based UI structure. The data layer is now "raw", relying on manual parsing and SQL to avoid the overhead of annotation processing and reflection.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| Manual SQLite | Drastic size reduction | Room adds ~1MB+ and significant annotation processing time |
| Manual HTTP | Eliminates OkHttp overhead | Retrofit/OkHttp add ~2MB to binary size |
| Manual JSON | Standard Android feature | Gson/Moshi add size and use reflection which is slower on low-end CPUs |

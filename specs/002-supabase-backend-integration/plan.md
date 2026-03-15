# Implementation Plan: Supabase Backend Integration

**Branch**: `002-supabase-backend-integration` | **Date**: 2026-03-15 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `specs/002-supabase-backend-integration/spec.md`

## Summary

This feature involves migrating the "Tambal Ban Finder" Android application's backend from its current state to a Supabase-powered infrastructure. This includes implementing email/password authentication using Supabase Auth, integrating with the Supabase REST API for workshop markers, reviews, and submissions, and establishing a robust repository-based data layer. The map system will be powered by osmdroid, fetching data dynamically based on visibility.

## Technical Context

**Language/Version**: Kotlin 1.9.x
**Primary Dependencies**: Retrofit 2.9.0, OkHttp 4.12.0, osmdroid 6.1.18, Supabase REST API (JWT)
**Storage**: Supabase (PostgreSQL)
**Testing**: JUnit 4, AndroidX Test, MockK
**Target Platform**: Android (Min SDK 24, Target SDK 34)
**Project Type**: Mobile Application
**Performance Goals**: <2s marker loading, responsive map with 10k+ potential markers
**Constraints**: No Firebase, No Google Maps SDK, offline-capable via Room caching (optional enhancement mentioned in SPEC.md)
**Scale/Scope**: National scale (Indonesia), community-driven data

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] **Principle I: Simplicity First** - Using the official Supabase REST API avoids complex custom backend services.
- [x] **Principle II: MVVM Enforcement** - Design strictly separates UI (Activities) from logic (ViewModels) and data (Repositories).
- [x] **Principle III: API-Driven Development** - All data sourced dynamically from Supabase; no hardcoded workshops.
- [x] **Principle V: Secure Authentication** - Supabase Auth implementation with JWT tokens stored in EncryptedSharedPreferences.
- [x] **Constraint: Connectivity** - Retrofit interceptors will handle auth headers and error states.

## Project Structure

### Documentation (this feature)

```text
specs/002-supabase-backend-integration/
├── plan.md              # This file
├── research.md          # Technology decisions and API contracts
├── data-model.md        # Kotlin data models (Workshop, Review, etc.)
├── quickstart.md        # Integration guide for Supabase Auth
├── contracts/           # API interface definitions (SupabaseService.kt)
└── tasks.md             # Implementation tasks
```

### Source Code (repository root)

```text
android/app/src/main/java/com/tambalban/
├── data/
│   ├── api/             # Retrofit interfaces & Interceptors
│   ├── model/           # Data models (DTOs)
│   └── repository/      # Repository implementations
├── ui/
│   ├── map/             # Map screen & ViewModels
│   ├── detail/          # Detail screen & ViewModels
│   ├── auth/            # Auth screens & ViewModels
│   └── add/             # Submission screen & ViewModels
└── utils/               # Secure storage & Config
```

**Structure Decision**: Option 3 (Mobile + API) adaptation for Android. The `android/` directory contains the full mobile app structure, with clear separation for data, UI, and utilities as governed by the constitution.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| N/A | | |

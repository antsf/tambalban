# Implementation Plan: Refresh Login Screen Design

**Branch**: `010-refresh-login-screen` | **Date**: 2026-04-12 | **Spec**: [specs/010-refresh-login-screen/spec.md](spec.md)
**Input**: Feature specification from `/specs/010-refresh-login-screen/spec.md`

## Summary

The goal is to implement a modern, high-fidelity login screen for the Tambal Ban application. The screen will feature a clean layout with rounded UI components, a subtle gradient background, and custom branding icons. This iteration focuses on core authentication and registration loops, with social logins and legal footer links explicitly removed from the immediate scope to prioritize simplicity.

## Technical Context

**Language/Version**: Kotlin 1.9+  
**Primary Dependencies**: Android SDK, Material Components (Material 3)  
**Storage**: EncryptedSharedPreferences (via `AuthPrefs.kt`)  
**Testing**: Espresso for functional UI testing  
**Target Platform**: Android (Min SDK 24)
**Project Type**: Mobile App Screen Redesign  
**Performance Goals**: Instant UI rendering, responsive keyboard handling.  
**Constraints**: Visual styling must match the provided reference image (pill-shaped fields, specific purple accents) while adhering to the Simplicity First constitution principle.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- **Simplicity First**: Footer links (TOS/Privacy/Help) have been removed from the spec to reduce initial complexity and focus on the primary user journey.
- **MVVM Architecture**: All state management and input validation (email/password checks) must remain in the `LoginViewModel`.
- **API-Driven Development**: Authentication remains connected to the Supabase backend via `AuthRepository`.
- **Secure Authentication**: No changes to the underlying security model; tokens remain in encrypted storage.

## Project Structure

### Documentation (this feature)

```text
specs/010-refresh-login-screen/
├── plan.md              # This file
├── research.md          # UI Component and Styling decisions
├── data-model.md        # Authentication entities and state
├── quickstart.md        # Setup and verification guide
└── spec.md              # Feature specification
```

### Source Code

```text
app/src/main/res/
├── layout/
│   └── activity_login.xml   # ConstraintLayout rewrite
├── drawable/
│   ├── bg_login_gradient.xml # Theme background
│   └── ic_brand_icon.xml    # Custom branding
└── values/
    ├── colors.xml           # Theme palette updates
    └── styles.xml           # M3 Component overrides
```

**Structure Decision**: We will perform a complete rewrite of `activity_login.xml` using `ConstraintLayout` to achieve the precision required by the new design. Logic changes are confined to UI binding in `LoginActivity.kt`.

## Complexity Tracking

*No constitution violations identified.*

# Implementation Plan: Update Login and Register Features

**Branch**: `008-auth-improvements` | **Date**: 2026-03-18 | **Spec**: [specs/008-auth-improvements/spec.md](spec.md)
**Input**: Feature specification from `/specs/008-auth-improvements/spec.md`

## Summary

This feature updates the existing login and registration flows in the Tambal Ban Finder app to significantly improve UX. It adds inline validation, password strength indicators (including full complexity rules), enhanced error handling with Snackbars, loading states, and a forgot password flow, while rigidly adhering to Android Material Design 3 and the existing MVVM + Supabase architecture.

## Technical Context

**Language/Version**: Kotlin 1.9+, Android SDK (Min SDK 24)
**Primary Dependencies**: Material Design 3 Components, Supabase Auth (Android SDK), ViewModels, LiveData/StateFlow
**Testing**: JUnit, MockK, Espresso (for UI components)
**Target Platform**: Android Native
**Project Type**: Mobile Application
**Performance Goals**: UI rendering at 60fps, negligible lag (<16ms) for inline validation while typing, instant API call state updates.
**Constraints**: Must fail gracefully offline, no direct DB/Network calls in UI layer, strictly MVVM, no use of Firebase/Google Maps.
**Scale/Scope**: Affects 3 screens (Login, Register, Forgot Password), updating existing AuthRepository and ViewModels.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- **Simplicity First**: Passed. Utilizing standard Material Design components (TextInputLayout) rather than custom views for inline errors and password toggles.
- **MVVM Architecture Enforcement**: Passed. All validation logic will be placed in ViewModels (`LoginViewModel`, `RegisterViewModel`), which will expose StateFlow/LiveData to update the UI. Activities will only observe and react.
- **API-Driven Development**: Passed. Registration/Login API calls remain within `AuthRepository` interacting with Supabase.
- **Offline Safety**: Passed. Adding explicit offline checks and offline-friendly error Snackbars during the Auth process.
- **Secure Authentication**: Passed. Maintaining Supabase Auth and adding password strength requirements (min 8 chars, uppercase, lowercase, number).
- **No forbidden tech used** (No Firebase, No Google Maps).

## Project Structure

### Documentation (this feature)

```text
specs/008-auth-improvements/
├── plan.md              # This file
├── research.md          # Output of Phase 0
├── data-model.md        # Output of Phase 1
├── quickstart.md        # Output of Phase 1
└── tasks.md             # (Created later by /speckit.tasks)
```

### Source Code (repository root)

```text
app/src/main/java/com/example/tambalban/
├── data/
│   └── repository/
│       └── AuthRepository.kt (Update error parsing)
├── ui/
│   └── auth/
│       ├── LoginActivity.kt (Update UI with Material 3, loading states, Snackbars)
│       ├── RegisterActivity.kt (Update UI with inline validation, terms checkbox, matching fields)
│       └── ForgotPasswordActivity.kt (New activity for forgot password flow)
└── viewmodel/
    ├── LoginViewModel.kt (Add inline validation logic, Flow states)
    └── RegisterViewModel.kt (Add password complexity logic, validation flows)

app/src/main/res/
├── layout/
│   ├── activity_login.xml (Update to TextInputLayouts with errorEnabled)
│   ├── activity_register.xml (Update to TextInputLayouts with errorEnabled, add status indicator)
│   └── activity_forgot_password.xml (New layout)
└── values/
    └── strings.xml (Add new error strings and validation messages)
```

**Structure Decision**: The feature is integrated directly into the `ui/auth/` and `viewmodel/` packages as defined by the Constitution's strict "Code Organization Rules".

## Complexity Tracking

No violations of the Constitution. Standard MVVM additions.

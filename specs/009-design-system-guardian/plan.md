# Implementation Plan: Design System: The Responsive Guardian

**Branch**: `009-design-system-guardian` | **Date**: 2026-04-11 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/009-design-system-guardian/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/plan-template.md` for the execution workflow.

## Summary

This feature implements "The Responsive Guardian" design system, moving from traditional utility-centric maps to a "Soft-Editorial Minimalism." The technical approach involves defining a set of Material 3 semantic tokens in Kotlin/XML, implementing tonal layering for structural integrity (eliminating borders), and building a custom component library with 56dp touch targets and glassmorphism effects.

## Technical Context

**Language/Version**: Kotlin 1.9+, Android SDK (Min SDK 24)
**Primary Dependencies**: Retrofit 2, OkHttp 4, osmdroid (Map System), Material Components for Android (Material 3)
**Storage**: N/A (UI-focused feature)
**Testing**: JUnit (logic), Espresso (UI verification for 56dp targets)
**Target Platform**: Android
**Project Type**: mobile-app
**Performance Goals**: 60 fps for drawer animations, <100ms response for touch interactions
**Constraints**: No 1px borders, offline-capable loading/error states
**Scale/Scope**: System-wide design tokens and core component library (Buttons, Chips, Inputs, Drawers)

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- **I. Simplicity**: System uses standard Android XML and Material 3 tokens. No new complex frameworks. [PASS]
- **II. MVVM Architecture**: All design logic is kept in resources (`res/`) and ViewModels will handle state for complex components like the Live-Status drawer. [PASS]
- **III. API-Driven**: Design system includes UI for Supabase-driven data (markers, reviews). [PASS]
- **IV. Offline Safety**: System defines Loading/Error/Empty states as part of the "Responsive Guardian" editorial look. [PASS]
- **V. Performance**: Ambient shadows and glassmorphism are optimized for marker-heavy map views. [PASS]

## Project Structure

### Documentation (this feature)

```text
specs/009-design-system-guardian/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output
└── tasks.md             # Phase 2 output (future)
```

### Source Code (repository root)

```text
app/src/main/
├── java/com/tambalban/
│   ├── data/            # Repositories and models
│   ├── viewmodel/       # UI State management
│   ├── ui/              # Activities, Fragments
│   │   ├── components/  # New Custom Design System Components
│   │   ├── theme/       # Design System Tokens (Color/Type/Shape)
│   │   └── common/      # Shared layouts (Error/Loading/Empty)
│   └── utils/           # Shadow/Glassmorphism helpers
├── res/
│   ├── values/          # themes.xml, colors.xml, type.xml, dimens.xml
│   ├── font/            # Plus Jakarta Sans & Inter
│   └── drawable/        # Rounded backgrounds, Pill shapes
```

**Structure Decision**: Standard Android project structure with a dedicated `ui/components` and `ui/theme` package to centralize the Design System implementation as per MVVM guidelines.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| Glassmorphism | Required for map spatial awareness while viewing status | simple opaque backgrounds hide the user's location on the map |
| Editorial Typography | Required for the "Soft-Editorial" character | Default fonts feel "utility-heavy" and don't match the Concierge north star |

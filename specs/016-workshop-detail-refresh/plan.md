# Implementation Plan: Workshop Detail UI Refresh

**Branch**: `016-workshop-detail-refresh` | **Date**: 2026-05-01 | **Spec**: [spec.md](spec.md)
**Input**: Feature specification from `/specs/016-workshop-detail-refresh/spec.md`

## Summary

This feature involves a complete redesign of the `WorkshopDetailActivity` to match a premium "Editorial Minimalism" aesthetic. Key updates include a full-width header image with a status badge, a flattened content container, prominently displayed action buttons (Call/Navigate), and a structured details list with Indonesian localization.

## Technical Context

**Language/Version**: Kotlin 1.9+
**Primary Dependencies**: Material Components (M3), Coil, ViewModel, LiveData
**Storage**: Local SQLite (WorkshopDbHelper) for fallback
**Testing**: Manual UI testing and Intent verification
**Target Platform**: Android (Min SDK 24)
**Project Type**: Mobile App
**Performance Goals**: Image load < 2s, Smooth scrolling
**Constraints**: 56dp touch targets, Indonesian localization
**Scale/Scope**: UI/UX Refresh of a single activity

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

1. **Simplicity First**: Avoiding complex custom views; using standard Material3 components where possible.
2. **MVVM Architecture**: All logic (status calculation) remains in ViewModel.
3. **API-Driven**: Data comes from existing `WorkshopRepository`.
4. **Offline Safety**: Handling missing images and network failures with placeholders and user-friendly error states.
5. **Performance**: Optimized image loading using Coil.
6. **Design Consistency**: Standard 20dp icons and 56dp touch targets.

## Project Structure

### Documentation (this feature)

```text
specs/016-workshop-detail-refresh/
├── plan.md              # This file
├── research.md          # Visual components research
├── data-model.md        # UI State model
├── quickstart.md        # UI testing scenarios
└── tasks.md             # Execution steps (Phase 2)
```

### Source Code (repository root)

```text
app/src/main/res/layout/
└── activity_workshop_detail.xml  # Updated layout

app/src/main/java/com/tambal_ban/workshop/ui/
└── WorkshopDetailActivity.kt    # Updated activity logic

app/src/main/java/com/tambal_ban/workshop/viewmodel/
└── WorkshopDetailViewModel.kt  # Updated to support new UI states
```

## Phase 1: Design & Contracts

**Enforce Dev Order**: Supabase Table (Done) → Model (Done) → Repository (Done) → ViewModel → UI.

1. **UI State Model** (`data-model.md`):
   - Define `WorkshopDetailUIState` to encapsulate all displayed fields including status color and text.
2. **ViewModel Update**:
   - Update `WorkshopDetailViewModel` to process raw `Workshop` data into `WorkshopDetailUIState`.
   - Calculate "Open Now/Closed" status based on current system time.
3. **Layout Refresh** (`activity_workshop_detail.xml`):
   - Use `CoordinatorLayout` + `AppBarLayout` + `CollapsingToolbarLayout` for the header image.
   - Implement the "Badge" using a `TextView` with custom background.
   - Use `MaterialButton` for Call and Navigate.
   - Use `ConstraintLayout` for the details list for precise alignment.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| N/A       |            |                                     |

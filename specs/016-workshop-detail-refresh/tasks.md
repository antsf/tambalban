---
description: "Task list for Workshop Detail UI Refresh implementation"
---

# Tasks: Workshop Detail UI Refresh

**Input**: Design documents from `/specs/016-workshop-detail-refresh/`
**Prerequisites**: plan.md (required), spec.md (required), research.md, data-model.md

**Organization**: Tasks are grouped by user story to enable independent implementation and testing.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2)
- Include exact file paths in descriptions

## Phase 1: Setup (Shared Infrastructure) ✅

**Purpose**: Resource initialization and localization

- [x] T001 [P] Add Indonesian strings for "ALAMAT LENGKAP", "NOMOR TELEPON", "JAM OPERASIONAL", "BUKA SEKARANG", "TUTUP" in `app/src/main/res/values/strings.xml`
- [x] T002 [P] Ensure `bg_status_open.xml` and `ic_schedule.xml` are available in `app/src/main/res/drawable/`

---

## Phase 2: Foundational (Blocking Prerequisites) ✅

**Purpose**: Core data layer updates for UI State

- [x] T003 [P] Create `WorkshopDetailUIState` data class in `app/src/main/java/com/tambal_ban/workshop/data/WorkshopDetailUIState.kt`
- [x] T004 Update `WorkshopDetailViewModel.kt` to expose `LiveData<WorkshopDetailUIState>` and implement mapping logic from `Workshop` entity

**Checkpoint**: Foundation ready - UI implementation can now begin.

---

## Phase 3: User Story 1 - Visual Experience & Information Clarity (Priority: P1) 🎯 MVP ✅

**Goal**: Implement the premium header image, status badge, and localized information list.

**Independent Test**: Open any workshop and verify the header image, "BUKA SEKARANG/TUTUP" badge, and Indonesian labels are displayed correctly.

### Implementation for User Story 1

- [x] T005 Update `app/src/main/res/layout/activity_workshop_detail.xml` to use `CoordinatorLayout` with `CollapsingToolbarLayout` for the header image
- [x] T006 [P] Add status badge `TextView` overlay on the header image in `app/src/main/res/layout/activity_workshop_detail.xml`
- [x] T007 [P] Implement the structured information list (Address, Phone, Hours) with uppercase labels and icons in `app/src/main/res/layout/activity_workshop_detail.xml`
- [x] T008 Update `app/src/main/java/com/tambal_ban/workshop/ui/WorkshopDetailActivity.kt` to observe `WorkshopDetailUIState` and bind it to the new layout elements

**Checkpoint**: User Story 1 is functional. Header and details are localized and premium.

---

## Phase 4: User Story 2 - Immediate Action (Priority: P2) ✅

**Goal**: Implement the side-by-side Call and Navigate buttons with icons.

**Independent Test**: Tap "Telepon" and "Navigasi" buttons to ensure they trigger the correct system intents.

### Implementation for User Story 2

- [x] T009 Update `app/src/main/res/layout/activity_workshop_detail.xml` to include prominent side-by-side buttons for "Telepon" and "Navigasi"
- [x] T010 [P] Implement click listeners in `app/src/main/java/com/tambal_ban/workshop/ui/WorkshopDetailActivity.kt` for phone and navigation intents

**Checkpoint**: All primary user actions are functional.

---

## Phase 5: Polish & Cross-Cutting Concerns ✅

**Purpose**: Final verification and UX refinement

- [x] T011 [P] Verify all labels are in Indonesian across the entire screen
- [x] T012 Ensure all interactive elements meet the 56dp touch target requirement
- [x] T013 Verify image placeholder behavior for workshops without an `image_url`

---

## Dependencies & Execution Order

### Phase Dependencies

1. **Setup (Phase 1)**: Can start immediately.
2. **Foundational (Phase 2)**: Depends on Phase 1 resources.
3. **User Story 1 (Phase 3)**: Depends on Foundational phase completion.
4. **User Story 2 (Phase 4)**: Can run in parallel with US1 UI layout work, but depends on Activity binding.
5. **Polish (Phase 5)**: Depends on all user stories being complete.

### Parallel Opportunities

- T001 and T002 can be done together.
- T006 and T007 (Layout work) can be done together.
- T010 (Activity logic) can be worked on while layout is being refined.

# Tasks: Refresh Home Screen Design

**Input**: Design documents from `/specs/011-refresh-home-screen/`
**Prerequisites**: [plan.md](file:///Users/antasofa/Devs/Java%20&%20Kotlin/Project/tambalban/specs/011-refresh-home-screen/plan.md), [spec.md](file:///Users/antasofa/Devs/Java%20&%20Kotlin/Project/tambalban/specs/011-refresh-home-screen/spec.md), [research.md](file:///Users/antasofa/Devs/Java%20&%20Kotlin/Project/tambalban/specs/011-refresh-home-screen/research.md), [data-model.md](file:///Users/antasofa/Devs/Java%20&%20Kotlin/Project/tambalban/specs/011-refresh-home-screen/data-model.md)

**Tests**: Tests are not explicitly requested. Implementation focuses on UI/UX fidelity.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (US1, US2, US3)
- Include exact file paths in descriptions

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and design alignment

- [x] T001 [P] Add mint-teal and orchid color tokens in `app/src/main/res/values/colors.xml`
- [x] T002 [P] Create `MapStyle` and `Home.Theme` in `app/src/main/res/values/styles.xml`
- [x] T003 [P] Initialize osmdroid configuration in `app/src/main/java/com/tambal_ban/ui/home/HomeActivity.kt`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Data layer and state management

- [x] T004 Implement radius-based workshop query in `app/src/main/java/com/tambal_ban/data/api/WorkshopApiService.kt`
- [x] T005 Update `app/src/main/java/com/tambal_ban/data/repository/WorkshopRepository.kt` with `getNearbyWorkshops` logic
- [x] T006 Implement `app/src/main/java/com/tambal_ban/ui/home/HomeViewModel.kt` to observe workshop data

**Checkpoint**: Foundation ready - UI implementation can now begin

---

## Phase 3: User Story 1 - Modernized Map Navigation (Priority: P1) 🎯 MVP

**Goal**: Styled map and interactive markers

**Independent Test**: Opne Home screen and verify map teal styling and labeled markers

### Implementation for User Story 1

- [x] T007 [US1] Define `activity_home.xml` with full-screen `MapView`
- [x] T008 [P] [US1] Implement teal `ColorMatrixColorFilter` utility in `app/src/main/java/com/tambal_ban/utils/MapUtils.kt`
- [x] T009 [US1] Implement custom `Marker` with label bubble in `app/src/main/java/com/tambal_ban/ui/home/HomeActivity.kt`

**Checkpoint**: Map navigation functional with new aesthetics

---

## Phase 4: User Story 3 - Nearby Workshops Bottom Sheet (Priority: P1)

**Goal**: Scrollable workshop list with status indicators

**Independent Test**: Pull up bottom sheet and verify list content shows 3 items in peek mode

### Implementation for User Story 3

- [x] T010 [P] [US3] Create `item_workshop_nearby.xml` card layout (rounded image, status badge)
- [x] T011 [P] [US3] Implement `app/src/main/java/com/tambal_ban/ui/home/NearbyWorkshopAdapter.kt`
- [x] T012 [US3] Configure `BottomSheetBehavior` with peek height in `app/src/main/java/com/tambal_ban/ui/home/HomeActivity.kt`
- [x] T013 [US3] Bind `HomeViewModel` workshops to the Bottom Sheet in `HomeActivity.kt`

**Checkpoint**: Nearby workshops can be browsed in the sheet

---

## Phase 5: User Story 2 - Floating Global Search (Priority: P2)

**Goal**: Pill-shaped search overlay with profile avatar

**Independent Test**: Tap search bar and verify transition to search entry state

### Implementation for User Story 2

- [x] T014 [P] [US2] Create `view_search_overlay.xml` layout for pill-shaped bar and avatar
- [x] T015 [US2] add `view_search_overlay.xml` as an overlay in `activity_home.xml`
- [x] T016 [US2] Implement search logic and avatar click handling in `HomeActivity.kt`

**Checkpoint**: Global search accessibility confirmed

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Interaction refinement

- [x] T017 Implement marker click-to-sheet focus in `HomeActivity.kt`
- [x] T018 Add smooth map pan animations in `app/src/main/java/com/tambal_ban/utils/MapUtils.kt`
- [x] T019 Run [quickstart.md](file:///Users/antasofa/Devs/Java%20&%20Kotlin/Project/tambalban/specs/011-refresh-home-screen/quickstart.md) validation

---

## Phase 7: User Story 4 - Dynamic Search Suggestions (Priority: P1)

**Goal**: Type-to-suggest overlay under the search bar

- [ ] T020 [US4] Add `ilike` search method to `SupabaseService.kt` and `WorkshopRepository.kt`
- [ ] T021 [US4] Implement debounced search pipeline in `MainViewModel.kt` (Flow + debounce)
- [ ] T022 [US4] Add `RecyclerView` for suggestions in `view_search_overlay.xml`
- [ ] T023 [US4] Create `SearchSuggestionAdapter.kt` and bind to `MainActivity.kt`
- [ ] T024 [US4] Implement "Snap to Marker" logic on suggestion click in `MainActivity.kt`

## Dependencies & Execution Order

- **Phase 1-2**: Mandatory setup. (Completed)
- **Phase 3-6**: Core UI and refined aesthetics. (Completed)
- **Phase 7**: Dynamic search enhancements. (Next)

# Tasks: Design System: The Responsive Guardian

**Input**: Design documents from `/specs/009-design-system-guardian/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

- Paths: `app/src/main/res/`, `app/src/main/java/com/tambalban/`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [x] T001 [P] Download and add "Plus Jakarta Sans" font files to `app/src/main/res/font/`
- [x] T002 [P] Download and add "Inter" font files to `app/src/main/res/font/`
- [x] T003 [DEPRECATED] (Removed external BlurView dependency for Simplicity First)
- [x] T004 Create `app/src/main/res/values/dimens.xml` with token values (`radius_xl`, `touch_target_min`)

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [x] T005 [P] Implement semantic color tokens (including orchid #973497 and on_surface #191c1d) in `app/src/main/res/values/colors.xml`
- [x] T006 [P] Implement typography scale (styles) in `app/src/main/res/values/type.xml`
- [x] T007 [P] Create custom shadow drawable for ambient elevation in `app/src/main/res/drawable/bg_ambient_shadow.xml`
- [x] T008 [P] Define `Guardian.Theme` base with Material 3 semantic roles in `app/src/main/res/values/themes.xml`
- [x] T009 Implement "No-Line" rule base styles (remove default dividers) in `app/src/main/res/values/styles.xml`

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Find Emergency Repair (Priority: P1) 🎯 MVP

**Goal**: Implement the core "Digital Concierge" look with Orchid beacons and 56dp targets.

**Independent Test**: Verify that the main map floating elements use Orchid #973497 and all buttons are 56dp tall with no 1px borders.

### Implementation for User Story 1
- [x] T010 [P] [US1] Create `Guardian.Button.Primary` style with `xl` (48dp) radius in `app/src/main/res/values/styles.xml`
- [x] T011 [US1] Implement Orchid gradient background for primary "SOS" button in `app/src/main/res/drawable/bg_primary_gradient_pill.xml`
- [x] T012 [P] [US1] Apply `display-sm` and `headline-lg` styles to map status text in `app/src/main/res/layout/layout_map_status.xml`
- [x] T013 [US1] **Flat Glass**: Floating action buttons (FABs) and map overlays utilize a semi-transparent effect (`surface_container_lowest` at 85% opacity) and high elevation (`8dp+`) in `app/src/main/res/layout/activity_map.xml`

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently.

---

## Phase 4: User Story 2 - Select a Repair Shop (Priority: P2)

**Goal**: Implement the "Soft-Editorial" shop list using tonal layering and safety-green chips. **Glassmorphism Strategy**: Utilize semi-transparent backgrounds (`#D9FFFFFF`) and high elevation to achieve a "Flat Glass" effect. This replaces the `BlurView` library to prioritize build stability and minimize external dependencies (Simplicity First).

**Independent Test**: Verify the list of shops has no dividers and uses background color shifts (`surfaceContainerLow`) for separation.

### Implementation for User Story 2

- [x] T014 [US2] Create list item layout `item_repair_shop.xml` using `surface_container_low` background and md (24dp) vertical spacing.
- [x] T015 [P] [US2] Implement "Emergency Chips" style using `tertiary_container` (Green) in `app/src/main/res/values/styles_chips.xml`
- [x] T016 [US2] Apply `display-sm` to distance text and `title-lg` to shop name in `app/src/main/res/layout/item_repair_shop.xml`
- [x] T017 [US2] Ensure all list interactive targets meet 56dp requirement in `app/src/main/res/layout/item_repair_shop.xml`

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently.

---

## Phase 5: User Story 3 - Track Live Progress (Priority: P3)

**Goal**: Implement the Live-Status drawer with Glassmorphism and 2rem corner radius.

**Independent Test**: Verify the bottom drawer slides over the map with a semi-transparent background and 85% white opacity.

### Implementation for User Story 3

- [x] T018 [US3] Create `Guardian.BottomSheet` style with top-corner radius `lg` (32dp) in `app/src/main/res/values/themes.xml`
- [x] T019 [US3] Implement `LiveStatusDrawer` fragment using "Flat Glass" (semi-transparent background) in `app/src/main/java/com/tambal_ban/ui/components/LiveStatusDrawer.kt`
- [x] T020 [P] [US3] Define glassmorphism/flat-glass overlay color (#D9FFFFFF) in `app/src/main/res/values/colors.xml`
- [x] T021 [US3] Integrate `plus_jakarta_sans` `headline-lg` for status updates in `app/src/main/res/layout/fragment_live_status.xml`

**Checkpoint**: All user stories should now be independently functional.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Final visual refinements and "Gloss Border" accessibility fallback.

- [x] T022 Implement "Ghost Border" fallback (15% opacity `outline_variant`) for accessibility in `app/src/main/res/drawable/bg_ghost_border.xml`
- [x] T023 [P] Create Guardian-styled shared layouts for offline states (`layout_loading.xml`, `layout_error.xml`, `layout_empty.xml`) in `app/src/main/res/layout/`
- [x] T024 [P] Apply `primary_fixed_dim` to all disabled states across the app in `app/src/main/res/values/themes.xml`
- [x] T025 Perform contrast accessibility audit (WCAG 2.1 AA 4.5:1 text, 3:1 graphical) for all tonal transitions.
- [x] T026 Final validation against `quickstart.md` scenarios.
arios.

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately.
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories.
- **User Stories (Phase 3+)**: All depend on Foundational phase completion.
  - US1, US2, and US3 can proceed in parallel once Phase 2 is complete.

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL)
3. Complete Phase 3: User Story 1 (The Orchid Beacon)
4. **STOP and VALIDATE**: Test User Story 1 independently on the map.

### Parallel Opportunities

- T001 and T002 (Font downloads) can run together.
- T005, T006, T007 (Token implementation) can run together.
- Once Foundation (T009) is done, US1 (T010-T013) and US2 (T014-T017) can be implemented by different developers as they touch different layout/style files.

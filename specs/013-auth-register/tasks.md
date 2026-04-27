# Tasks: User Registration

**Input**: Design documents from `/specs/013-auth-register/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: Automated tests are requested in quickstart.md.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [X] T001 [P] Ensure Supabase dependencies are present in `app/build.gradle.kts`
- [X] T002 [P] Configure Registration navigation in `app/src/main/java/com/tambal_ban/ui/auth/LoginActivity.kt` (add Register link)

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

- [X] T003 [P] Update `AuthRepository.kt` with a registration contract in `app/src/main/java/com/tambal_ban/data/repository/AuthRepository.kt`
- [X] T004 [P] Create `AuthErrorMapper` utility in `app/src/main/java/com/tambal_ban/utils/AuthErrorMapper.kt`

**Checkpoint**: Foundation ready - user story implementation can now begin

---

## Phase 3: User Story 1 - Create Account with Email and Password (Priority: P1) 🎯 MVP

**Goal**: Allow users to create a new account and log in automatically.

**Independent Test**: Successfully register a new user and see redirection to the Map screen.

### Tests for User Story 1

- [X] T005 [P] [US1] Create unit test for registration logic in `app/src/test/java/com/tambal_ban/viewmodel/auth/RegisterViewModelTest.kt`

### Implementation for User Story 1

- [X] T006 [P] [US1] Ensure `User` data model handles new fields in `app/src/main/java/com/tambal_ban/data/model/User.kt`
- [X] T007 [US1] Implement `signUp` method in `app/src/main/java/com/tambal_ban/data/repository/AuthRepository.kt`
- [X] T008 [US1] Create `RegisterViewModel` with registration state in `app/src/main/java/com/tambal_ban/viewmodel/auth/RegisterViewModel.kt`
- [X] T009 [P] [US1] Create `activity_register.xml` using `Login.TextInputLayout` style in `app/src/main/res/layout/activity_register.xml`
- [X] T010 [US1] Create `RegisterActivity` and wire with `RegisterViewModel` in `app/src/main/java/com/tambal_ban/ui/auth/RegisterActivity.kt`
- [X] T011 [US1] Handle successful registration redirection in `app/src/main/java/com/tambal_ban/ui/auth/RegisterActivity.kt`

**Checkpoint**: User Story 1 (MVP) is functional.

---

## Phase 4: User Story 2 - Password Visibility Toggle (Priority: P2)

**Goal**: Allow users to toggle password visibility.

**Independent Test**: Click toggle icon and verify password masking changes.

### Implementation for User Story 2

- [X] T012 [P] [US2] Update `activity_register.xml` to use `ic_visible` and `ic_invisible` (20dp) in `app/src/main/res/layout/activity_register.xml`
- [X] T013 [US2] Update `TambalTextField` or `RegisterActivity` to handle toggle logic in `app/src/main/java/com/tambal_ban/ui/auth/RegisterActivity.kt`

---

## Phase 5: User Story 3 - Field Validation (Priority: P1)

**Goal**: Provide real-time validation feedback.

**Independent Test**: Enter invalid data and verify immediate error messages.

### Implementation for User Story 3

- [X] T014 [US3] Implement validation logic in `app/src/main/java/com/tambal_ban/viewmodel/auth/RegisterViewModel.kt`
- [X] T015 [US3] Connect validation state to UI errors in `app/src/main/java/com/tambal_ban/ui/auth/RegisterActivity.kt`

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [X] T016 [P] Add string resources for errors and labels in `app/src/main/res/values/strings.xml`
- [X] T017 Implement loading state (Shimmer or Progress) in `app/src/main/res/layout/activity_register.xml`
- [X] T018 Run `quickstart.md` validation to ensure full flow compliance

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: Can start immediately.
- **Foundational (Phase 2)**: Depends on Setup - BLOCKS user stories.
- **User Stories (Phase 3+)**: Depend on Foundational phase. US1 is priority.

### Parallel Opportunities

- T001, T002 (Setup)
- T003, T004 (Foundational)
- T005, T006, T009 (US1 Implementation)
- US2 and US3 can be worked on after US1 core is stable.

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Setup + Foundational.
2. Implement core registration flow (US1).
3. Validate with manual test.

### Incremental Delivery

1. Foundation -> MVP (US1) -> UX (US2) -> Robustness (US3).

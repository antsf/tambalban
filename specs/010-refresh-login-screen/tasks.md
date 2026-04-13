# Tasks: Refresh Login Screen Design

**Input**: Design documents from `specs/010-refresh-login-screen/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Design tokens and asset initialization

- [ ] T001 [P] Define purple theme colors (Surface, Primary, Accent) in `app/src/main/res/values/colors.xml`
- [ ] T002 [P] Create custom gradient drawable `app/src/main/res/drawable/bg_login_gradient.xml`
- [ ] T003 [P] Update M3 component styles (pill-shaped text fields) in `app/src/main/res/values/styles.xml`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core UI structure updates

- [ ] T004 Implement root ConstraintLayout and center card container (MaterialCardView) in `app/src/main/res/layout/activity_login.xml`

**Checkpoint**: Base layout structure ready - UI components can now be added.

---

## Phase 3: User Story 1 - Authenticate with Refresh Design (Priority: P1) 🎯 MVP

**Goal**: Implement the primary login form and branding elements.

**Independent Test**: Launch login screen, verify brand icon/titles, fill fields, and trigger login flow with inline error validation.

### Implementation for User Story 1

- [ ] T005 [P] [US1] Create wrench branding icon `app/src/main/res/drawable/ic_brand_icon.xml`
- [ ] T006 [US1] Implement branding logo (wrench in purple circle) and text titles in `activity_login.xml`
- [ ] T007 [US1] Add rounded Email and Password input fields with Material Design 3 icons in `activity_login.xml`
- [ ] T008 [US1] Implement purple pill-shaped Login button with arrow trailing icon in `activity_login.xml`
- [ ] T009 [US1] Remove previous Google/Apple social login buttons and associated logic in `activity_login.xml`
- [ ] T010 [US1] Update `com/tambal_ban/ui/auth/LoginActivity.kt` to bind to new layout and handle inline `setError` feedback
- [ ] T011 [US1] Update `com/tambal_ban/viewmodel/LoginViewModel.kt` to drive the new loading and localized error states

**Checkpoint**: Primary login flow is functional with the new high-fidelity design.

---

## Phase 4: User Story 2 - Account Registration Access (Priority: P2)

**Goal**: Provide clear access to account registration.

**Independent Test**: Tap the "Register" link, verify navigation to `RegisterActivity`.

### Implementation for User Story 2

- [ ] T012 [US2] Add registration prompt ("Don't have an account? Register") with modern typography in `activity_login.xml`
- [ ] T013 [US2] Update click handling in `LoginActivity.kt` for navigation to registration

---

## Phase 5: User Story 3 - Password Recovery Access (Priority: P2)

**Goal**: Provide password recovery link.

**Independent Test**: Tap "Forgot?", verify it triggers the password reset flow.

### Implementation for User Story 3

- [ ] T014 [US3] Implement the "Forgot?" link with specific purple accent styling in `activity_login.xml`
- [ ] T015 [US3] Update `LoginActivity.kt` to handle password recovery navigation

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Final visual cleanup and copyright implementation

- [ ] T016 [US4] Implement copyright and "Powered by" text at the bottom footer in `activity_login.xml`
- [ ] T017 Verify all icons have appropriate `contentDescription` for accessibility
- [ ] T018 Confirm soft keyboard "adjustResize" behavior is correct in `AndroidManifest.xml`
- [ ] T019 Run final validation against `quickstart.md` to ensure design fidelity

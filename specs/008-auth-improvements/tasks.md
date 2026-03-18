# Tasks: Update Login and Register Features

**Input**: Design documents from `/specs/008-auth-improvements/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, quickstart.md

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure updates

- [ ] T001 Update strings.xml with new error strings and validation messages in `app/src/main/res/values/strings.xml`
- [ ] T002 Update `AuthRepository.kt` to define and properly parse Supabase errors and offline scenarios in `app/src/main/java/com/example/tambalban/data/repository/AuthRepository.kt`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

- [ ] T003 Create state classes `FormValidationState`, `PasswordStrength`, `AuthState`, `ErrorType` in `app/src/main/java/com/example/tambalban/ui/auth/AuthStates.kt`

**Checkpoint**: Foundation ready - user story implementation can now begin

---

## Phase 3: User Story 1 - Secure and Streamlined Registration (Priority: P1) 🎯 MVP

**Goal**: Users need to be able to register for the Tambal Ban Finder app with clear validations so they know if their input is correct, including ensuring passwords are secure.

**Independent Test**: Can be fully tested by creating a new account through the registration form and verifying validations and immediate redirection, delivering a frictionless sign-up process.

### Implementation for User Story 1

- [ ] T004 [P] [US1] Update `activity_register.xml` to use Material 3 `TextInputLayout`s with errorEnabled, terms checkbox, and progress bar for password strength indicator in `app/src/main/res/layout/activity_register.xml`
- [ ] T005 [P] [US1] Implement `RegisterViewModel.kt` with password complexity logic, confirm password match, debounced input testing, and validation `StateFlow`s in `app/src/main/java/com/example/tambalban/viewmodel/RegisterViewModel.kt`
- [ ] T006 [US1] Update `RegisterActivity.kt` to observe `StateFlow`s, handle Snackbars, manage loading state, and validate 'Done' action in `app/src/main/java/com/example/tambalban/ui/auth/RegisterActivity.kt`

**Checkpoint**: At this point, User Story 1 (Registration redesign) should be fully functional and testable independently.

---

## Phase 4: User Story 2 - Smooth Login Experience (Priority: P1)

**Goal**: Users need to log in to the app seamlessly with clear feedback if they enter the wrong credentials or encounter network issues.

**Independent Test**: Can be fully tested by logging in with valid and invalid credentials, and in offline modes.

### Implementation for User Story 2

- [ ] T007 [P] [US2] Update `activity_login.xml` to use Material 3 `TextInputLayout`s with errorEnabled, and add loading spinner structure in `app/src/main/res/layout/activity_login.xml`
- [ ] T008 [P] [US2] Implement `LoginViewModel.kt` with inline validation logic and form `StateFlow`s in `app/src/main/java/com/example/tambalban/viewmodel/LoginViewModel.kt`
- [ ] T009 [US2] Update `LoginActivity.kt` to observe `StateFlow`s, display loading animations and Snackbars in `app/src/main/java/com/example/tambalban/ui/auth/LoginActivity.kt`

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently.

---

## Phase 5: User Story 3 - Forgot Password Flow (Priority: P2)

**Goal**: Users who forget their passwords need a clear path to recover their accounts from the Login screen.

**Independent Test**: Can be fully tested by navigating from the Login screen to the Forgot Password UI and initiating the basic flow.

### Implementation for User Story 3

- [ ] T010 [P] [US3] Create `activity_forgot_password.xml` layout for email input and submission in `app/src/main/res/layout/activity_forgot_password.xml`
- [ ] T011 [P] [US3] Create `ForgotPasswordActivity.kt` and implement UI logic for triggering Supabase password reset in `app/src/main/java/com/example/tambalban/ui/auth/ForgotPasswordActivity.kt`
- [ ] T012 [US3] Add "Forgot Password?" link interaction to `LoginActivity.kt` to launch `ForgotPasswordActivity` based on user interactions.

**Checkpoint**: All user stories should now be independently functional.

---

## Phase N: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T013 Verify backward compatibility constraints in `AuthRepository.kt`.
- [ ] T014 Run user manual flows to confirm offline safety responses correctly fire Snackbar notifications rather than fatal crashes.
- [ ] T015 Verify visual elements (spacing, colors, layouts) strictly adhere to Material 3 design philosophy as mandated.

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3-4-5)**: All depend on Foundational phase completion
  - US1 and US2 can proceed in parallel once Phase 2 is complete.
  - US3 should begin after US2 since it connects through the Login screen.
- **Polish (Final Phase)**: Depends on all user stories being complete.

### User Story Dependencies

- **User Story 1 (P1)**: Registration changes. Can start after Foundational (Phase 2).
- **User Story 2 (P1)**: Login changes. Can start after Foundational (Phase 2).
- **User Story 3 (P2)**: Forgot Password. Should follow User Story 2 to securely hook into the completed Login interface.

### Parallel Opportunities

- T004 and T005 can be executed simultaneously.
- T007 and T008 can be executed simultaneously.
- T010 and T011 can be executed simultaneously.

---

## Implementation Strategy

### MVP First (User Story 1 & 2)

1. Complete Phase 1 & 2: Setup & Foundation.
2. Complete Phase 3: Registration Redesign. Test independently.
3. Complete Phase 4: Login Redesign. Test independently.
4. Deploy/demo if ready (This creates the core MVP).
5. Address Phase 5: Forgot Password (P2 Priority) post-MVP.

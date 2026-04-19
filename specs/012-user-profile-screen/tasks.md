# Tasks: User Profile Screen

**Input**: Design documents from `/specs/012-user-profile-screen/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: Tests are NOT explicitly requested in the spec; focusing on functional implementation and manual validation via quickstart.md.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [x] T001 [P] Create directory structure for Profile feature in `app/src/main/java/com/tambal_ban/`
- [x] T002 Update `SupabaseService.kt` with profile REST and Storage endpoints in `app/src/main/java/com/tambal_ban/data/api/SupabaseService.kt`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

- [x] T003 [P] Create `Profile.kt` data model in `app/src/main/java/com/tambal_ban/data/model/Profile.kt`
- [x] T004 Create `ProfileRepository.kt` in `app/src/main/java/com/tambal_ban/data/repository/ProfileRepository.kt`
- [x] T005 Create `ProfileViewModel.kt` in `app/src/main/java/com/tambal_ban/viewmodel/ProfileViewModel.kt`

**Checkpoint**: Foundation ready - user story implementation can now begin.

---

## Phase 3: User Story 1 - Access Profile from Home (Priority: P1) 🎯 MVP

**Goal**: Navigate to Profile from Home screen search bar with authentication gate.

**Independent Test**: Tap profile icon in search bar; verify navigation to Profile if logged in, or Login if not.

### Implementation for User Story 1

- [x] T006 [US1] Implement profile icon click listener in `MainActivity.kt` and update `view_search_overlay.xml`
- [x] T007 [US1] Implement auth session check and Login redirect logic in `ProfileViewModel.kt`

---

## Phase 4: User Story 2 - View Personal Identity (Priority: P1)

**Goal**: Display user profile data in a flat, clean header integrated with the background.

**Independent Test**: Open Profile screen; verify name, email, phone, and avatar are displayed without a card container.

### Implementation for User Story 2

- [x] T008 [P] [US2] Create `activity_profile.xml` layout following Flat design rules in `app/src/main/res/layout/activity_profile.xml`
- [x] T009 [US2] Implement `ProfileActivity.kt` to bind user data from `ProfileViewModel`

---

## Phase 5: User Story 3 - Edit Profile Information (Priority: P2)

**Goal**: Update name, email, and phone number through a dedicated editing screen.

**Independent Test**: Change name in Edit screen, save, and verify update on Profile view.

### Implementation for User Story 3

- [x] T010 [P] [US3] Create `activity_edit_profile.xml` in `app/src/main/res/layout/activity_edit_profile.xml`
- [x] T011 [US3] Implement `EditProfileActivity.kt` with input validation (FR-012)
- [x] T012 [US3] Implement `updateProfile` persistence logic in `ProfileRepository.kt` and `ProfileViewModel.kt`

---

## Phase 6: User Story 4 - Change Profile Picture (Priority: P2)

**Goal**: Upload or change profile avatar using the device gallery and Supabase Storage.

**Independent Test**: Change avatar in Edit screen, save, and verify new image displays.

### Implementation for User Story 4

- [x] T013 [US4] Integrate `ActivityResultContracts.PickVisualMedia` in `EditProfileActivity.kt` for image selection
- [x] T014 [US4] Implement `uploadAvatar` binary upload logic in `ProfileRepository.kt` for Supabase Storage

---

## Phase 7: User Story 5 - Secure Logout (Priority: P3)

**Goal**: Clear user session and redirect to entry screen.

**Independent Test**: Tap Logout; verify redirection to Login and session invalidation.

### Implementation for User Story 5

- [x] T015 [US5] Implement `logout` action in `ProfileViewModel.kt` and trigger button in `ProfileActivity.kt`

---

## Phase 8: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [x] T016 [P] Apply "The Responsive Guardian" tonal shifts and 56dp touch targets to all new components
- [x] T017 [P] Run full validation using `quickstart.md` scenarios

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1) & Foundational (Phase 2)**: MUST be complete before any User Story.
- **User Story 1 (P1)**: Prerequisite for meaningful navigation to subsequent stories.
- **User Story 2 (P1)**: Core viewing functionality.
- **User Story 3 & 4 (P2)**: Independent editing features, depend on US2.
- **User Story 5 (P3)**: Logout functionality.

### Parallel Opportunities

- T001, T003 can be done in parallel.
- US2 and US3 layout creation (T008, T010) can be done in parallel.
- Polish tasks (T016, T017) can start as soon as US1 and US2 are stable.

---

## Implementation Strategy

### MVP First (User Story 1 & 2 Only)

1. Complete Setup & Foundational phases.
2. Implement US1 (Navigation & Auth Gate).
3. Implement US2 (View Profile).
4. **VALIDATE**: Ensure user can reach their profile and see their data.

### Incremental Delivery

1. Add US3 (Edit Info).
2. Add US4 (Avatar Change).
3. Add US5 (Logout).

# Tasks: Supabase Backend Integration

**Input**: Design documents from `specs/002-supabase-backend-integration/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Includes exact file paths in descriptions

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [x] T001 [P] Create com.tambal_ban.utils.Constants.kt with URL and ANON_KEY constants
- [x] T002 [P] Configure Android dependencies for Retrofit, Moshi, and Security-Crypto in app/build.gradle.kts
- [x] T003 [P] Create com.tambal_ban.utils.AuthPrefs.kt for EncryptedSharedPreferences management

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [x] T004 [P] Implement com.tambal_ban.data.api.AuthInterceptor.kt to handle apikey and Bearer tokens
- [x] T005 [P] Implement com.tambal_ban.data.api.SupabaseService.kt with Auth and REST endpoints
- [x] T006 Implement com.tambal_ban.data.api.NetworkModule.kt using providing Retrofit and SupabaseService (depends on T004, T005)

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - View Nearby Workshops (Priority: P1) 🎯 MVP

**Goal**: Display tire repair workshops on an OpenStreetMap using osmdroid and Supabase REST API.

**Independent Test**: Open map screen, verify markers appear, tap marker to see info window.

### Implementation for User Story 1

- [x] T007 [P] [US1] Create com.tambal_ban.data.model.Workshop.kt matching Supabase schema
- [x] T008 [P] [US1] Implement com.tambal_ban.data.repository.WorkshopRepository.kt for fetching nearby workshops
- [x] T009 [US1] Implement com.tambal_ban.ui.main.MainViewModel.kt to load and expose workshop markers
- [x] T010 [US1] Update com.tambal_ban.ui.main.MainActivity.kt to integrate with MainViewModel and display markers

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently.

---

## Phase 4: User Story 2 - User Authentication (Priority: P2)

**Goal**: Enable users to login using email and password via Supabase Auth.

**Independent Test**: Navigate to login screen, enter valid credentials, verify successful login and token storage in AuthPrefs.

### Implementation for User Story 2

- [x] T011 [P] [US2] Create com.tambal_ban.data.model.AuthModels.kt (LoginRequest, AuthResponse, User)
- [x] T012 [P] [US2] Implement com.tambal_ban.data.repository.AuthRepository.kt for login and session management
- [x] T013 [US2] Implement com.tambal_ban.ui.auth.LoginViewModel.kt to handle user authentication state
- [x] T014 [US2] Implement com.tambal_ban.ui.auth.LoginActivity.kt with email/password input and login logic

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently.

---

## Phase 5: User Story 3 - Submit New Workshop (Priority: P3)

**Goal**: Allow authenticated users to submit new workshop proposals.

**Independent Test**: Login, navigate to "Add Workshop", submit form, verify new record in `workshop_submissions` table via Supabase dashboard.

### Implementation for User Story 3

- [x] T015 [P] [US3] Create com.tambal_ban.data.model.WorkshopSubmission.kt
- [x] T016 [P] [US3] Implement com.tambal_ban.data.repository.SubmissionRepository.kt for workshop submission
- [x] T017 [US3] Implement com.tambal_ban.ui.add.AddWorkshopViewModel.kt to handle submission logic
- [x] T018 [US3] Implement com.tambal_ban.ui.add.AddWorkshopActivity.kt with submission form and location picker

**Checkpoint**: Authenticated submission flow should be fully functional.

---

## Phase 6: User Story 4 - Write Workshop Review (Priority: P3)

**Goal**: Allow authenticated users to rate and review workshops.

**Independent Test**: Login, open workshop detail, submit rating/comment, verify review appears in list.

### Implementation for User Story 4

- [x] T019 [P] [US4] Create com.tambal_ban.data.model.Review.kt
- [x] T020 [P] [US4] Implement com.tambal_ban.data.repository.ReviewRepository.kt for reviews API
- [x] T021 [US4] Implement com.tambal_ban.ui.detail.WorkshopDetailViewModel.kt for review submissions
- [x] T022 [US4] Update com.tambal_ban.ui.detail.WorkshopDetailActivity.kt to display review list and "Write Review" entry point

**Checkpoint**: All user stories should now be independently functional.

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [x] T023 [P] Update AndroidManifest.xml with Activities, permissions, and AdMob configuration
- [x] T024 [P] Refine AuthInterceptor to handle Auth endpoints correctly (skip Bearer for /auth/)
- [x] T025 Implement user_id storage in AuthPrefs and Repositories to satisfy Row-Level Security (RLS)
- [x] T026 Final validation of quickstart.md and migration of credentials to SupabaseConfig.kt

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3+)**: All depend on Foundational phase completion
  - US1 (Map) is the MVP and can be worked on once the foundation is ready.
  - US2 (Auth) is independent but required for US3 and US4.

### Parallel Opportunities

- T001, T002, T003 can be done in any order within Phase 1.
- T004 and T005 can be done in parallel within Phase 2.
- Once Phase 2 is complete, US1 and US2 implementation can start in parallel.
- US3 and US4 can be implemented in parallel once US2 (Auth foundation) is complete.

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1 (Map View)
4. **STOP and VALIDATE**: Verify public map features.

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Map features (MVP!)
3. Add User Story 2 → Authentication
4. Add User Story 3 & 4 → Community features (Submissions & Reviews)

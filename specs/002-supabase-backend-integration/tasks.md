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

- [ ] T001 [P] Create com.tambalban.utils.SupabaseConfig.kt with URL and ANON_KEY constants
- [ ] T002 [P] Configure Android dependencies for Retrofit, Moshi, and Security-Crypto in android/app/build.gradle
- [ ] T003 [P] Create com.tambalban.utils.AuthPrefs.kt for EncryptedSharedPreferences management

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T004 [P] Implement com.tambalban.data.api.AuthInterceptor.kt to handle apikey and Bearer tokens
- [ ] T005 [P] Implement com.tambalban.data.api.SupabaseService.kt with Auth and REST endpoints
- [ ] T006 Implement com.tambalban.data.api.NetworkModule.kt using providing Retrofit and SupabaseService (depends on T004, T005)

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - View Nearby Workshops (Priority: P1) 🎯 MVP

**Goal**: Display tire repair workshops on an OpenStreetMap using osmdroid and Supabase REST API.

**Independent Test**: Open map screen, verify markers appear, tap marker to see info window.

### Implementation for User Story 1

- [ ] T007 [P] [US1] Create com.tambalban.data.model.Workshop.kt matching Supabase schema
- [ ] T008 [P] [US1] Implement com.tambalban.data.repository.WorkshopRepository.kt for fetching nearby workshops
- [ ] T009 [US1] Implement com.tambalban.viewmodel.MapViewModel.kt to load and expose workshop markers
- [ ] T010 [US1] Update com.tambalban.ui.map.HomeMapActivity.kt to integrate with MapViewModel and display markers

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently.

---

## Phase 4: User Story 2 - User Authentication (Priority: P2)

**Goal**: Enable users to login using email and password via Supabase Auth.

**Independent Test**: Navigate to login screen, enter valid credentials, verify successful login and token storage in AuthPrefs.

### Implementation for User Story 2

- [ ] T011 [P] [US2] Create com.tambalban.data.model.AuthModels.kt (LoginRequest, AuthResponse, User)
- [ ] T012 [P] [US2] Implement com.tambalban.data.repository.AuthRepository.kt for login and session management
- [ ] T013 [US2] Implement com.tambalban.viewmodel.LoginViewModel.kt to handle user authentication state
- [ ] T014 [US2] Implement com.tambalban.ui.auth.LoginActivity.kt with email/password input and login logic

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently.

---

## Phase 5: User Story 3 - Submit New Workshop (Priority: P3)

**Goal**: Allow authenticated users to submit new workshop proposals.

**Independent Test**: Login, navigate to "Add Workshop", submit form, verify new record in `workshop_submissions` table via Supabase dashboard.

### Implementation for User Story 3

- [ ] T015 [P] [US3] Create com.tambalban.data.model.WorkshopSubmission.kt
- [ ] T016 [P] [US3] Implement com.tambalban.data.repository.SubmissionRepository.kt for workshop submission
- [ ] T017 [US3] Implement com.tambalban.viewmodel.AddWorkshopViewModel.kt to handle submission logic
- [ ] T018 [US3] Implement com.tambalban.ui.add.AddWorkshopActivity.kt with submission form and location picker

**Checkpoint**: Authenticated submission flow should be fully functional.

---

## Phase 6: User Story 4 - Write Workshop Review (Priority: P3)

**Goal**: Allow authenticated users to rate and review workshops.

**Independent Test**: Login, open workshop detail, submit rating/comment, verify review appears in list.

### Implementation for User Story 4

- [ ] T019 [P] [US4] Create com.tambalban.data.model.Review.kt
- [ ] T020 [P] [US4] Implement com.tambalban.data.repository.ReviewRepository.kt for reviews API
- [ ] T021 [US4] Implement com.tambalban.viewmodel.ReviewViewModel.kt for review submissions
- [ ] T022 [US4] Update com.tambalban.ui.detail.WorkshopDetailActivity.kt to display review list and "Write Review" entry point

**Checkpoint**: All user stories should now be independently functional.

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T023 [P] Update AndroidManifest.xml with all new Activities and necessary permissions
- [ ] T024 [P] Implement global error handling for network-wide interceptors in AuthInterceptor
- [ ] T025 Code cleanup and ensuring all Repositories follow the IPlaceRepository-like interface structure
- [ ] T026 Final validation of quickstart.md steps to ensures integration is seamless

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

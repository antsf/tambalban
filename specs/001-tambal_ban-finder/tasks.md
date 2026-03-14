# Tasks: TambalBan Finder (Native Implementation)

**Input**: Design documents from `/specs/001-tambal_ban-finder/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: Focus on implementation tasks that are independently testable via the UI.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

- **Android Project**: `app/src/main/java/com/tambal_ban//`, `app/src/main/res/`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization with minimal dependencies

- [x] T001 Clean up current app/build.gradle to remove Retrofit, Room, and Gson dependencies
- [x] T002 Configure minimal Gradle dependencies (osmdroid, AdMob, Lifecycle) in app/build.gradle
- [x] T003 [P] Ensure all vector assets are used instead of raster images in app/src/main/res/drawable/
- [x] T004 [P] Update AndroidManifest.xml for native location and network permissions in app/src/main/AndroidManifest.xml

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Native core infrastructure (SQL, HTTP, JSON)

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [x] T005 Implement WorkshopDbHelper using SQLiteOpenHelper in app/src/main/java/com/tambal_ban/data/database/
- [x] T006 [P] Create manual Cursor-to-Entity mapping functions in app/src/main/java/com/tambal_ban/data/database/mappers/
- [x] T007 Implement NetworkClient using HttpURLConnection in app/src/main/java/com/tambal_ban/data/api/
- [x] T008 [P] Implement manual JSON parsing for Workshop objects using org.json in app/src/main/java/com/tambal_ban/data/api/parsers/
- [x] T009 Implement WorkshopRepository with manual SQLite and NetworkClient sync in app/src/main/java/com/tambal_ban/data/repository/
- [x] T010 [P] Implement GeoUtils with Haversine formula in app/src/main/java/com/tambal_ban/utils/GeoUtils.kt
- [x] T011 [P] Setup AdMob helper with non-intrusive logic in app/src/main/java/com/tambal_ban/ads/

**Checkpoint**: Native foundation ready - skipping high-overhead libraries

---

## Phase 3: User Story 1 - Find Nearest Workshop (Priority: P1) 🎯 MVP

**Goal**: Load markers within viewport using native networking and SQL.

**Independent Test**: Pan the map and verify markers appear correctly on 2GB RAM devices.

### Implementation for User Story 1

- [x] T012 [P] [US1] Create optimized map layout with ViewBinding in app/src/main/res/layout/activity_main.xml
- [x] T013 [US1] Implement MainViewModel to trigger viewport-based fetching in app/src/main/java/com/tambal_ban/ui/main/
- [x] T014 [US1] Integrate osmdroid in MainActivity with memory-cache limits in app/src/main/java/com/tambal_ban/ui/main/
- [x] T015 [US1] Implement RadiusMarkerClusterer with custom low-res icons in app/src/main/java/com/tambal_ban/ui/main/
- [x] T016 [US1] Implement bounding box SQL query in WorkshopDbHelper.kt

**Checkpoint**: User Story 1 functional on low-spec hardware.

---

## Phase 4: User Story 2 - Emergency Help (Priority: P1)

**Goal**: Instant closest workshop search within 3km.

**Independent Test**: Tap Emergency Button and verify it completes in < 2 seconds.

### Implementation for User Story 2

- [x] T017 [P] [US2] Add optimized Floating Emergency Button to activity_main.xml
- [x] T018 [US2] Implement closest-workshop SQL query in WorkshopDbHelper.kt
- [x] T019 [P] [US2] Create lightweight Workshop Detail layout in app/src/main/res/layout/activity_workshop_detail.xml
- [x] T020 [US2] Implement WorkshopDetailActivity with manual data binding in app/src/main/java/com/tambal_ban/ui/detail/

---

## Phase 5: User Story 3 - Contact and Navigate (Priority: P1)

**Goal**: Standard system intents for calling and directions.

**Independent Test**: Verify dialer and Google/Waze launch correctly.

### Implementation for User Story 3

- [x] T021 [P] [US3] Implement IntentUtils for ACTION_DIAL and Geo Intents in app/src/main/java/com/tambal_ban/utils/
- [x] T022 [US3] Add Call and Navigate buttons to activity_workshop_detail.xml
- [x] T023 [US3] Wire buttons in WorkshopDetailActivity to IntentUtils.

---

## Phase 6: User Story 4 - Search with Radius Filter (Priority: P2)

**Goal**: Multi-radius filtering using local distance calculations.

### Implementation for User Story 4

- [x] T024 [P] [US4] Add radius filter UI in activity_main.xml
- [x] T025 [US4] Implement filtered SQL fetch in WorkshopDbHelper.kt

---

## Phase 7: User Story 5 - Contribute New Workshop (Priority: P3)

**Goal**: Submission form with manual JSON POST.

### Implementation for User Story 5

- [x] T026 [P] [US5] Create Add Workshop layout with minimal View count in app/src/main/res/layout/activity_add_workshop.xml
- [x] T027 [US5] Implement AddWorkshopActivity and ViewModel in app/src/main/java/com/tambal_ban/ui/add/
- [x] T028 [US5] Implement manual JSON POST for submission in NetworkClient.kt

---

## Phase 8: Polish & Low-Spec Optimization

**Purpose**: Finalize for 2GB RAM / Minimal Footprint

- [x] T029 [P] Implement global low-memory handling (onTrimMemory) in TambalBanApp.kt
- [x] T030 Optimize background thread priority to prevent UI lag on slow CPUs
- [x] T031 Integrate Banner Ads with delayed loading to prioritize map rendering
- [x] T032 Final APK size audit and ProGuard/R8 configuration tuning
- [x] T033 Run quickstart.md validation on low-spec emulator profile

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: Must be first to strip unnecessary libraries.
- **Foundational (Phase 2)**: Mandatory blocking phase.
- **User Stories (Phases 3-7)**: Multi-story implementation.
- **Polish (Phase 8)**: Final optimization.

### Parallel Opportunities

- T003 and T004 (Assets vs Config)
- T006 and T008 (DB Mappers vs JSON Parsers)
- T010 and T011 (Utils vs Ads)
- T019 and T021 (UI Detail vs Intents)

---

## Implementation Strategy

### Native First MVP

1. Strip project of heavy libraries (Retrofit, Room, Gson).
2. Build native wrappers for HTTP and SQL.
3. Deliver P1 User Stories (Find, Call, Emergency).
4. Verify memory usage stays below target.

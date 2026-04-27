# Tasks: Project Modularization

**Input**: Design documents from `/specs/014-modular-structure/`
**Prerequisites**: plan.md (required), spec.md (required), research.md

**Organization**: Tasks follow a phase-by-feature migration strategy to minimize build breakages.

## Format: `[ID] [P?] Description`

- **[P]**: Can run in parallel (different files/packages)

## Phase 1: Setup & Directory Structure

**Purpose**: Prepare the new package hierarchy

- [X] T001 Create `core` sub-packages in `app/src/main/java/com/tambal_ban/core/` (network, ui, utils, location, ads)
- [X] T002 Create feature packages in `app/src/main/java/com/tambal_ban/` (auth, workshop, map)

---

## Phase 2: Core Module Migration

**Purpose**: Move shared infrastructure elements

- [X] T003 [P] Move Network files (SupabaseService, Interceptors, NetworkModule) from `data/api/` to `core/network/`
- [X] T004 [P] Move Common UI components from `ui/components/` to `core/ui/`
- [X] T005 [P] Move Utils from `utils/` to `core/utils/`
- [X] T006 [P] Move Location and Ads services from `location/` and `ads/` to `core/location/` and `core/ads/`
- [X] T007 [P] Move `AuthPrefs`, `Constants`, `SupabaseConfig` to `core/utils/` or `core/data/` (as per plan)

---

## Phase 3: Auth Feature Migration

**Purpose**: Consolidate everything related to user identity

- [X] T008 [P] Move Auth Activities and ViewModels from `ui/auth/` and `viewmodel/` to `auth/`
- [X] T009 [P] Move Auth-specific Repositories and Models (AuthRepository, ProfileRepository, AuthModels, Profile) to `auth/`

---

## Phase 4: Workshop Feature Migration

**Purpose**: Consolidate workshop lifecycle and interaction logic

- [X] T010 [P] Move Workshop Activities (Add, Detail) and ViewModels from `ui/add/`, `ui/detail/` to `workshop/`
- [X] T011 [P] Move Workshop Adapters (ReviewAdapter) to `workshop/`
- [X] T012 [P] Move Workshop Data Layer (Repositories, Models, Database, Mappers) to `workshop/`

---

## Phase 5: Map Feature Migration

**Purpose**: Finalize the discovery and main UI module

- [X] T013 [P] Move `MainActivity` and `MainViewModel` to `map/`
- [X] T014 [P] Move Map-related Adapters (NearbyWorkshopAdapter, SearchSuggestionAdapter) to `map/`

---

## Phase 7: Internal Feature Organization 

**Purpose**: Group files within features into ui, viewmodel, and data folders 

- [X] T021 Create ui, viewmodel, data folders in auth/, workshop/, and map/ 
- [X] T022 Move Auth files into their sub-folders 
- [X] T023 Move Workshop files into their sub-folders 
- [X] T024 Move Map files into their sub-folders 
- [X] T025 Repeat Phase 6 (Manifest, Imports, Build) for the new sub-folders 

--- 
## Phase 6: Integration & Cleanup

**Purpose**: Fix broken references and validate build

- [X] T015 Update `AndroidManifest.xml` with new Activity package paths
- [X] T016 Run global search & replace for package imports in all `.kt` files
- [X] T017 Update custom view references in XML layouts (e.g. `com.tambal_ban.ui.components.TambalButton` -> `com.tambal_ban.core.ui.TambalButton`)
- [X] T018 Fix Data Binding / View Binding references if necessary
- [X] T019 [P] Remove empty old directories (`ui/`, `data/`, `viewmodel/`, etc.)
- [X] T020 Run `./gradlew assembleDebug` to verify project stability

---

## Dependencies & Execution Order

1. **Phase 1** must be first to provide target paths.
2. **Phase 2-5** can run mostly in parallel, but it is safer to do one at a time.
3. **Phase 6** MUST be done last to resolve the compilation errors introduced by moves.

## Implementation Strategy

### Incremental Move
1. Move files using `git mv` to keep history.
2. Expect the project to NOT compile during Phase 2-5.
3. Do NOT attempt to fix individual imports until all moves are done (Phase 6).

### Validation
- Build success is the primary success metric.
- No change in functionality is expected.

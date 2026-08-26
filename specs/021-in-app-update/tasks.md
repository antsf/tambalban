# Tasks: In-App Update

## Phase 1: Dependency
- [x] T001 [P1] Add play-app-update-ktx dependency to app/build.gradle.kts

## Phase 2: Core Utility
- [x] T002 [P1] Create InAppUpdateManager utility in core/ that wraps AppUpdateManager, checks availability, starts flexible flow, and handles InstallStateUpdatedListener

## Phase 3: Preference Tracking
- [x] T003 [P1] Add lastPromptTimestamp tracking to avoid prompting too often (store in SharedPreferences via AuthPrefs or dedicated prefs)

## Phase 4: Integration
- [x] T004 [P1] Wire InAppUpdateManager.checkForUpdate() in MainActivity.onResume() after existing logic

## Phase 5: Polish
- [x] T005 [P2] Handle non-Play-Store environments (emulator, sideload) — catch PlayCoreException gracefully
- [x] T006 [P2] Update CHANGELOG.md
- [x] T007 [P1] Build verification: ./gradlew assembleDebug

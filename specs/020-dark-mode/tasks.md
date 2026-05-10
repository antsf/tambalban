# Tasks: Dark Mode Toggle

## Phase 1: Theme Resources
- [ ] T001 [P1] Create values-night/colors.xml with dark palette (app/src/main/res/values-night/colors.xml)
- [ ] T002 [P1] Create values-night/themes.xml with Dark.NoActionBar parent (app/src/main/res/values-night/themes.xml)

## Phase 2: Preference Storage
- [ ] T003 [P1] Add theme preference save/load to AuthPrefs (core/utils/AuthPrefs.kt)

## Phase 3: Apply Theme on Start
- [ ] T004 [P1] Apply saved theme in BaseActivity before super.onCreate (core/ui/BaseActivity.kt)

## Phase 4: Profile Toggle
- [ ] T005 [P1] Add "Tema Gelap" toggle row to activity_profile.xml (res/layout/activity_profile.xml)
- [ ] T006 [P1] Wire toggle in ProfileActivity with immediate apply (auth/ui/ProfileActivity.kt)

## Phase 5: Polish
- [ ] T007 [P2] Update CHANGELOG.md
- [ ] T008 [P1] Build verification: ./gradlew assembleDebug

# Tasks: Firebase Analytics & Crashlytics

## Phase 1: Dependency & Setup
- [ ] T001 [P1] Add Firebase BoM + analytics + crashlytics dependencies to app/build.gradle.kts (core/network/...)
- [ ] T002 [P1] Place google-services.json in app/ directory (manual step — provide to developer)
- [ ] T003 [P1] Add firebase-crashlytics gradle plugin to root and app build.gradle.kts

## Phase 2: Core Analytics Utility
- [ ] T004 [P1] Create AnalyticsHelper utility that wraps FirebaseAnalytics with screen_view and event logging methods (core/utils/AnalyticsHelper.kt)
- [ ] T005 [P1] Create CrashlyticsHelper utility that wraps FirebaseCrashlytics with user ID management and non-fatal logging (core/utils/CrashlyticsHelper.kt)

## Phase 3: App Initialization
- [ ] T006 [P1] Initialize FirebaseApp, AnalyticsHelper, CrashlyticsHelper in TambalBanApp.onCreate() (TambalBanApp.kt)
- [ ] T007 [P1] Wire login/logout to set/clear Crashlytics user ID (authRepository/authPrefs integration)

## Phase 4: Screen Tracking
- [ ] T008 [P1] Add screen_view event to BaseActivity.onCreate() so all activities auto-log (core/ui/BaseActivity.kt)
- [ ] T009 [P2] Add screen_view event to SplashActivity, WorkshopDetailActivity, WorkshopListActivity, AddWorkshopActivity, LoginActivity, RegisterActivity, ProfileActivity, EditProfileActivity

## Phase 5: Event Tracking
- [ ] T010 [P2] Log login, register, logout events where they occur
- [ ] T011 [P2] Log workshop_submit event in AddWorkshopViewModel/Activity
- [ ] T012 [P2] Log review_submit event in WorkshopDetailActivity
- [ ] T013 [P2] Log call_workshop and navigate_to_workshop events
- [ ] T014 [P2] Log search event in MainActivity/WorkshopListActivity
- [ ] T015 [P2] Log dark_mode_toggle and share_app events

## Phase 6: Crashlytics Non-Fatal Logging
- [ ] T016 [P2] Log network errors as non-fatal exceptions in WorkshopRepository
- [ ] T017 [P2] Log network errors as non-fatal exceptions in AuthRepository, ProfileRepository, ReviewRepository

## Phase 7: Polish
- [ ] T018 [P2] Update CHANGELOG.md
- [ ] T019 [P1] Build verification: ./gradlew assembleDebug

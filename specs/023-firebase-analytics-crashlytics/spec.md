# Firebase Analytics & Crashlytics

## Overview
Integrate Firebase Analytics for tracking user behavior across all screens and key actions, and Firebase Crashlytics for crash reporting. This provides visibility into real user usage patterns and enables data-driven feature decisions. No new UI — all integration is in the data/analytics layer and wired into existing activities.

## User Stories
- [P1] As a developer, I can see daily active users and screen usage in Firebase console
- [P1] As a developer, I receive crash reports with user context (user ID, app version)
- [P2] As a developer, I can track key business events (workshop submit, review, call, navigate)
- [P2] As a developer, I can log non-fatal network errors to Crashlytics

## Functional Requirements
- FR-001: Firebase Analytics initialized on app start in TambalBanApp.onCreate()
- FR-002: Firebase Crashlytics initialized on app start
- FR-003: Screen view events logged automatically per activity (splash, map, detail, list, add, login, register, profile, edit profile)
- FR-004: Key user events logged: login, register, logout, workshop_submit, review_submit, call_workshop, navigate_to_workshop, search, dark_mode_toggle, share_app
- FR-005: Crashlytics user ID set when user logs in, cleared on logout
- FR-006: Network errors in repositories logged as non-fatal Crashlytics events
- FR-007: Crashlytics collects crashes automatically without custom code

## Assumptions
- google-services.json will be provided by developer (not checked into repo)
- Uses Firebase BoM for version management (compatible with project's compileSdk 35)
- No google-services plugin — using manual initialization via FirebaseApp.initializeApp(this)

## Out of Scope
- Custom event parameter schemas — use standard Firebase event names with minimal params
- A/B testing or Remote Config
- Firebase Cloud Messaging (push notifications)
- Performance Monitoring

# In-App Update

## Overview
Prompt users to update the app when a newer version is available on Google Play Store using Play Core's `AppUpdateManager`. Flexible update flow downloads in background and installs on user confirmation. Check runs on MainActivity start and is rate-limited to avoid annoyance.

## User Stories
- [P1] As a user, I am prompted to update when a new version is available on Play Store
- [P1] As a user, I can accept or decline a flexible update
- [P2] As a user, the update downloads in the background without blocking my usage
- [P2] As a user, I am not prompted more than once per session

## Functional Requirements
- FR-001: Check for update availability on MainActivity.onResume() using AppUpdateManager
- FR-002: Use FLEXIBLE flow (background download, install on confirmation)
- FR-003: Rate-limit prompt to once per app session (SharedPreferences flag)
- FR-004: Handle Play Store unavailability (sideload/emulator) gracefully — no crash, no prompt
- FR-005: Listen for download state changes and show install confirmation when download completes
- FR-006: Minimum update window delay of 3 days between prompts

## Assumptions
- Uses `com.google.android.play:app-update-ktx` (not the deprecated `play-core` split)
- Stale flag stored in SharedPreferences via AuthPrefs or dedicated prefs

## Out of Scope
- Immediate update mode (reserved for critical security updates, requires server-side flag)
- Custom update UI — uses Play Core's native dialog
- Forcing updates — user can always decline

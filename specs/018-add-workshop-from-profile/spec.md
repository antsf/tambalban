# Add Tambal Ban from Profile — Simple Form Dialog

## Overview

Add quick-access form on Profile screen to submit new workshop without navigating to map. Form displays as bottom sheet dialog with simple fields (no map picker). Simplified version of AddWorkshopActivity for profile context.

## User Stories

- [P2] As logged-in user on Profile screen, I can tap "Add Tambal Ban" button to open quick-submit form
- [P2] As user, I can enter workshop name, address, city, phone, optional province/opening_hours/photo and submit directly from profile
- [P2] As user, after submit I see success Snackbar and form closes

## Functional Requirements

- FR-001: Profile screen has "Add Tambal Ban" button at top or bottom
- FR-002: Tapping button opens BottomSheetDialog with form
- FR-003: Form fields: name (req), address (req), city (req), phone (req), province (opt), opening_hours (opt), photo (opt)
- FR-004: No location picker — user enters lat/lon manually OR form uses default coords (center map) OR hidden field
- FR-004b: For MVP, set fixed lat/lon (e.g., -6.2, 106.8 Jakarta center) or require user input
- FR-005: Submit button validates required fields, calls WorkshopRepository.addWorkshop()
- FR-006: On success: show Snackbar "Terkirim, sedang ditinjau admin", close dialog, refresh profile
- FR-007: On error: show error message, keep dialog open

## Out of Scope

- Location picker (map) — use fixed coords or manual entry
- "My Submissions" history
- Photo cropping/compression UI (use existing picker)

## Assumptions

- Reuse existing WorkshopSubmission/Workshop models
- Reuse existing photo picker launcher from AddWorkshopActivity pattern
- Use BottomSheetDialog or AlertDialog (simplest)
- Set default location to app DEFAULT_LATITUDE/DEFAULT_LONGITUDE (Jakarta center)

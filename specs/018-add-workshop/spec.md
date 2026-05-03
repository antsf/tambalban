# Add Workshop from Edit Profile

## Overview

Add "Tambah Tambal Ban" menu item to Edit Profile screen. Tapping opens AddWorkshopActivity with form to submit new workshop. Reuses WorkshopRepository logic; includes current location button to fetch device location. All labels in Indonesian.

## Clarifications

### Session 2026-05-04

- Q: Location behavior — hardcoded vs. user device location? → A: lat/lon fields show Jakarta default; current location button (icon) fetches device location on tap; fields editable manually

## User Stories

- [P2] As logged-in user on Edit Profile screen, I see "Tambah Tambal Ban" menu item
- [P2] Tapping "Tambah Tambal Ban" opens full activity screen with form fields
- [P2] As user, I fill name, address, city, phone, optional province/opening_hours/photo, then submit
- [P2] After submit: Snackbar "Terkirim, sedang ditinjau admin", back to Edit Profile

## Functional Requirements

- FR-001: Edit Profile screen shows "Tambah Tambal Ban" menu item (top menu or bottom button)
- FR-002: Tapping opens AddWorkshopActivity with title "Tambah Tambal Ban" and back button
- FR-003: Form fields: nama (required), alamat (required), kota (required), telepon (required), provinsi (optional), jam buka (optional), foto (optional) — all Indonesian labels
- FR-004: lat/lon fields display default Jakarta center (-6.2, 106.8); "current location" button (icon) fetches device location and populates fields; user can edit fields manually
- FR-005: Submit button validates required fields, calls WorkshopRepository.addWorkshop()
- FR-006: On success: Snackbar "Terkirim, sedang ditinjau admin" then finish() back to Edit Profile
- FR-007: On error: show error toast, keep form visible for retry

## UI Details

- Screen title: "Tambah Tambal Ban"
- Back button (up navigation) returns to Edit Profile
- lat/lon fields: editable TextInputLayouts, display default Jakarta center; "current location" icon button next to fields (taps to fetch device location)
- All field labels & hints in Indonesian
- Submit button: "Kirim" or "Tambah"
- Cancel: back button

## Out of Scope

- Interactive map picker — location via button or manual field edit only
- "My Submissions" history
- Photo compression UI
- Multiple workshops per session

## Assumptions

- Reuse existing WorkshopRepository.addWorkshop() 
- Reuse existing photo picker from AddWorkshopActivity pattern (GetContent)
- Reuse existing activity_add_workshop.xml layout (update context to auth package)
- Default location: app Constants.DEFAULT_LATITUDE/DEFAULT_LONGITUDE
- Returns to Edit Profile on success via finish()

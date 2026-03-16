# Feature Specification: TambalBan Finder Major Update

**Feature Branch**: `005-v2-major-refactor`
**Created**: 2026-03-16
**Status**: Draft
**Input**: Comprehensive specification update for Tambal Ban Finder Android App.

## Project Context
A mobile application to help users find nearby tire repair workshops ("tambal ban") in Indonesia, specifically for emergency roadside situations.

**Technology Stack**:
- Android (Kotlin)
- XML Layout
- MVVM Architecture
- Backend: Supabase (Database, Auth, Storage)
- Map: osmdroid / Map integration
- Image Loading: Glide
- Ads: Google AdMob

---

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Find Nearest Workshop (P1)
As a driver with a flat tire, I want to see workshops on a map within my visible area so I can find help quickly.

**Acceptance Scenarios**:
1. **Given** the app start, **When** user location is retrieved, **Then** map centers to user location.
2. **Given** user moves the map, **When** map stops, **Then** workshops within visible bounds are loaded and displayed as markers.
3. **Given** a marker is tapped, **Then** the Workshop Detail page opens.

### User Story 2 - Workshop List (P2)
As a user, I want to see all workshops in a list format sorted by distance so I can compare options easily.

**Acceptance Scenarios**:
1. **Given** I open "Semua Tambal Ban" from the drawer, **Then** I see a paginated list of workshops.
2. **Given** the list, **When** location is available, **Then** items are sorted by distance.
3. **Given** the list, **When** I tap the "Add Workshop" FAB, **Then** the submission form opens.

### User Story 3 - Add & Edit Workshop (P2)
As an authenticated user, I want to submit or edit workshop information to keep the data accurate.

**Acceptance Scenarios**:
1. **Given** I am logged in, **When** I submit a workshop, **Then** status is set to "pending" and it is hidden from the map until admin approval.
2. **Given** I edit my workshop, **Then** status returns to "pending" and it becomes hidden until re-approved.
3. **Given** I add photos, **When** I upload up to 3 photos, **Then** they are compressed before storage.

---

## Requirements *(mandatory)*

### Functional Requirements

#### Theme & UI
- **FR-UI-001**: Primary theme color MUST be `#DA70D6` (AppBar, buttons, active icons, highlights, progress indicators).
- **FR-UI-002**: UI MUST be clean, modern, and readable with rounded input fields and modern card layouts.
- **FR-UI-003**: All labels and UI text MUST use Bahasa Indonesia.

#### Home Screen (Simplified)
- **FR-HOME-001**: Layout MUST consist of AppBar, Search Field ("Cari Tambal Ban"), and Map View.
- **FR-HOME-002**: All extra buttons and the "Emergency Button" MUST be removed.

#### Map Behavior
- **FR-MAP-001**: Map MUST center on user location upon start.
- **FR-MAP-002**: Map MUST load workshops using bounding box query (visible bounds).
- **FR-MAP-003**: Markers MUST display workshop name and rating.

#### Navigation Drawer
- **FR-NAV-001**: Navigation Drawer MUST be accessible from the top-left menu.
- **FR-NAV-002**: Items MUST include: Home Map, List Semua Tambal Ban, User Profile.
- **FR-NAV-003**: Guest users see: Login, Register.

#### Workshop List Page ("Semua Tambal Ban")
- **FR-LIST-001**: Display paginated/lazy-loaded list of workshops.
- **FR-LIST-002**: Sort by distance if location is available.
- **FR-LIST-003**: Each item MUST show: photo, name, address, distance, rating.
- **FR-LIST-004**: Floating Action Button (FAB) for "Add Workshop".

#### Workshop Detail Page
- **FR-DETAIL-001**: Layout MUST include: Photo Gallery (max 3 images, swipeable carousel), Name, Address, Phone, Rating, Buttons (Panggil/Call, Buka Navigasi), and Reviews Section.

#### Photo System & Compression
- **FR-PHOTO-001**: Support max 3 photos per workshop stored in Supabase Storage (`workshop-images/{workshop_id}/{photo_id}.jpg`).
- **FR-PHOTO-002**: Photos MUST be compressed before upload: max width 1280px, JPEG format, 70% quality (target size 200kb-500kb).
- **FR-PHOTO-003**: Use `default_workshop_image.png` if no photos exist.

#### User Profile & Submissions
- **FR-USER-001**: Profile MUST show User Information and a list of workshops submitted by the user.
- **FR-USER-002**: Workshop status MUST be displayed: Pending, Approved, Rejected.
- **FR-USER-003**: Users MUST be authenticated to submit/edit workshops or reviews.
- **FR-USER-004**: Authenticated session MUST persist via SharedPreferences.

#### Rating System
- **FR-RATE-001**: Users can rate 1-5 stars with comments. One review per user per workshop.
- **FR-RATE-002**: Workshops MUST store aggregated `rating_avg` and `rating_count`.

#### Monetization
- **FR-ADS-001**: Integrate AdMob Banner Ads on Workshop List and Detail pages.
- **FR-ADS-002**: DO NOT show ads on Map, Login, or navigation flows.

---

### Key Entities

- **Workshop**: `id`, `name`, `latitude`, `longitude`, `phone`, `address`, `rating_avg`, `rating_count`, `verified` (boolean).
- **WorkshopPhoto**: `id`, `workshop_id`, `image_url`, `created_at`.
- **Review**: `id`, `workshop_id`, `user_id`, `rating`, `comment`, `created_at`.
- **UserSession**: `user_id`, `email`, `auth_token`.

---

## Success Criteria *(mandatory)*

- **SC-001**: APK size remains small despite feature increases.
- **SC-002**: Image loading via Glide is smooth and cached.
- **SC-003**: Map remains responsive while loading markers by bounds.
- **SC-004**: All UI elements strictly follow the `#DA70D6` theme.
- **SC-005**: Redirect after login works correctly to the intended destination.

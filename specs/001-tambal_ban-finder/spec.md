# Feature Specification: TambalBan Finder

> **SUPERSEDED SCHEMA:** this spec references the retired `workshops` /
> `workshop_submissions` tables. The live shared table is `tambal_ban` — see
> [`017-workshop-schema-update`](../017-workshop-schema-update/spec.md) and
> [`../../supabase_schema.sql`](../../supabase_schema.sql).

**Feature Branch**: `001-tambal_ban-finder`
**Created**: 2026-03-12 (Updated: 2026-03-13)
**Status**: Draft
**Input**: User description: "Build a complete Android mobile application using Kotlin and XML layouts. Application Name: TambalBan Finder. Purpose: Help drivers quickly find the nearest tire repair shop during emergency situations. The app must run well on low-spec devices (2GB RAM) and keep the build file as small as possible by using fewer dependencies and built-in features."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Find Nearest Workshop (Priority: P1)

As a driver with a flat tire, I want to immediately see the nearest tire repair shops on a map so I can get help quickly.

**Why this priority**: This is the core value proposition of the app. Without finding a workshop, the app has no purpose.

**Independent Test**: Can be fully tested by opening the app and verifying that markers appear around the user's current location on the map.

**Acceptance Scenarios**:

1. **Given** the app is open and GPS is enabled, **When** the map loads, **Then** I should see my current location and markers for nearby workshops.
2. **Given** I am looking at the map, **When** I move the map to a different area, **Then** new workshop markers for that area should load.
3. **Given** many workshops in an area, **When** I zoom out, **Then** markers should cluster to maintain map readability.

---

### User Story 2 - Emergency Help (Priority: P1)

As a driver in a high-stress emergency situation, I want a single-tap way to find the absolute closest workshop within 3km.

**Why this priority**: Emergency situations require minimal interaction. This feature differentiates the app from a general map search.

**Independent Test**: Can be fully tested by tapping the emergency button and verifying it identifies and displays the closest workshop within 3km immediately.

**Acceptance Scenarios**:

1. **Given** I am on the home screen, **When** I press the Floating Emergency Button, **Then** the app should immediately identify the closest workshop within 3km and show its details.
2. **Given** no workshops are within 3km, **When** I press the Emergency Button, **Then** I should be notified that no immediate help was found within the emergency radius.

---

### User Story 3 - Contact and Navigate to Workshop (Priority: P1)

As a driver who found a workshop, I want to call them or get driving directions so I can actually get my tire fixed.

**Why this priority**: Finding a workshop is useless if the user cannot reach them or contact them.

**Independent Test**: Can be tested by tapping a workshop marker and using the "Call" or "Navigate" buttons.

**Acceptance Scenarios**:

1. **Given** a workshop detail view is open, **When** I tap the "Call" button, **Then** the device's dialer should open with the workshop's phone number.
2. **Given** a workshop detail view is open, **When** I tap the "Navigate" button, **Then** external navigation apps (Google Maps, Waze) should be offered to start directions.

---

### User Story 4 - Search with Radius Filter (Priority: P2)

As a driver, I want to find workshops within a specific distance (1km, 3km, 5km) so I can choose the best option based on my situation.

**Why this priority**: Provides user control over the search results beyond just the "nearest" one.

**Independent Test**: Can be tested by selecting different radius filters and verifying the list of workshops changes accordingly.

**Acceptance Scenarios**:

1. **Given** the search screen, **When** I select a "3km" filter, **Then** only workshops within that radius should be listed or highlighted.

---

### User Story 5 - contribute New Workshop (Priority: P3)

As a helpful citizen, I want to add a new tire repair shop I found so other drivers can benefit from accurate data.

**Why this priority**: User-generated content helps keep the database up-to-date and comprehensive.

**Independent Test**: Can be tested by submitting the "Add Workshop" form and verifying the data is sent to the backend.

**Acceptance Scenarios**:

1. **Given** the "Add Workshop" form, **When** I fill in the name, phone, address, and location and tap "Submit", **Then** the submission should be recorded in the system.

---

### User Story 6 - Run on Low-Spec Hardware (Priority: P1)

As a user with a budget smartphone (2GB RAM), I want the app to remain responsive and fast so I can get help without my phone freezing.

**Why this priority**: Ensuring accessibility for users with lower-end devices expands the safety net the app provides.

**Independent Test**: Can be tested by running the app on a device or emulator with 2GB of RAM and limited processing power.

**Acceptance Scenarios**:

1. **Given** a device with 2GB RAM, **When** the app is running and markers are being clustered, **Then** the map interaction should remain fluid without crashes.
2. **Given** limited storage, **When** the app is installed, **Then** it should occupy minimal space compared to similar map-based applications.

---

### Edge Cases

- **No GPS Permission**: The app should prompt for permission or allow manual location fallback.
- **No Internet Connectivity**: The app should load cached workshop data from the local database.
- **10,000+ Markers**: The system should use clustering and viewport-based loading to prevent performance lag.
- **Low Memory Warning**: System MUST handle low memory signals gracefully without data loss.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST display an interactive map using OpenStreetMap.
- **FR-002**: System MUST detect user GPS location automatically.
- **FR-003**: System MUST load workshop markers from a remote datastore based on the current map viewport.
- **FR-004**: System MUST cluster markers when zoomed out to maintain performance.
- **FR-005**: System MUST provide a "Nearby Search" feature using distance calculation.
- **FR-006**: System MUST allow users to filter nearby workshops by radius (1km, 3km, 5km).
- **FR-007**: System MUST display workshop details including contact info and ratings.
- **FR-008**: System MUST trigger external navigation apps via system intents.
- **FR-009**: System MUST allow users to submit new workshop data.
- **FR-010**: System MUST cache workshop data for offline access.
- **FR-011**: System MUST include a high-visibility Emergency Button.
- **FR-012**: System MUST integrate non-intrusive monetization that does not interfere with emergency actions.
- **FR-013**: System MUST minimize external library dependencies to reduce APK size.
- **FR-014**: System MUST prefer system-native capabilities over third-party libraries for networking, JSON parsing, and location when feasible.
- **FR-015**: System MUST be optimized for low-memory environments (2GB RAM).

### Key Entities *(include if feature involves data)*

- **Workshop**: Name, location, contact, hours, rating.
- **Review**: User rating and comment.
- **User**: Profile info.
- **Workshop Submission**: New data awaiting verification.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Critical data (nearest workshop) visible within 5 seconds of cold start.
- **SC-002**: Map interaction remains fluid on 2GB RAM devices.
- **SC-003**: Emergency action (finding closest) completes in under 2 seconds.
- **SC-004**: Offline data access is instant (under 1 second).
- **SC-005**: APK size is significantly smaller than comparable mapping apps (target < 5MB excluding map tiles).
- **SC-006**: 0% crash rate related to OutOfMemory (OOM) on target low-spec devices.

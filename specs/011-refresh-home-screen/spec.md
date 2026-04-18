# Feature Specification: Refresh Home Screen Design

**Feature Branch**: `011-refresh-home-screen`  
**Created**: 2026-04-18  
**Status**: Draft  
**Input**: User description: "update home screen jadi seperti di gambar"

## Clarifications

### Session 2026-04-18
- **Q: Bottom Sheet "Fit to Content" behavior?** → **A: Fully Collapsible (Option C)**: Sheet is hidden if 0 items, and grows/shrinks exactly to the number of items found.
- **Q: Floating Center Button behavior?** → **A: Snap (Option C)**: Instantly jump to user location with no animation.
- **Q: How to make map "more comfortable"?** → **A: Reduce contrast and increase brightness**.
- **Q: Initial Map Centering behavior?** → **A: World View (Option C)**: Zoom out to a city/regional level until the specific user point is found.
- **Q: Search Results presentation?** → **A: Overlay List (Option C)**: Show a result list directly under the search bar while typing.
- **Q: Search Trigger logic?** → **A: Hybrid (A+C)**: Debounced suggestions while typing (500ms) + full search on submit.
- **Q: Tap search result action?** → **A: Center & Select (Option A)**: Snap map to workshop and show marker.

## User Scenarios & Testing *(mandatory)*

### User Story 4 - Dynamic Search Suggestions (Priority: P1)

As a user in need of a specific workshop, I want to see suggestions as I type in the search bar so that I can quickly find the right place without typing the full name.

**Acceptance Scenarios**:

1. **Given** the user is typing in the search bar, **When** they pause for 500ms and have entered at least 3 characters, **Then** an overlay list of matching workshops should appear below the bar.
2. **Given** the suggestion list is visible, **When** the user taps a result, **Then** the list should close, and the map should snap instantly to that workshop's location.
3. **Given** a query is entered, **When** the user presses "Search" on the keyboard, **Then** a full search across the entire database should be performed, updating both the map and the bottom sheet.

## User Scenarios & Testing *(mandatory)* (legacy)
...

### User Story 1 - Modernized Map Navigation (Priority: P1)

As a user looking for help, I want a clean and visually appealing map interface so that I can easily identify nearby workshops without visual clutter.

**Why this priority**: The map is the primary interface for the "Finder" utility. High-fidelity visuals are critical for the project's "Soft-Editorial Minimalism" goal.

**Acceptance Scenarios**:

1. **Given** the user is on the Home screen, **When** the map loads, **Then** it must display a stylized light-teal background with purple (orchid) workshop markers, using low contrast and high brightness for eye comfort.
2. **Given** the user is navigating the map, **When** they tap the "My Location" FAB (Bottom Right), **Then** the map must snap instantly to their current coordinates.
3. **Given** the app is launched, **When** GPS is not yet acquired, **Then** the map should show a zoomed-out regional view until a location lock is achieved.

---

### User Story 2 - Floating Global Search (Priority: P2)

As a user, I want a prominent floating search bar at the top of the map so that I can quickly find specific workshops or locations.

**Acceptance Scenarios**:

1. **Given** the user is on the Home screen, **When** viewing the top area, **Then** a pill-shaped floating search bar should be visible with a circular profile avatar.
2. **Given** the search bar is visible, **When** the user taps it, **Then** it should transition to a search state.

---

### User Story 3 - Nearby Workshops Bottom Sheet (Priority: P1)

As a user, I want a persistent but non-intrusive sheet at the bottom of the map so that I can quickly browse nearby workshops in a list format.

**Acceptance Scenarios**:

1. **Given** the Home screen is open, **When** viewed, **Then** the bottom sheet MUST have a pure white background and its height MUST fit the content found (up to a reasonable max height).
2. **Given** no workshops are found nearby, **When** the results are updated, **Then** the bottom sheet should be hidden (fully collapsed).
3. **Given** 1-3 workshops are found, **When** the sheet is displayed, **Then** its height should adjust automatically to wrap the list items exactly.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST render a custom-styled map with light-teal tile colors, orchid-accented UI elements, and a "Soft-Comfort" color matrix (low contrast, high brightness).
- **FR-002**: System MUST display a floating, pill-shaped search bar at the top of the map.
- **FR-003**: System MUST implement a "My Location" Floating Action Button (FAB) in the bottom right corner that snaps the map to user coordinates.
- **FR-007**: System MUST implement a white-background Bottom Sheet that dynamically adjusts its height based on the number of workshops found ("Fit to Content").
- **FR-008**: System MUST hide the bottom sheet entirely if no workshops are returned in the current map radius.
- **FR-009**: System MUST implement a debounced (500ms) search suggestion overlay that displays matching workshop names.
- **FR-010**: Selecting a search suggestion MUST snap the map to the workshop and trigger a marker selection event.

### Key Entities *(include if feature involves data)*

- **Workshop**: Represents a service provider. Attributes: `Name`, `Image`, `Location (Lat/Long)`, `Rating`, `Status (Open/Closed)`, `Address`.
- **Search Query**: Represents the user's intent to find a specific entity or location.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can identify the nearest open workshop in under 5 seconds from app launch.
- **SC-002**: The bottom sheet transitions between peek and expanded states with no frame drops (smooth 60fps).
- **SC-003**: 95% of beta users report that the new "Nearby Workshops" cards provide critical information (status/distance) more clearly than the legacy design.
- **SC-004**: Search bar accessibility: magnifying glass and profile avatar meet minimum 44x44dp tap target requirements.

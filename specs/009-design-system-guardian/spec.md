# Feature Specification: Design System: The Responsive Guardian

**Feature Branch**: `009-design-system-guardian`  
**Created**: 2026-04-11  
**Status**: Draft  
**Input**: User description provided in request.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Find Emergency Repair (Priority: P1)

As a driver with a flat tire on a busy road, I want to find the nearest repair shop quickly using a calm and authoritative interface, so I can resolve my situation with minimal stress.

**Why this priority**: This is the core purpose of the app. In an emergency, the user's cognitive load is high, and the design must minimize friction.

**Independent Test**: Can be tested by navigating the main map and list views without seeing a single 1px border, ensuring tonal shifts provide clear sectioning.

**Acceptance Scenarios**:

1. **Given** the user is on the map screen, **When** they look at the interface, **Then** they see high-contrast Orchid elements guiding their eye to the "Find Nearest Repair" action.
2. **Given** the user is in a panic, **When** they interact with the app, **Then** all touch targets are at least 56dp, ensuring no missed taps.

---

### User Story 2 - Select a Repair Shop (Priority: P2)

As a user looking for options, I want to see a list of available repair shops separated by spacing and tonal shifts rather than lines, so the interface feels clean and editorial.

**Why this priority**: Provides the primary decision-making interface for the user.

**Independent Test**: Can be tested by scrolling through the repair shop list and verifying that shops are separated by 1.5rem (md) spacing and subtle background color shifts.

**Acceptance Scenarios**:

1. **Given** a list of repair shops, **When** the user scrolls, **Then** they see "Emergency Chips" in tertiary green for "Open Now" or "Mobile Repair."
2. **Given** a shop card, **When** the user views it, **Then** the distance (e.g., "1.2 km") is displayed as the primary hero element using `display-sm` typography.

---

### User Story 3 - Track Live Progress (Priority: P3)

As a user waiting for help, I want to see a "Live-Status" drawer sliding over the map with a glassmorphism effect, so I can track my rescue in real-time while maintaining spatial awareness.

**Why this priority**: Provides reassurance and prevents the user from feeling "lost" while waiting.

**Independent Test**: Can be tested by opening the bottom drawer and verifying map visibility through the 85% opaque blurred background.

**Acceptance Scenarios**:

1. **Given** help is on the way, **When** the "Live-Status" drawer is visible, **Then** it has a 2rem (lg) top-corner radius and allows map colors to bleed through subtly.
2. **Given** the status drawer is active, **When** the user reads the status, **Then** it uses Plus Jakarta Sans `headline-lg` for the "Help is 5 mins away" update.

---

### Edge Cases

- **Direct Sunlight Visibility**: What happens when the user is outside in bright light? The system must use high-contrast Orchid and `on_surface` (#191c1d) text (not pure black) to maintain legibility.
- **Disabled Actions**: How does the system handle disabled buttons? It must use `primary_fixed_dim` instead of generic grey to maintain the "brand soul."
- **Accessibility with Tonal Layering**: How does a visually impaired user distinguish sections without borders? Tonal shifts between `surface` and `surface_container_low` must meet minimum contrast requirements.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001: Tonal Layering Architecture**: The system MUST implement structural integrity through background color shifts (`surface`, `surface_container_low`, `surface_container_highest`), explicitly prohibiting 1px solid borders for sectioning.
- **FR-002: Orchid-Centric Palette**: The system MUST use `#973497` as the `primary` color and `#DA70D6` as the `primary_container` for critical "Emergency Orchid" beacons.
- **FR-003: Editorial Typography Pairing**: The system MUST use **Plus Jakarta Sans** for headlines/display and **Inter** for body/utility text, strictly following the defined scale (e.g., `display-lg` for status).
- **FR-004: Hyper-Rounded Geometry**: All main action buttons MUST use an `xl` (3rem) corner radius (pill shape). Input fields MUST also use pill shapes.
- **FR-005: Large-Target Interactive Elements**: ALL interactive elements (buttons, chips, inputs) MUST have a minimum height of 56dp.
- **FR-006: Flat Glass**: Floating action buttons (FABs) and map overlays utilize a semi-transparent effect (`surface_container_lowest` at 85% opacity) and high elevation (`8dp+`).
- **FR-007: Ambient Elevation**: The system MUST use diffused ambient shadows (`Y: 8, Blur: 24, Color: on_surface @ 6%`) for floating elements instead of standard Material shadows.
- **FR-008: Gradient SOS Actions**: Primary "SOS" or "Request Help" buttons MUST use a 45-degree linear gradient from `primary` to `primary_container`.

### Key Entities *(include if feature involves data)*

- **Color Palette**: Collection of hex codes and opacity levels mapped to semantic tokens (`primary`, `surface`, etc.).
- **Typography Scale**: Predefined weights, sizes, and kerning rules for Plus Jakarta Sans and Inter font families.
- **Component Library**: Suite of reusable UI elements (Buttons, Chips, Inputs, Drawers) adhering to the "The Responsive Guardian" design rules.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% adherence to the "No-Line" rule (zero 1px borders used for sectioning).
- **SC-002**: 100% of interactive elements meet the minimum 56dp height requirement.
- **SC-003**: 95% of users report the interface feels "calm" or "reliable" in simulated emergency testing (qualitative).
- **SC-004**: Success rate of "SOS" initiation is 100% on first attempt in usability testing due to high-contrast Orchid beacons.
- **SC-005**: Contrast ratio between all adjacent tonal layers (e.g., `surface` vs `surface_container_low`) meets WCAG 2.1 AA standards for meaningful boundaries.
